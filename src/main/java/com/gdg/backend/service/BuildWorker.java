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

    @Override
    public void onMessage(Message message, byte[] pattern) {

        BuildMessage buildMessage = deserializeMessage(message);

        if (buildMessage == null) {
            return; // 역직렬화 실패
        }

        String output = executeCodeInDocker(buildMessage);

        BuildResult result = new BuildResult(buildMessage.getJobId(), output, "COMPLETED");
        redisTemplate.opsForHash().put("jobResults", buildMessage.getJobId(), result);
        redisTemplate.opsForHash().put("jobStatus", buildMessage.getJobId(), "COMPLETED");
    }

    private BuildMessage deserializeMessage(Message message) {
        try {
            return objectMapper.readValue(message.getBody(), BuildMessage.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String executeCodeInDocker(BuildMessage buildMessage) {
        log.info("실행");

        String dockerImage = selectDockerImage(buildMessage.getLanguage());
        String code = buildMessage.getCode();
        String input = buildMessage.getInput(); // 입력값 (BuildMessage에 추가된 필드)

        log.info("입력된 코드: \n" + code);
        if (input != null && !input.isEmpty()) {
            log.info("입력값: \n" + input);
        }

        String command;
        if ("java".equalsIgnoreCase(buildMessage.getLanguage())) {
            String escapedCode = code.replace("\\", "\\\\").replace("\"", "\\\"");
            if (input != null && !input.isEmpty()) {
                String escapedInput = input.replace("'", "'\\''");
                // 입력값이 있으면 echo로 파이프 처리
                command = "printf \"%s\" '" + escapedCode + "' > Main.java && javac Main.java && echo '" + escapedInput + "' | java Main";
            } else {
                command = "printf \"%s\" '" + escapedCode + "' > Main.java && javac Main.java && java Main";
            }
        } else if ("python".equalsIgnoreCase(buildMessage.getLanguage())) {
            String escapedCode = code.replace("'", "'\\''");
            if (input != null && !input.isEmpty()) {
                String escapedInput = input.replace("'", "'\\''");
                // 입력값이 있으면 echo로 파이프 처리
                command = "echo '" + escapedInput + "' | python3 -c '" + escapedCode + "'";
            } else {
                command = "python3 -c '" + escapedCode + "'";
            }
        } else {
            // 다른 언어의 경우 입력값이 있다면 파이프로 전달
            if (input != null && !input.isEmpty()) {
                String escapedInput = input.replace("'", "'\\''");
                command = "echo '" + escapedInput + "' | " + code;
            } else {
                command = code;
            }
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






    private String selectDockerImage(String language) {
        if (language == null) {
            return "alpine";
        }
        switch (language.toLowerCase()) {
            case "java":
                return "openjdk:17";
            case "python":
                return "python:3.8";
            default:
                return "alpine";
        }
    }

}