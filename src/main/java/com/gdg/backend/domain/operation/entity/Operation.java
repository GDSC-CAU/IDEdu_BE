package com.gdg.backend.domain.operation.entity;

import com.gdg.backend.common.entity.BaseTimeEntity;
import com.gdg.backend.domain.document.entity.Document;
import com.gdg.backend.domain.enums.OperationType;
import com.gdg.backend.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import org.apache.catalina.User;


@Entity
@Getter
public class Operation extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private OperationType operation;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "document_id")
    private Document document;

    private Long position; // 수정된 내용의 인덱스

    private String content; // 삽입된 텍스트

    private Long version; // 적용 순서

    @OneToOne
    @JoinColumn(name = "user_id")
    private Member user;
}
