package com.gdg.backend.domain.member.repository;

import com.gdg.backend.domain.member.entity.Student;
import com.gdg.backend.domain.member.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface TeacherRepository extends JpaRepository<Teacher, Long> {
}
