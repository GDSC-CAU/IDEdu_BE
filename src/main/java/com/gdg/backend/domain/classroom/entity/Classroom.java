package com.gdg.backend.domain.classroom.entity;

import com.gdg.backend.domain.course.entity.Course;
import com.gdg.backend.domain.invitation.entity.Invitation;
import com.gdg.backend.domain.notice.entity.Notice;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Classroom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // 강의실 이름

    @OneToMany(mappedBy = "classroom")
    private List<Course> courses;

    @OneToMany(mappedBy = "classroom")
    private List<Notice> notices;

    @OneToMany(mappedBy = "classroom")
    private List<Invitation> invitations;
}
