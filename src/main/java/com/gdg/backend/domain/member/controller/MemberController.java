package com.gdg.backend.domain.member.controller;

import com.gdg.backend.common.response.ApiResponse;
import com.gdg.backend.domain.member.dto.SignInRequestDto;
import com.gdg.backend.domain.member.dto.SignInResponseDto;
import com.gdg.backend.domain.member.dto.SignUpRequestDto;
import com.gdg.backend.domain.member.dto.SignUpResponseDto;
import com.gdg.backend.domain.member.entity.Student;
import com.gdg.backend.domain.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "멤버 관련 API", description = "멤버 관련 API입니다")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/sign-up")
    @Operation(summary = "회원가입")
    public ApiResponse<SignUpResponseDto> signupStudent(@RequestBody @Valid SignUpRequestDto signUpRequestDto) {
        return ApiResponse.onSuccess(memberService.register(signUpRequestDto));
    }

    @PostMapping("/sign-in")
    @Operation(summary = "로그인")
    public ApiResponse<SignInResponseDto> signupStudent(@RequestBody @Valid SignInRequestDto signInRequestDto) {
        return ApiResponse.onSuccess(memberService.signIn(signInRequestDto));
    }
}
