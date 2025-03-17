package com.gdg.backend.domain.member.service;

import com.gdg.backend.domain.member.dto.SignInRequestDto;
import com.gdg.backend.domain.member.dto.SignInResponseDto;
import com.gdg.backend.domain.member.dto.SignUpRequestDto;
import com.gdg.backend.domain.member.dto.SignUpResponseDto;
import com.gdg.backend.domain.member.entity.Member;

public interface MemberService {
    SignUpResponseDto register(SignUpRequestDto signUpRequestDto);
    SignInResponseDto signIn(SignInRequestDto signInRequestDto);
    Object getDashboardInfo(Member member);
}
