package com.gdg.backend.domain.classroom.dto;

import com.gdg.backend.domain.enums.MemberType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class ClassroomDto {

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ClassroomResponseDto {
        private Long studentIdeId;
        private Long teacherIdeId;
        private String className;
        private List<StudentDto> studentList;
        private List<NoticeDto> noticeList;
        private List<AssignmentDto> assignmentList;
        private List<BuildHistoryDto> buildHistoryList;

    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class NoticeDto {
        private String title;
        private String content;
        private LocalDateTime createdAt;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class AssignmentDto {
        private String content;
        private LocalDateTime createdAt;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class BuildHistoryDto {
        private String member;
        private String result;
        private LocalDateTime createdAt;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class StudentDto {
        private String memberName;
        private Long studentId;
    }
}
