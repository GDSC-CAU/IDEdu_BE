package com.gdg.backend.domain.assignment.repository;

import com.gdg.backend.domain.assignment.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
}
