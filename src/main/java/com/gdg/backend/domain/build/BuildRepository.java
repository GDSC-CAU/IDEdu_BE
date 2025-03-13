package com.gdg.backend.domain.build;

import com.gdg.backend.domain.build.entity.Build;
import com.gdg.backend.domain.classroom.entity.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BuildRepository extends JpaRepository<Build, Long> {

    List<Build> findAllByClassroom(Classroom classroom);

}
