package com.gdg.backend.domain.build.service;

import com.gdg.backend.common.exception.handler.GeneralHandler;
import com.gdg.backend.common.response.status.ErrorCode;
import com.gdg.backend.domain.build.BuildRepository;
import com.gdg.backend.domain.build.dto.BuildRequest;
import com.gdg.backend.domain.build.entity.Build;
import com.gdg.backend.domain.classroom.entity.Classroom;
import com.gdg.backend.domain.document.entity.Document;
import com.gdg.backend.domain.document.repository.DocumentRepository;
import com.gdg.backend.domain.mapping.IdeMember;
import com.gdg.backend.domain.mapping.repository.IdeMemberRepository;
import com.gdg.backend.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class CodeExecutionService {

    private final SimpMessagingTemplate messagingTemplate;
    private final DocumentRepository documentRepository;
    private final BuildRepository buildRepository;
    private final IdeMemberRepository ideMemberRepository;
    // 세션별 실행 프로세스를 저장하는 맵
    private final Map<String, Process> processMap = new ConcurrentHashMap<>();
    private final Map<String, OutputStream> processOutputStreamMap = new ConcurrentHashMap<>();

    public String runCode(BuildRequest request) {

        log.info("입력 받은 코드 : {}", request.getCode());

        Document document = documentRepository.findById(Long.valueOf(request.getIdeId())).orElseThrow(()-> new GeneralHandler(ErrorCode._BAD_REQUEST));
        IdeMember ideMember = ideMemberRepository.findByDocument(document).orElseThrow(()-> new GeneralHandler(ErrorCode._BAD_REQUEST));
        Classroom classroom = ideMember.getClassroom();
        Member member = ideMember.getMember();

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
                    // javac로 컴파일 후, stdbuf를 이용해 unbuffered 실행
                    command = "javac Main.java && stdbuf -o0 java Main";
                    break;
                case "c":
                    log.info("C 실행");
                    fileName = "code.c";
                    imageName = "gcc:latest";
                    // gcc로 컴파일 후, stdbuf로 unbuffered 실행
                    command = "gcc code.c -o code && stdbuf -o0 ./code";
                    break;
                case "python":
                    log.info("python 실행");
                    fileName = "script.py";
                    imageName = "python:3.8";
                    // -u 옵션을 사용해 unbuffered 실행
                    command = "python -u script.py";
                    break;
                default:
                    throw new IllegalArgumentException("지원하지 않는 언어입니다.");
            }

            Path codeFile = tempDir.resolve(fileName);
            Files.write(codeFile, request.getCode().getBytes());

            // 2. Docker 컨테이너 실행 (인터랙티브 모드)
            List<String> cmd = List.of(
                    "docker", "run", "--rm", "-i", // <-- "-t" 추가
                    "-v", tempDir.toAbsolutePath() + ":/workspace",
                    "-w", "/workspace", imageName,
                    "bash", "-c", command
            );

            ProcessBuilder pb = new ProcessBuilder(cmd);
            Process process = pb.start();

            messagingTemplate.convertAndSend("/sub/output/" + request.getIdeId(), "[INFO] 프로세스가 시작되었습니다.");

            // 3. 생성된 세션 ID로 프로세스를 매핑
            processMap.put(request.getIdeId(), process);
            processOutputStreamMap.put(request.getIdeId(), process.getOutputStream());

            // 4. 실행 결과를 바로 클라이언트에 스트리밍
            new Thread(() -> streamOutput(request.getIdeId(), process, document, classroom, member)).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
        // 생성된 세션 ID를 반환하여 클라이언트가 이후 통신에 사용할 수 있게 함
        return request.getIdeId();
    }

    private void streamOutput(String sessionId, Process process, Document document, Classroom classroom, Member member) {

        log.info("세션 id : {}", sessionId);
        StringBuilder result = new StringBuilder();
        StringBuilder error = new StringBuilder();

        try (
                BufferedReader stdOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
                BufferedReader stdErr = new BufferedReader(new InputStreamReader(process.getErrorStream()))
        ) {
            String line;
            // 표준 출력(STDOUT) 스트리밍
            while ((line = stdOut.readLine()) != null) {
                messagingTemplate.convertAndSend("/sub/output/" + sessionId, line);
                result.append(line);
            }

            // 표준 에러(STDERR) 스트리밍
            while ((line = stdErr.readLine()) != null) {
                messagingTemplate.convertAndSend("/sub/output/" + sessionId, "[ERROR] " + line);
                error.append(line);
            }

            saveBuildResult(document, classroom, result.toString(), error.toString(), member);

            // 프로세스가 종료된 후 종료 메시지 전송
            messagingTemplate.convertAndSend("/sub/output/" + sessionId, "[INFO] 프로세스가 종료되었습니다.");

        } catch (IOException e) {
            log.error("출력 스트림 읽기 오류", e);
        } finally {
            // 프로세스와 출력 스트림 파이프를 정리 (세션 제거)
            processMap.remove(sessionId);
            OutputStream os = processOutputStreamMap.remove(sessionId);
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    log.error("OutputStream 닫기 실패", e);
                }
            }
        }
    }

    // 클라이언트가 전송한 입력을 실행 중인 프로세스로 전달
    public void sendInput(String sessionId, String input) {
        OutputStream os = processOutputStreamMap.get(sessionId);
        if (os != null) {
            try {
                os.write((input + "\n").getBytes());
                os.flush();
                log.info("입력 데이터 전송: {}", input);
            } catch (IOException e) {
                log.error("입력 데이터 전송 실패", e);
            }
        } else {
            log.error("세션 {}에 대한 출력 스트림이 없습니다.", sessionId);
        }
    }

    private void saveBuildResult(Document document, Classroom classroom, String result, String error, Member member) {

        // Build 엔티티 생성 및 저장
        Build build = new Build();
        build.setDocument(document);
        build.setMember(member);
        build.setClassroom(classroom);
        build.setResult(error.isEmpty() ? result : "[ERROR]\n" + error);

        buildRepository.save(build);
        log.info("빌드 결과 저장 완료");
    }
}
