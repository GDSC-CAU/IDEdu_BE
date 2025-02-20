package com.gdg.backend.domain.build.entity;

import com.gdg.backend.common.entity.BaseTimeEntity;
import com.gdg.backend.domain.document.entity.Document;
import com.gdg.backend.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
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

    private String result;
}

