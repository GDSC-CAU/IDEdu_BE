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

    // 작업 제출: jobId 생성 후 Redis Pub/Sub 채널에 메시지 발행
    public String submitJob(BuildRequest request) {
        String jobId = UUID.randomUUID().toString();
        BuildMessage message = new BuildMessage(jobId, request.getLanguage(), request.getCode());
        redisTemplate.convertAndSend("buildChannel", message);
        // 초기 상태 저장 (예: "PENDING")
        redisTemplate.opsForHash().put("jobStatus", jobId, "PENDING");
        return jobId;
    }

    // 작업 결과 조회
    public BuildResult getResult(String jobId) {
        Object resultObj = redisTemplate.opsForHash().get("jobResults", jobId);

        if (resultObj == null) {
            String status = (String) redisTemplate.opsForHash().get("jobStatus", jobId);
            return new BuildResult(jobId, "아직 결과 없음", status != null ? status : "UNKNOWN");
        }

        // resultObj가 LinkedHashMap일 때, ObjectMapper를 이용하여 BuildResult 객체로 변환
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.convertValue(resultObj, BuildResult.class);
        } catch (Exception e) {
            // 변환 실패 시 적절한 예외 처리
            throw new RuntimeException("결과 변환 실패", e);
        }
    }

}
