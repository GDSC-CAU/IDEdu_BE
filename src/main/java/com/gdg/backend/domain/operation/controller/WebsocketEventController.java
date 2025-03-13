package com.gdg.backend.domain.operation.controller;

import com.gdg.backend.domain.operation.dto.OperationRequestDto;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.concurrent.BlockingQueue;

@Slf4j
@Controller
public class WebsocketEventController {
    private final BlockingQueue<OperationRequestDto> operationQueue;
    private final SimpMessagingTemplate template;

    public WebsocketEventController(
            BlockingQueue<OperationRequestDto> operationQueue,
            SimpMessagingTemplate template
    ){
        this.operationQueue = operationQueue;
        this.template = template;
    }

    /** 클라이언트의 문서 편집 요청을 메시지 큐에 push */
    @MessageMapping("/edit")
    public void handleEditOperation(@Valid OperationRequestDto operation) throws InterruptedException {
        operationQueue.put(operation);
        log.info(operation + " put to queue");
        template.convertAndSend("/sub/ack/" + operation.getDocumentId(), "ACK");
    }
}
