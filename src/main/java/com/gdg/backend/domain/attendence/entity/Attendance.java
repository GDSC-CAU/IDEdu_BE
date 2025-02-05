package com.gdg.backend.domain.attendence.entity;

import com.gdg.backend.common.entity.BaseTimeEntity;
import com.gdg.backend.domain.course.entity.Course;
import com.gdg.backend.domain.member.entity.Member;
import jakarta.persistence.*;

@Entity
public class Attendance extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private Course course;


}
