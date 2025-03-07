package com.gdg.backend.domain.classroom.repository;

import com.gdg.backend.domain.classroom.entity.Classroom;
import com.gdg.backend.domain.member.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClassroomRepository extends JpaRepository<Classroom, Long> {
    Optional<Classroom> findByInvitationCode(String invitationCode);
    Boolean existsByTeacherAndName(Teacher teacher, String name);
}
