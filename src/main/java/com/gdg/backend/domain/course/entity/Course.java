package com.gdg.backend.domain.course.entity;

import com.gdg.backend.domain.attendence.entity.Attendance;
import com.gdg.backend.domain.classroom.entity.Classroom;
import com.gdg.backend.domain.mapping.ClassDocument;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.List;

@Entity
@Getter
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // 강의 이름

    @ManyToOne
    @JoinColumn(name = "classroom_id")
    private Classroom classroom;


    @OneToMany(mappedBy = "course")
    private List<Attendance> attendances;

    @OneToMany(mappedBy = "course")
    private List<ClassDocument> classDocuments;
}

