package com.gdg.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gdg.backend.dto.BuildMessage;
import com.gdg.backend.dto.BuildResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class BuildWorker implements MessageListener {

    private final RedisTemplate<String, Object> redisTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Redis 메시지 수신 시 호출됨
    @Override
    public void onMessage(Message message, byte[] pattern) {

        BuildMessage buildMessage = deserializeMessage(message);

        if (buildMessage == null) {
            return; // 역직렬화 실패
        }

        // Docker 컨테이너 실행 (여기서는 시뮬레이션)
        String output = executeCodeInDocker(buildMessage);

        // 실행 결과를 BuildResult에 담아 Redis에 저장
        BuildResult result = new BuildResult(buildMessage.getJobId(), output, "COMPLETED");
        redisTemplate.opsForHash().put("jobResults", buildMessage.getJobId(), result);
        redisTemplate.opsForHash().put("jobStatus", buildMessage.getJobId(), "COMPLETED");
    }

    // 메시지를 BuildMessage 객체로 변환 (JSON 역직렬화)
    private BuildMessage deserializeMessage(Message message) {
        try {
            return objectMapper.readValue(message.getBody(), BuildMessage.class);
        } catch (Exception e) {
            // 실제 환경에서는 로깅 및 예외 처리를 해야 합니다.
            e.printStackTrace();
            return null;
        }
    }

    private String executeCodeInDocker(BuildMessage buildMessage) {

        log.info("실행");

        // buildMessage.getLanguage()에 따라 사용할 Docker 이미지를 선택
        String dockerImage = selectDockerImage(buildMessage.getLanguage());
        String code = buildMessage.getCode();

        log.info("입력된 코드: \n" + code);

        // Java 코드 실행 방식 수정
        String command;
        if ("java".equalsIgnoreCase(buildMessage.getLanguage())) {
            command = "printf \"%s\" '" + code.replace("\\", "\\\\").replace("\"", "\\\"") + "' > Main.java && javac Main.java && java Main";
        } else {
            command = code;  // 다른 언어는 기존 방식 유지
        }

        log.info("실행할 명령어: " + command);

        ProcessBuilder processBuilder = new ProcessBuilder(
                "docker", "run", "--rm", dockerImage, "/bin/sh", "-c", command
        );

        processBuilder.redirectErrorStream(true);

        try {
            Process process = processBuilder.start();
            String output = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8)
            ).lines().collect(Collectors.joining("\n"));

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                return "Docker 컨테이너 실행 에러 (종료코드: " + exitCode + "):\n" + output;
            }
            return output;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Docker 실행 중 예외 발생: " + e.getMessage();
        }
    }


    // 언어에 따른 Docker 이미지 선택 예시 메서드
    private String selectDockerImage(String language) {
        if (language == null) {
            return "alpine";  // 기본 이미지
        }
        switch (language.toLowerCase()) {
            case "java":
                return "openjdk:17";  // Java 실행을 위한 이미지
            case "python":
                return "python:3.8";  // Python 실행을 위한 이미지
            default:
                return "alpine";  // 간단한 쉘 명령 실행을 위한 경량 이미지
        }
    }

}