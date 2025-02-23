package com.gdg.backend.service;

import com.gdg.backend.dto.BuildRequest;
import com.gdg.backend.dto.BuildResult;

public interface BuildService {
    String submitJob(BuildRequest buildRequest);
    BuildResult getResult(String jobId);
}
