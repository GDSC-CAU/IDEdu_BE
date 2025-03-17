package com.gdg.backend.domain.member.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignInResponseDto {
    private String username;
    private String userId;
    private String token;
}
