package com.gdg.backend.domain.event.controller;

import com.gdg.backend.domain.event.dto.DocumentOperationRequestDto;
import com.gdg.backend.domain.event.dto.DocumentOperationResponseDto;
import jakarta.validation.Valid;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.concurrent.BlockingQueue;

@Controller
public class WebsocketEventController {
    private final BlockingQueue<DocumentOperationRequestDto> operationQueue;
    private final SimpMessagingTemplate template;

    public WebsocketEventController(
            BlockingQueue<DocumentOperationRequestDto> operationQueue,
            SimpMessagingTemplate template
    ){
        this.operationQueue = operationQueue;
        this.template = template;
    }

    /** 클라이언트의 문서 편집 요청을 메시지 큐에 push */
    @MessageMapping("/edit")
    public void receiveEditOperation(@Valid DocumentOperationRequestDto operation) throws InterruptedException {
        operationQueue.put(operation);
        template.convertAndSend("/sub/ack/" + operation.getDocumentId(), "ACK");
    }


}
