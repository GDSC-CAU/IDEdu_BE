package com.gdg.backend.common.entity;

import java.time.LocalDateTime;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.web.bind.annotation.RequestParam;

@MappedSuperclass  // 이 클래스는 다른 엔티티 클래스의 부모 클래스가 될 수 있도록 설정
@EntityListeners(AuditingEntityListener.class)  // JPA Auditing 기능을 사용하기 위한 설정
@Getter
@Setter
public abstract class BaseTimeEntity {

    @CreatedDate  // 엔티티 생성 시 자동으로 현재 시간이 저장됨
    private LocalDateTime createdDate;

    @LastModifiedDate  // 엔티티 수정 시 자동으로 현재 시간이 저장됨
    private LocalDateTime modifiedDate;
}

