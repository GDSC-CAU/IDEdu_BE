package com.gdg.backend.domain.operation.service;

import com.gdg.backend.domain.document.entity.Document;
import com.gdg.backend.domain.document.repository.DocumentRepository;
import com.gdg.backend.domain.operation.dto.OperationRequestDto;
import com.gdg.backend.domain.operation.dto.OperationResponseDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;


/** OperationType 큐에서 주기적으로 이벤트를 가져와 처리하는 클래스 */
@Component
@RequiredArgsConstructor
public class OperationQueueProcessor {

    private final DocumentRepository documentRepository;
    private final BlockingQueue<OperationRequestDto> operationQueue;
    private final SimpMessagingTemplate template;
    private final ConcurrentHashMap<Long, AtomicLong> documentVersions = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, StringBuilder> documents = new ConcurrentHashMap<>();

    @PostConstruct
    public void startProcessing() {
        // 별도 스레드에서 큐를 polling 하여 처리
        new Thread(() -> {
            while(true) {
                try {
                    OperationRequestDto operation = operationQueue.take();
                    processOperation(operation);
                } catch (InterruptedException e) {
                    System.out.println("QUEUE PROCESSOR THREAD INTERRUPTED!");
                    System.out.println(e.getMessage());
                    break;
                }
            }
        }).start();
    }

    /** DB에 존재하는 Document version pool 추적 (인메모리라서 서버 껐다키면 사라지니까..) */
    @PostConstruct
    public void fillDocumentVersionPool () {
        List<Document> documents = documentRepository.findAll();
        documents.stream().forEach(document -> {
            documentVersions.put(document.getId(), new AtomicLong(document.getVersion()));
        });
    }

    private void processOperation(OperationRequestDto operation) {
        // todo documentID 없는 경우 예외 처리

        // todo OT 알고리즘 적용
        // 어떻게 할까
        // - operation의 version과 서버가 추적하는 version을 비교
        // - 차이나는 version만큼 position을 업데이트한다 (insert: position 증가 / delete: position 감소)
        // - 이전 version의 이벤트를 어떻게 추적할까?
        // - (1) DB에서 가져온다 -> 구현이 쉬우니까 일단 이걸로 감
        //   - 대신 DB 가져오는 시간이 너무 오래 걸릴 거임
        // - (2) 메모리에 킵한다 -> 얼마나 킵할지 알 수 없음 (전부 킵하면 결국 OutOfMemory 뜰거임)
        //   - 연결된 클라들이 어느 version까지 받았는지 추적할 수 있으면 메모리 할당량 조절 가능
        //   - 클라가 전부 version 11까지는 받았다 -> version 10 이상은 메모리에서 해제
        //   - queue로 구현해서, 클라이언트 ACK 받을 시 queue에서 옛날 event pop / 새로운 event 받을 시 queue에 push
        //   -> 클라이언트 ACK 추적 기능 구현 되면 (2)번으로 갈아타기

        // List<OperationType> concurrentOperations =


        // 버전 부여
        OperationResponseDto response = OperationResponseDto.of(operation);
        response.setVersion(documentVersions.get(operation.getDocumentId()).incrementAndGet());

        // todo 서버 문서 상태에도 변경사항 가함

        // todo OperationType DB에 저장 && Document version 업데이트
        // - 동기 처리 vs 비동기 처리

        // 클라이언트에 브로드캐스트
        Long docId = operation.getDocumentId();
        template.convertAndSend("/sub/edit/" + docId, response);
    }
}
