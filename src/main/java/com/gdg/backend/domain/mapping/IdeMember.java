package com.gdg.backend.domain.mapping;

import com.gdg.backend.domain.classroom.entity.Classroom;
import com.gdg.backend.domain.document.entity.Document;
import com.gdg.backend.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class IdeMember {

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

    public IdeMember(Document document, Member member, Classroom classroom) {
        this.document = document;
        this.member = member;
        this.classroom = classroom;
    }
}
