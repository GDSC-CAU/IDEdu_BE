package com.gdg.backend.domain.helprequest.controller;

import com.gdg.backend.domain.helprequest.dto.HelpRequestDto;
import com.gdg.backend.domain.helprequest.service.HelpRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HelpRequestController {
    private final HelpRequestService helpRequestService;

    @MessageMapping("/help/{classroomId}")
    public void handleHelpRequest(
            @Valid HelpRequestDto request,
            @DestinationVariable("classroomId") Long classroomId
    ) {
        log.info("help request from student id {} : (classroomId={})", request.getUserId(), classroomId);
        helpRequestService.handleHelpRequest(classroomId, request);
    }
}
