package com.gdg.backend.domain.member.dto;

import com.gdg.backend.domain.enums.MemberType;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignUpResponseDto {
    private String username;
    private String userId;
}
