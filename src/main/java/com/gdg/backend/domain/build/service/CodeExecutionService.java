package com.gdg.backend.domain.build.service;

import com.gdg.backend.domain.build.dto.BuildRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class CodeExecutionService {

    private final SimpMessagingTemplate messagingTemplate;
    // 세션별 실행 프로세스를 저장하는 맵
    private final Map<String, Process> processMap = new ConcurrentHashMap<>();

    public String runCode(BuildRequest request) {

        log.info("입력 받은 코드 : {}", request.getCode());

        // 서버에서 UUID로 세션 ID 생성
        String sessionId = UUID.randomUUID().toString();
        messagingTemplate.convertAndSend("/sub/session", sessionId);

        try {
            // 1. 임시 디렉토리 생성 및 코드 파일 저장
            Path tempDir = Files.createTempDirectory("code");
            String language = request.getLanguage().toLowerCase();
            String fileName;
            String imageName;
            String command;

            switch (language) {
                case "java":
                    log.info("자바 실행");
                    fileName = "Main.java";
                    imageName = "openjdk:11";
                    command = "javac Main.java && java Main";
                    break;
                case "c":
                    log.info("C 실행");
                    fileName = "code.c";
                    imageName = "gcc:latest";
                    command = "gcc code.c -o code && ./code";
                    break;
                case "python":
                    log.info("python 실행");
                    fileName = "script.py";
                    imageName = "python:3.8";
                    command = "python script.py";
                    break;
                default:
                    throw new IllegalArgumentException("지원하지 않는 언어입니다.");
            }

            Path codeFile = tempDir.resolve(fileName);
            Files.write(codeFile, request.getCode().getBytes());

            // 2. Docker 컨테이너 실행 (인터랙티브 모드)
            List<String> cmd = List.of(
                    "docker", "run", "--rm", "-i",
                    "-v", tempDir.toAbsolutePath() + ":/workspace",
                    "-w", "/workspace", imageName,
                    "bash", "-c", command
            );

            ProcessBuilder pb = new ProcessBuilder(cmd);
            Process process = pb.start();

            // 3. 생성된 세션 ID로 프로세스를 매핑
            processMap.put(sessionId, process);

            // 4. 실행 결과를 바로 클라이언트에 스트리밍
            new Thread(() -> streamOutput(sessionId, process)).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
        // 생성된 세션 ID를 반환하여 클라이언트가 이후 통신에 사용할 수 있게 함
        return sessionId;
    }

    private void streamOutput(String sessionId, Process process) {
        log.info("세션 id : {}", sessionId);

        try (
                BufferedReader stdOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
                BufferedReader stdErr = new BufferedReader(new InputStreamReader(process.getErrorStream()))
        ) {
            String line;

            // 표준 출력(STDOUT) 스트리밍
            while ((line = stdOut.readLine()) != null) {
                messagingTemplate.convertAndSend("/sub/output/" + sessionId, line);  // 실시간으로 바로 전송
            }

            // 표준 에러(STDERR) 스트리밍 (컴파일 에러 등)
            while ((line = stdErr.readLine()) != null) {
                messagingTemplate.convertAndSend("/sub/output/" + sessionId, "[ERROR] " + line); // 실시간으로 바로 전송
            }

        } catch (IOException e) {
            log.error("출력 스트림 읽기 오류", e);
        }
    }

    // 클라이언트가 전송한 입력을 실행 중인 프로세스로 전달
    public void sendInput(String sessionId, String input) {
        Process process = processMap.get(sessionId);
        if (process != null) {
            try (OutputStream os = process.getOutputStream()) {
                // 입력 받은 데이터를 실행 중인 프로세스로 전송
                os.write((input + "\n").getBytes());
                os.flush();
                log.info("입력 데이터 전송: {}", input);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            log.error("세션 {}에 대한 프로세스가 없습니다.", sessionId);
        }
    }
}
