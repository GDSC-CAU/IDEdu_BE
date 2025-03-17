package com.gdg.backend.domain.build.dto;

import com.gdg.backend.domain.enums.LanguageType;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BuildResponse {
    private String output;
}
