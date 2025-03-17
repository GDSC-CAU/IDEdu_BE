package com.gdg.backend.domain.build.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InputMessage {
    private String sessionId; // 실행 중인 세션을 식별하기 위한 고유 ID
    private String input;     // 사용자가 입력한 값 (예: 표준 입력으로 전달될 데이터)
}
