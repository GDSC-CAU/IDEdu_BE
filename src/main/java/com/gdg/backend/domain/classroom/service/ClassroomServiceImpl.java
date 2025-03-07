package com.gdg.backend.domain.classroom.service;

import com.gdg.backend.common.exception.handler.GeneralHandler;
import com.gdg.backend.common.response.status.ErrorCode;
import com.gdg.backend.domain.classroom.entity.Classroom;
import com.gdg.backend.domain.classroom.repository.ClassroomRepository;
import com.gdg.backend.domain.invitation.entity.Invitation;
import com.gdg.backend.domain.invitation.repository.InvitationRepository;
import com.gdg.backend.domain.member.entity.Member;
import com.gdg.backend.domain.member.entity.Student;
import com.gdg.backend.domain.member.entity.Teacher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.gdg.backend.common.util.RandomCodeGenerator.getRandomCode;

@Service
@RequiredArgsConstructor
public class ClassroomServiceImpl implements ClassroomService {

    private final ClassroomRepository classroomRepository;
    private final InvitationRepository invitationRepository;

    @Override
    @Transactional
    public String createClassroom(String name, Member member) {

        // 이미 존재하는 경우
        if(classroomRepository.existsByTeacherAndName((Teacher) member, name)){
            throw new GeneralHandler(ErrorCode.CLASS_ALREADY_EXIST);
        }

        String randomCode = getRandomCode(7);

        Classroom classroom = new Classroom(name, randomCode, member);
        classroomRepository.save(classroom);

        return randomCode;
    }

    @Override
    public String enterClassroom(String code, Member member) {

        // 유효하지 않은 코드인 경우
        Classroom classroom = classroomRepository.findByInvitationCode(code).orElseThrow(()-> new GeneralHandler(ErrorCode.INVALID_CODE));

        if(member instanceof Student) {
            Invitation invitation = new Invitation(member, classroom);
            invitationRepository.save(invitation);
        } else {
            throw new GeneralHandler(ErrorCode._FORBIDDEN);
        }

        return "success";
    }

}
