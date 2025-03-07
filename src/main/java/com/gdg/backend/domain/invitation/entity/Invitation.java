package com.gdg.backend.domain.invitation.entity;

import com.gdg.backend.common.entity.BaseTimeEntity;
import com.gdg.backend.domain.classroom.entity.Classroom;
import com.gdg.backend.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Invitation extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "classroom_id")
    private Classroom classroom;

    public Invitation(Member member, Classroom classroom) {
        super();
        this.member = member;
        this.classroom = classroom;
    }
}
