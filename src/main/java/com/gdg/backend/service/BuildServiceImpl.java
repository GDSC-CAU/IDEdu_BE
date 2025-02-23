package com.gdg.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gdg.backend.dto.BuildMessage;
import com.gdg.backend.dto.BuildRequest;
import com.gdg.backend.dto.BuildResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BuildServiceImpl implements BuildService {

    private final RedisTemplate<String, Object> redisTemplate;

    public String submitJob(BuildRequest request) {
        String jobId = UUID.randomUUID().toString();
        BuildMessage message = new BuildMessage(jobId, request.getLanguage(), request.getCode(), request.getInput());
        redisTemplate.convertAndSend("buildChannel", message);
        redisTemplate.opsForHash().put("jobStatus", jobId, "PENDING");
        return jobId;
    }

    public BuildResult getResult(String jobId) {
        Object resultObj = redisTemplate.opsForHash().get("jobResults", jobId);

        if (resultObj == null) {
            String status = (String) redisTemplate.opsForHash().get("jobStatus", jobId);
            return new BuildResult(jobId, "아직 결과 없음", status != null ? status : "UNKNOWN");
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.convertValue(resultObj, BuildResult.class);
        } catch (Exception e) {
            throw new RuntimeException("결과 변환 실패", e);
        }
    }

}
