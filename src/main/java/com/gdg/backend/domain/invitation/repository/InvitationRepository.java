package com.gdg.backend.domain.invitation.repository;

import com.gdg.backend.domain.invitation.entity.Invitation;
import com.gdg.backend.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {

    List<Invitation> findAllByMember(Member member);

}
