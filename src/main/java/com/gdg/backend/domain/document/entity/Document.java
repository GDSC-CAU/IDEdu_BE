package com.gdg.backend.domain.document.entity;

import com.gdg.backend.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String content;

    @Setter
    private Long version;

    /**
     * content 수정용 StringBuilder (content 직접 수정은 String이므로 오래 걸림)
     * !! 주의: DB 저장 전에 syncContentBuilder() 등으로 contentBuilder -> content 동기화 필요 !!
     * */
    @Transient
    private StringBuilder contentBuilder;

    public StringBuilder getContentBuilder() {
        if(contentBuilder == null) initContentBuilder();
        return contentBuilder;
    }

    public void syncContentBuilder() {
        if(contentBuilder != null) content = contentBuilder.toString();
    }

    // DB에서 로드 시 content -> contentBuilder 초기화
    @PostLoad
    public void initContentBuilder() {
        this.contentBuilder = new StringBuilder(content != null ? content : "");
    }

    // INSERT, UPDATE 전 contentBuilder -> content 동기화
    @PrePersist
    @PreUpdate 
    public void syncContentBeforeSave() {
        if (contentBuilder != null) {
            content = contentBuilder.toString();
        }
    }

    @Override
    public String toString() {
        return String.format("DOCUMENT(id=%d, version=%d", id, version) + "content=" + content + ")";
    }
}
