package com.gdg.backend.domain.member.dto;

import com.gdg.backend.domain.enums.MemberType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@RequiredArgsConstructor
public class SignUpRequestDto {

    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 10, message = "Username must be between 3 and 10 characters")
    private String username;

    @NotBlank(message = "User ID cannot be blank")
    @Size(min = 5, max = 20, message = "User ID must be between 5 and 20 characters")
    private String userId;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
    private String password;

    @NotNull(message = "Member type is required")
    private MemberType memberType;
}
