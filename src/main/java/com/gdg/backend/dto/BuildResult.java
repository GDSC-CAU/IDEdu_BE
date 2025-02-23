package com.gdg.backend.dto;

import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class BuildResult {
    private String jobId;
    private String output;
    private String status;
}
