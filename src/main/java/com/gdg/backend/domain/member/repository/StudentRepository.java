package com.gdg.backend.domain.member.repository;

import com.gdg.backend.domain.member.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findById(@Param("id") Long id);

}
