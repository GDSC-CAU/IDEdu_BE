package com.gdg.backend.domain.member.repository;

import com.gdg.backend.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByLoginId(String id);
    Boolean existsByLoginId(String id);
}
