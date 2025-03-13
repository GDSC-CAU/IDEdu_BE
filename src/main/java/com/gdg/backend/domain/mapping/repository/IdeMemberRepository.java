package com.gdg.backend.domain.mapping.repository;

import com.gdg.backend.domain.document.entity.Document;
import com.gdg.backend.domain.mapping.IdeMember;
import com.gdg.backend.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface IdeMemberRepository extends JpaRepository<IdeMember, Long> {
    Optional<IdeMember> findByMember(Member member);
    Optional<IdeMember> findByDocument(Document document);
}