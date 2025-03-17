package com.gdg.backend.domain.helprequest.service;

import com.gdg.backend.common.annotation.TrackExecutionTime;
import com.gdg.backend.domain.classroom.repository.ClassroomRepository;
import com.gdg.backend.domain.helprequest.dto.HelpRequestDto;
import com.gdg.backend.domain.helprequest.dto.HelpRequestResponseDto;
import com.gdg.backend.domain.mapping.IdeMember;
import com.gdg.backend.domain.mapping.repository.IdeMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HelpRequestService {
    private final SimpMessagingTemplate template;
    private final IdeMemberRepository ideMemberRepository;
    private final ClassroomRepository classroomRepository;

    @TrackExecutionTime
    public void handleHelpRequest(Long classroomId, HelpRequestDto helpRequest) {
        // STOMP 예외처리 정리
        if(!classroomRepository.existsById(classroomId)) throw new IllegalStateException("해당하는 id의 강의실이 존재하지 않습니다. (id=" + classroomId + ")");
        IdeMember ideMember = ideMemberRepository.findByClassroomIdAndMemberIdFetchJoinDocument(classroomId, helpRequest.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("해당 회원은 해당 강의실에 속하지 않습니다. (userId=" + helpRequest.getUserId() + ", id=" + classroomId + ")"));
        HelpRequestResponseDto response = new HelpRequestResponseDto(helpRequest.getUserId(), ideMember.getDocument().getId());
        log.info("broadcast help to 'sub/help/{}': {}", classroomId, response);
        template.convertAndSend("/sub/help/" + classroomId, response);
    }
}
