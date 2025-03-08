package com.gdg.backend.domain.member.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

public class CourseInfo {

    @Getter
    @Setter
    @AllArgsConstructor
    public static class TeacherCourseInfo {
        private String courseCode;
        private String courseName;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class StudentCourseInfo {
        private String teacherName;
        private String courseName;
    }
}
