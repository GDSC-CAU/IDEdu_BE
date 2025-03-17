package com.gdg.backend.domain.build.entity;

import com.gdg.backend.common.entity.BaseTimeEntity;
import com.gdg.backend.domain.classroom.entity.Classroom;
import com.gdg.backend.domain.document.entity.Document;
import com.gdg.backend.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Build extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "document_id")
    private Document document;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "classroom_id")
    private Classroom classroom;

    private String result;
}

