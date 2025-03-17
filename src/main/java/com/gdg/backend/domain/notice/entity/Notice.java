package com.gdg.backend.domain.notice.entity;

import com.gdg.backend.common.entity.BaseTimeEntity;
import com.gdg.backend.domain.classroom.entity.Classroom;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Notice extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "classroom_id")
    private Classroom classroom;

    private String title;

    private String content;
}
