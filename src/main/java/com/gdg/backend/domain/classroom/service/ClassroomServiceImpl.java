package com.gdg.backend.domain.classroom.service;

import com.gdg.backend.common.exception.handler.GeneralHandler;
import com.gdg.backend.common.response.status.ErrorCode;
import com.gdg.backend.domain.assignment.entity.Assignment;
import com.gdg.backend.domain.assignment.repository.AssignmentRepository;
import com.gdg.backend.domain.build.BuildRepository;
import com.gdg.backend.domain.build.entity.Build;
import com.gdg.backend.domain.classroom.dto.ClassroomDto;
import com.gdg.backend.domain.classroom.entity.Classroom;
import com.gdg.backend.domain.classroom.repository.ClassroomRepository;
import com.gdg.backend.domain.document.entity.Document;
import com.gdg.backend.domain.document.repository.DocumentRepository;
import com.gdg.backend.domain.invitation.entity.Invitation;
import com.gdg.backend.domain.invitation.repository.InvitationRepository;
import com.gdg.backend.domain.mapping.IdeMember;
import com.gdg.backend.domain.mapping.repository.IdeMemberRepository;
import com.gdg.backend.domain.member.entity.Member;
import com.gdg.backend.domain.member.entity.Student;
import com.gdg.backend.domain.member.entity.Teacher;
import com.gdg.backend.domain.member.repository.StudentRepository;
import com.gdg.backend.domain.notice.entity.Notice;
import com.gdg.backend.domain.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.print.Doc;

import java.util.ArrayList;
import java.util.List;

import static com.gdg.backend.common.util.RandomCodeGenerator.getRandomCode;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClassroomServiceImpl implements ClassroomService {

    private final ClassroomRepository classroomRepository;
    private final InvitationRepository invitationRepository;
    private final DocumentRepository documentRepository;
    private final IdeMemberRepository ideMemberRepository;
    private final NoticeRepository noticeRepository;
    private final AssignmentRepository assignmentRepository;
    private final BuildRepository buildRepository;

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

        // 선생님의 ide 생성하기
        Document document = new Document();
        document.setVersion(0L);
        document.setContent("Enter Your Code");
        documentRepository.save(document);

        // ide 정보 매핑하기
        IdeMember ideMember = new IdeMember(document, member, classroom);
        ideMemberRepository.save(ideMember);

        return randomCode;
    }

    @Override
    public String enterClassroom(String code, Member member) {

        // 유효하지 않은 코드인 경우
        Classroom classroom = classroomRepository.findByInvitationCode(code).orElseThrow(()-> new GeneralHandler(ErrorCode.INVALID_CODE));

        // 이미 존재하는 경우
        if(invitationRepository.existsByMemberAndClassroom(member, classroom)){
            throw new GeneralHandler(ErrorCode.CLASS_ALREADY_EXIST);
        }

        if(member instanceof Student) {
            Invitation invitation = new Invitation(member, classroom);
            invitationRepository.save(invitation);
        } else {
            throw new GeneralHandler(ErrorCode._FORBIDDEN);
        }

        // 학생의 ide 생성하기
        Document document = new Document();
        document.setVersion(0L);
        document.setContent("Enter Your Code");
        documentRepository.save(document);

        // ide 정보 매핑하기
        IdeMember ideMember = new IdeMember(document, member, classroom);
        ideMemberRepository.save(ideMember);

        return "success";
    }

    @Override
    public ClassroomDto.ClassroomResponseDto getClassroomInfo(Long classroomId, Member member) {


        ClassroomDto.ClassroomResponseDto classroomResponseDto = new ClassroomDto.ClassroomResponseDto();
        IdeMember ideMember = ideMemberRepository.findByMember(member).orElseThrow(()-> new GeneralHandler(ErrorCode.MEMBER_NOT_FOUND));
        Classroom classroom = ideMember.getClassroom();

        // 선생님의 ide 가져오기
        IdeMember ideTeacher = ideMemberRepository.findByMember(classroom.getTeacher()).orElseThrow(()-> new GeneralHandler(ErrorCode.MEMBER_NOT_FOUND));
        classroomResponseDto.setTeacherIdeId(ideTeacher.getDocument().getId());


        // 학생의 ide 가져오기
        if(member instanceof Student) {
            classroomResponseDto.setStudentIdeId(ideMember.getDocument().getId());
        }

        // 클래스명 가져오기
        classroomResponseDto.setClassName(classroom.getName());

        // 등록된 학생 정보 가져오기
        List<Invitation> invitationList = invitationRepository.findAllByClassroom(classroom);
        List<ClassroomDto.StudentDto> studentDtos = new ArrayList<>();

        invitationList.forEach(invitation -> {
            Member student = invitation.getMember();
            studentDtos.add(new ClassroomDto.StudentDto(student.getUsername(), student.getId()));
        });

        // 등록된 공지 사항 가져오기
        // TODO 임시로 구현
        List<Notice> noticeList = noticeRepository.findAll();
        List<ClassroomDto.NoticeDto> noticeDtos = new ArrayList<>();

        noticeList.forEach(notice -> {
            noticeDtos.add(new ClassroomDto.NoticeDto(notice.getTitle(), notice.getContent(), notice.getCreatedDate()));
        });

        // 등록된 과제 가져오기
        // TODO 임시로 구현
        List<Assignment> assignmentList = assignmentRepository.findAll();
        List<ClassroomDto.AssignmentDto> assignmentDtos = new ArrayList<>();

        assignmentList.forEach(assignment -> {
            assignmentDtos.add(new ClassroomDto.AssignmentDto(assignment.getContent(), assignment.getCreatedDate()));
        });

        // 빌드 기록 가져오기
        List<Build> buildList = buildRepository.findAllByClassroom(classroom);
        List<ClassroomDto.BuildHistoryDto> buildHistoryDtos = new ArrayList<>();

        buildList.forEach(build -> {
            buildHistoryDtos.add(
                    new ClassroomDto.BuildHistoryDto(
                            build.getMember().getUsername(),
                            build.getResult(),
                            build.getCreatedDate()));
        });

        classroomResponseDto.setNoticeList(noticeDtos);
        classroomResponseDto.setAssignmentList(assignmentDtos);
        classroomResponseDto.setStudentList(studentDtos);
        classroomResponseDto.setBuildHistoryList(buildHistoryDtos);

        return classroomResponseDto;
    }

}
