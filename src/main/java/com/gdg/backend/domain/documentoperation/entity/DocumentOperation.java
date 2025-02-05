package com.gdg.backend.domain.documentoperation.entity;

import com.gdg.backend.common.entity.BaseTimeEntity;
import com.gdg.backend.domain.Enum.Operation;
import com.gdg.backend.domain.document.entity.Document;
import com.gdg.backend.domain.member.entity.Member;
import jakarta.persistence.*;



@Entity
public class DocumentOperation extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Operation operation;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "document_id")
    private Document document;

    private Integer index; // 수정된 내용의 인덱스

    private String content; // 삽입된 텍스트
}
