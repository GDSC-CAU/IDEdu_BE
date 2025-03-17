package com.gdg.backend.domain.classroom.controller;

import com.gdg.backend.common.annotation.AuthUser;
import com.gdg.backend.common.response.ApiResponse;
import com.gdg.backend.domain.classroom.dto.ClassroomDto;
import com.gdg.backend.domain.classroom.service.ClassroomService;
import com.gdg.backend.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "강의실 관련 API", description = "강의실 관련 API입니다")
public class ClassroomController {

    private final ClassroomService classroomService;

    //TODO 일단 제일 쉬운 방법으로
    @PostMapping("/classroom/add")
    @Operation(summary = "강의실 추가")
    public ApiResponse<String> addClassroom(@RequestParam String name, @AuthUser Member member) {
        return ApiResponse.onSuccess(classroomService.createClassroom(name, member));
    }

    //TODO 일단 제일 쉬운 방법으로
    @PostMapping("/classroom/enter")
    @Operation(summary = "강의실 입장")
    public ApiResponse<String> enterClassroom(@RequestParam String code, @AuthUser Member member) {
        return ApiResponse.onSuccess(classroomService.enterClassroom(code, member));
    }

    @GetMapping("/classroom/{classroomId}")
    @Operation(summary = "강의실 정보 가져오기")
    public ApiResponse<ClassroomDto.ClassroomResponseDto> getClassroomInfo(@PathVariable Long classroomId, @AuthUser Member member) {
        return ApiResponse.onSuccess(classroomService.getClassroomInfo(classroomId, member));
    }

}
