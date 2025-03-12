package com.gdg.backend.domain.operation.service;

import com.gdg.backend.common.exception.handler.GeneralHandler;
import com.gdg.backend.common.response.status.ErrorCode;
import com.gdg.backend.common.annotation.TrackExecutionTime;
import com.gdg.backend.domain.document.entity.Document;
import com.gdg.backend.domain.document.repository.DocumentRepository;
import com.gdg.backend.domain.enums.OperationType;
import com.gdg.backend.domain.operation.dto.OperationRequestDto;
import com.gdg.backend.domain.operation.dto.OperationResponseDto;
import com.gdg.backend.domain.operation.entity.Operation;
import com.gdg.backend.domain.operation.repository.OperationRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;


/** OperationType 큐에서 주기적으로 이벤트를 가져와 처리하는 클래스 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OperationQueueProcessor {

    private final DocumentRepository documentRepository;
    private final OperationRepository operationRepository;
    private final BlockingQueue<OperationRequestDto> operationQueue;
    private final SimpMessagingTemplate template;
    private final ConcurrentHashMap<Long, AtomicLong> documentVersions = new ConcurrentHashMap<>();

    // 문서 상태 캐싱
    // - todo 문서 많아지면 OutOfMemory 발생 가능 -> 추후에 LRU나 TTL 설정
    private final ConcurrentHashMap<Long, Document> documentCache = new ConcurrentHashMap<>();

    private final Set<Long> dirtyDocuments = new HashSet<>(); // 변경된 Document 추적 (주기적으로 저장)

    @PostConstruct
    public void startProcessing() {
        // 별도 스레드에서 큐를 polling 하여 처리
        new Thread(() -> {
            while(true) {
                try {
                    OperationRequestDto operation = operationQueue.take();
                    processOperation(operation);
                } catch (InterruptedException e) {
                    log.error("QUEUE PROCESSOR THREAD INTERRUPTED: {}", e.getMessage(), e);
                    break;
                } catch (Exception e) {
                    log.error("QUEUE PROCESSOR UNCAUGHT EXCEPTION: {}", e.getMessage(), e);
                }
            }
        }).start();
    }

    @PostConstruct
    public void postConstructJob() {
        createTestDocument();
        fillDocumentPool();
        fillDocumentVersionPool();
    }

    /** (임시) 테스트 문서 초기화 -> 다른 PostConstruct 메소드보다 먼저 호출되어야 함 */
    private void createTestDocument() {
        try {
            final Long TEST_DOC_ID = 1L;
            Document testDoc = documentRepository.findById(TEST_DOC_ID)
                    .orElse(Document.builder()
                            .id(1L)
                            .build());
            testDoc.setVersion(0L);
            testDoc.setContent("");
            documentRepository.save(testDoc);
            // (임시) 테스트 문서 operation 로그 초기화
            operationRepository.deleteByDocumentId(TEST_DOC_ID);

            log.info("CREATED TEST DOCUMENT: {}", testDoc);
        } catch (Exception e) {
            log.error("Exception while creating test document: {}", e.getMessage());
        }
    }

    /** DB에서 Document fetch해서 메모리로 가져옴 */
//    @PostConstruct
    public void fillDocumentPool() {
        List<Document> documents = documentRepository.findAll();
        for(Document doc : documents) {
            documentCache.put(doc.getId(), doc);
        }
        log.info("FILLED DOCUMENT POOL : {}", documentCache);
    }

    /** DB에 존재하는 Document version pool 추적 (인메모리라서 서버 껐다키면 사라지니까..) */
//    @PostConstruct
    public void fillDocumentVersionPool () {
        List<Document> documents = documentRepository.findAll();
        documents.stream().forEach(document -> {
            if(document.getVersion() > documentVersions.getOrDefault(document.getId(), new AtomicLong(-1)).get())
                documentVersions.put(document.getId(), new AtomicLong(document.getVersion()));
        });
        log.info("FILLED DOCUMENT POOL: {}", documentVersions);
    }

    @TrackExecutionTime
    public void processOperation(OperationRequestDto operation) {
        Long docId = operation.getDocumentId();
        Long baseVersion = operation.getBaseVersion();
        Long opPosition = operation.getPosition();

        // documentID 없는 경우 예외처리 (캐시엔 없지만 DB에 있는 경우 캐시 업데이트)
        Document doc = documentCache.computeIfAbsent(docId, id -> documentRepository.findById(docId)
                .orElseThrow(() -> new GeneralHandler(ErrorCode.DOCUMENT_NOT_FOUND))
        );

        // SYNC인 경우 따로 처리
        // - 현재 문서 상태 브로드캐스팅
        // - version을 높이지 않음
        // - 추후 전략 패턴 등으로 추상화
        if(operation.getOperation().equals(OperationType.SYNC)) {
            log.info("Received: SYNC");
            String docContent = doc.getContentBuilder().toString();
            template.convertAndSend("/sub/edit/" + docId, docContent);
            return;
        }

        // operation 충돌 시 변환 처리
        // - operation의 baseVersion과 서버가 추적하는 version을 비교
        //   - 차이나는 version만큼 position을 업데이트한다 (insert: position 증가 / delete: position 감소)
        // - 이전 version의 이벤트 추적 방법
        // - (1) DB에서 가져온다 -> 구현이 쉬우니까 일단 이걸로 감
        //   - 대신 DB 가져오는 시간이 너무 오래 걸릴 거임
        // - (2) 메모리에 킵한다 -> 얼마나 킵할지 알 수 없음 (전부 킵하면 결국 OutOfMemory 뜰거임)
        //   - 연결된 클라들이 어느 version까지 받았는지 추적하면 메모리 할당량 조절 가능
        //   - 클라가 전부 version 11까지는 받았다 -> version 10 이상은 메모리에서 해제
        //   - queue로 구현해서, 클라이언트 ACK 받을 시 queue에서 옛날 event pop / 새로운 event 받을 시 queue에 push
        //   -> 클라이언트 ACK 추적 기능 구현 되면 (2)번으로 갈아타기
        try {
            // 로그 출력
            log.info("Received: {}", operation);
            List<Operation> concurrentOperations = operationRepository.findByDocumentIdAndVersionGreaterThan(docId, baseVersion);
            for (Operation concurrentOp : concurrentOperations) {
                if (concurrentOp.getOperation().equals(OperationType.INSERT) && concurrentOp.getPosition() < opPosition) {
                    // 현재 operation보다 앞에 삽입한 경우
                    if(concurrentOp.getInsertContent() == null) continue;;
                    opPosition += concurrentOp.getInsertContent().length();
                } else if (concurrentOp.getOperation().equals(OperationType.DELETE) && concurrentOp.getPosition() < opPosition) {
                    // 현재 operation보다 앞을 삭제한 경우
                    if(concurrentOp.getDeleteLength() == null) continue;;
                    opPosition -= concurrentOp.getDeleteLength();
                }
            }

            // 버전 부여
            OperationResponseDto response = OperationResponseDto.of(operation);
            response.setPosition(opPosition);
            response.setVersion(documentVersions.get(operation.getDocumentId()).incrementAndGet());

            // 문서 상태 갱신
            int idx = Math.toIntExact(opPosition);
            switch(operation.getOperation()) {
                case INSERT -> doc.getContentBuilder().insert(idx, operation.getInsertContent());
                case DELETE -> doc.getContentBuilder().delete(idx - operation.getDeleteLength(), idx);
            }
            log.info("current content: {}", doc.getContentBuilder().toString());
            dirtyDocuments.add(docId);

            // Operation DB에 저장 && Document version 업데이트
            // - 동기 처리 vs 비동기 처리
            // - todo 메모리에 Operation 캐싱하기
            //   - Operation 큐 만들어서 캐싱하기 (클라이언트 ACK에 맞춰 갱신)
            operationRepository.save(Operation.builder()
                    .operation(response.getOperation())
                    .document(doc)
                    .position(response.getPosition())
                    .insertContent(response.getInsertContent())
                    .deleteLength(response.getDeleteLength())
                    .version(response.getVersion())
                    .member(null) // todo
                    .build()
            );

            // 로그 출력
           log.info("  수정된 Operation: {}", response);

            // 클라이언트에 브로드캐스트
            template.convertAndSend("/sub/edit/" + docId, response);
        } catch (Exception e) {
            log.error("Exception while handling operation {} -> {}", operation, e.getMessage(), e);
        }
    }

    /** 주기적으로 변경된 문서 저장 <br>
     * - DB 쓰기는 네트워크 요청 + 디스크 I/O를 포함하므로 무거움
     * - processOperation에서 제거해서 실행시간 줄여서 사용자 경험 증가 & 트랜잭션 부하 감소
     * - 실시간성은 documentCache로 유지하고 저장은 백그라운드에서 주기적으로 진행
     * */
    @Scheduled(fixedRate = 10000) // 10초마다 실행
    @Transactional
    public void saveDirtyDocuments() {
        // 로그 출력
        log.info("SAVING DIRTY DOCUMENTS (id=" + dirtyDocuments + ")");
        for (Long docId : dirtyDocuments) {
            Document doc = documentCache.get(docId);
            if (doc != null) {
                synchronized (doc) {
                    log.info(" - DOCUMENT {}: {}", docId, doc.getContentBuilder().toString());
                    doc.syncContentBuilder();
                    documentRepository.save(doc);
                }
            }
        }
        dirtyDocuments.clear();
    }
}
