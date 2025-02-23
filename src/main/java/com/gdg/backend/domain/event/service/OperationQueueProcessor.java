package com.gdg.backend.domain.event.service;

import com.gdg.backend.domain.document.entity.Document;
import com.gdg.backend.domain.document.repository.DocumentRepository;
import com.gdg.backend.domain.event.dto.DocumentOperationRequestDto;
import com.gdg.backend.domain.event.dto.DocumentOperationResponseDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;


/** DocumentOperation 큐에서 주기적으로 이벤트를 가져와 처리하는 클래스 */
@Component
@RequiredArgsConstructor
public class OperationQueueProcessor {

    private final DocumentRepository documentRepository;
    private final BlockingQueue<DocumentOperationRequestDto> operationQueue;
    private final SimpMessagingTemplate template;
    private final ConcurrentHashMap<Long, AtomicLong> documentVersions = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, StringBuilder> documents = new ConcurrentHashMap<>();

    @PostConstruct
    public void startProcessing() {
        // 별도 스레드에서 큐를 polling 하여 처리
        new Thread(() -> {
            while(true) {
                try {
                    DocumentOperationRequestDto operation = operationQueue.take();
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

    private void processOperation(DocumentOperationRequestDto operation) {
        // TODO OT 알고리즘 적용
        // TODO documentID 없는 경우 예외 처리

        // 버전 부여
        DocumentOperationResponseDto response = new DocumentOperationResponseDto();
        response.setVersion(documentVersions.get(operation.getDocumentId()).incrementAndGet());

        // todo 서버에도 변경사항 가함

        // Operation DB에 저장할지 말지??

        // 클라이언트에 브로드캐스트
        Long docId = operation.getDocumentId();
        template.convertAndSend("/sub/document/" + docId, response);
    }
}
