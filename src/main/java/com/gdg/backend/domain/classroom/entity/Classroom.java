package com.gdg.backend.domain.classroom.entity;

import com.gdg.backend.common.exception.handler.GeneralHandler;
import com.gdg.backend.common.response.status.ErrorCode;
import com.gdg.backend.domain.course.entity.Course;
import com.gdg.backend.domain.invitation.entity.Invitation;
import com.gdg.backend.domain.member.entity.Member;
import com.gdg.backend.domain.member.entity.Teacher;
import com.gdg.backend.domain.notice.entity.Notice;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Classroom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // 강의실 이름

    private String invitationCode;

    @ManyToOne
    private Teacher teacher;

    @OneToMany(mappedBy = "classroom")
    private List<Course> courses;

    @OneToMany(mappedBy = "classroom")
    private List<Notice> notices;

    @OneToMany(mappedBy = "classroom")
    private List<Invitation> invitations;

    public Classroom(String name, String randomCode, Member member) {
        this.name = name;
        this.invitationCode = randomCode;

        if(member instanceof Teacher){
            this.teacher = (Teacher) member;
        } else {
            throw new GeneralHandler(ErrorCode._FORBIDDEN);
        }
    }
}
