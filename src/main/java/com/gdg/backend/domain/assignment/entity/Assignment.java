package com.gdg.backend.domain.assignment.entity;

import com.gdg.backend.common.entity.BaseTimeEntity;
import com.gdg.backend.domain.classroom.entity.Classroom;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Assignment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @ManyToOne
    @JoinColumn(name = "classroom_id")
    private Classroom classroom;

}
