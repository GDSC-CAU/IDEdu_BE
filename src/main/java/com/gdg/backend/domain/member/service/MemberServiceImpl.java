package com.gdg.backend.domain.member.service;

import com.gdg.backend.common.exception.handler.GeneralHandler;
import com.gdg.backend.common.jwt.CustomPasswordEncoder;
import com.gdg.backend.common.jwt.JwtTokenProvider;
import com.gdg.backend.common.response.status.ErrorCode;
import com.gdg.backend.domain.classroom.entity.Classroom;
import com.gdg.backend.domain.classroom.repository.ClassroomRepository;
import com.gdg.backend.domain.invitation.entity.Invitation;
import com.gdg.backend.domain.member.dto.*;
import com.gdg.backend.domain.member.entity.Member;
import com.gdg.backend.domain.member.entity.Student;
import com.gdg.backend.domain.member.entity.Teacher;
import com.gdg.backend.domain.invitation.repository.InvitationRepository;
import com.gdg.backend.domain.member.repository.MemberRepository;
import com.gdg.backend.domain.member.repository.StudentRepository;
import com.gdg.backend.domain.member.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final MemberRepository memberRepository;
    private final CustomPasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final InvitationRepository invitationRepository;
    private final ClassroomRepository classroomRepository;

    @Override
    public SignUpResponseDto register(SignUpRequestDto signUpRequestDto) {

        log.info("입력 받은 유저 이름 : {}", signUpRequestDto.getUsername());
        log.info("입력 받은 유저 아이디 : {}",signUpRequestDto.getUserId());

        if(memberRepository.existsByLoginId(signUpRequestDto.getUserId())){
            throw new GeneralHandler(ErrorCode.EMAIL_ALREADY_EXIST);
        }

        return switch (signUpRequestDto.getMemberType()) {
            case STUDENT -> {
                Student student = Student.builder()
                        .username(signUpRequestDto.getUsername())
                        .loginId(signUpRequestDto.getUserId())
                        .password(passwordEncoder.encode(signUpRequestDto.getPassword()))
                        .build();

                studentRepository.save(student);

                yield SignUpResponseDto.builder()
                        .userId(student.getLoginId())
                        .username(student.getUsername())
                        .build();
            }
            case TEACHER -> {
                Teacher teacher = Teacher.builder()
                        .username(signUpRequestDto.getUsername())
                        .loginId(signUpRequestDto.getUserId())
                        .password(passwordEncoder.encode(signUpRequestDto.getPassword()))
                        .build();

                teacherRepository.save(teacher);

                yield SignUpResponseDto.builder()
                        .userId(teacher.getLoginId())
                        .username(teacher.getUsername())
                        .build();
            }
        };
    }

    @Override
    public SignInResponseDto signIn(SignInRequestDto signInRequestDto) {

        String id = signInRequestDto.getUserId();
        String password = signInRequestDto.getPassword();

        log.info("[getSignInResult] signDataHandler 로 회원 정보 요청");

        Member member = memberRepository.findByLoginId(id).orElseThrow(()-> new GeneralHandler(ErrorCode.MEMBER_NOT_FOUND));

        log.info("[getSignInResult] Id : {}", id);

        log.info("[getSignInResult] 패스워드 비교 수행");
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new GeneralHandler(ErrorCode.MEMBER_LOGIN_FAILURE);
        }
        log.info("[getSignInResult] 패스워드 일치");

        log.info("[getSignInResult] SignInResultDto 객체 생성");
        SignInResponseDto signInResultDto = SignInResponseDto.builder()
                .userId(id)
                .username(member.getUsername())
                .token(jwtTokenProvider.generateToken(member.getId()))
                .build();

        log.info("[getSignInResult] SignInResultDto 객체에 값 주입");

        return signInResultDto;
    }

    @Override
    public Object getDashboardInfo(Member member) {

        log.info(member.getUsername());

        if (member instanceof Student) {

            List<Invitation> invitations = invitationRepository.findAllByMember(member);

            List<CourseInfo.StudentCourseInfo> studentCourseInfos = new java.util.ArrayList<>(List.of());

            invitations.forEach(invitation -> {
                studentCourseInfos.add(new CourseInfo.StudentCourseInfo(invitation.getClassroom().getName(), invitation.getClassroom().getTeacher().getUsername()));
            });

            return new DashBoardInfoDto.StudentDashBoardInfoDto(member.getUsername(), studentCourseInfos);

        } else if (member instanceof Teacher) {

            List<Classroom> classrooms = classroomRepository.findAllByTeacher((Teacher) member);

            List<CourseInfo.TeacherCourseInfo> teacherCourseInfos = new java.util.ArrayList<>(List.of());

            classrooms.forEach(classroom -> {
                teacherCourseInfos.add(new CourseInfo.TeacherCourseInfo(classroom.getInvitationCode(), classroom.getName()));
            });

            return new DashBoardInfoDto.TeacherDashBoardInfoDto(member.getUsername(), teacherCourseInfos);

        } else {
            throw new IllegalArgumentException("Unknown member type");
        }
    }
}
