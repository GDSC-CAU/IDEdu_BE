package com.gdg.backend.dto;

import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class BuildRequest {
    private Long memberId;
    private String language;
    private String code;
    private String input;
}
