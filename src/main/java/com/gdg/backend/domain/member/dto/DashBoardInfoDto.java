package com.gdg.backend.domain.member.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

import java.util.List;

public class DashBoardInfoDto {

    @Getter
    @Setter
    @AllArgsConstructor  // 모든 필드가 있는 생성자 자동 생성
    public static class StudentDashBoardInfoDto {
        private String studentName;
        private List<CourseInfo.StudentCourseInfo> studentCourseInfoList;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class TeacherDashBoardInfoDto {
        private String teacherName;
        private List<CourseInfo.TeacherCourseInfo> teacherCourseInfoList;
    }
}
