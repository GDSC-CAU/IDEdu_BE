package com.gdg.backend.domain.classroom.service;

import com.gdg.backend.domain.classroom.entity.Classroom;
import com.gdg.backend.domain.member.entity.Member;

public interface ClassroomService {

    String createClassroom(String name, Member member);

    String enterClassroom(String code, Member member);
}
