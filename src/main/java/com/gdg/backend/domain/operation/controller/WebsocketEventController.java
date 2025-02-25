package com.gdg.backend.domain.operation.controller;

import com.gdg.backend.domain.operation.dto.OperationRequestDto;
import jakarta.validation.Valid;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.concurrent.BlockingQueue;

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
    public void receiveEditOperation(@Valid OperationRequestDto operation) throws InterruptedException {
        operationQueue.put(operation);
        template.convertAndSend("/sub/ack/" + operation.getDocumentId(), "ACK");
    }


}
