package com.gdg.backend.domain.build.controller;

import com.gdg.backend.domain.build.dto.BuildRequest;
import com.gdg.backend.domain.build.dto.InputMessage;
import com.gdg.backend.domain.build.service.CodeExecutionService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class BuildController {

    private final CodeExecutionService codeExecutionService;

    // 클라이언트가 코드 실행 요청을 보냈을 때
    @MessageMapping("/compile")
    public void compileCode(BuildRequest request) {
        // Docker 컨테이너에서 실행
        codeExecutionService.runCode(request);
    }

    // 클라이언트가 실행 도중 입력을 보냈을 때
    @MessageMapping("/input")
    public void handleInput(InputMessage inputMessage) {
        codeExecutionService.sendInput(inputMessage.getSessionId(), inputMessage.getInput());
    }
}