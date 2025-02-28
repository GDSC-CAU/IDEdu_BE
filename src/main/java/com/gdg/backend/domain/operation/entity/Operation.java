package com.gdg.backend.domain.operation.entity;

import com.gdg.backend.common.entity.BaseTimeEntity;
import com.gdg.backend.domain.document.entity.Document;
import com.gdg.backend.domain.enums.OperationType;
import com.gdg.backend.domain.member.entity.Member;
import com.gdg.backend.domain.operation.dto.OperationResponseDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.catalina.User;


@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    private String insertContent; // 삽입된 텍스트

    private Integer deleteLength;

    private Long version; // 적용 순서

}
