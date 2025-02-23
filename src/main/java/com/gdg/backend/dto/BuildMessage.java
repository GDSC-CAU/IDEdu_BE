package com.gdg.backend.dto;

import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class BuildMessage {
    private String jobId;
    private String language;
    private String code;

}
