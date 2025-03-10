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

    @PostLoad // JPA가 DB에서 로드한 후 호출
    public void initContentBuilder() {
        this.contentBuilder = new StringBuilder(content != null ? content : "");
    }

    public StringBuilder getContentBuilder() {
        if(contentBuilder == null) initContentBuilder();
        return contentBuilder;
    }

    public void syncContentBuilder() {
        if(contentBuilder != null) content = contentBuilder.toString();
    }
}
