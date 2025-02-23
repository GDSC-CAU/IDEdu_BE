package com.gdg.backend.controller;

import com.gdg.backend.dto.BuildRequest;
import com.gdg.backend.dto.BuildResult;
import com.gdg.backend.service.BuildService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/build")
@RequiredArgsConstructor
public class BuildController {

    private final BuildService buildService;

    @PostMapping("/submit")
    public ResponseEntity<Map<String, String>> submitJob(@RequestBody BuildRequest request) {
        String jobId = buildService.submitJob(request);
        return ResponseEntity.ok(Collections.singletonMap("jobId", jobId));
    }

    @GetMapping("/result/{jobId}")
    public ResponseEntity<BuildResult> getResult(@PathVariable String jobId) {
        BuildResult result = buildService.getResult(jobId);
        return ResponseEntity.ok(result);
    }
}
