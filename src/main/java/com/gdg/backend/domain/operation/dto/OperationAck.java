package com.gdg.backend.domain.operation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/** 클라이언트가 서버에게 보내는 확인 메시지 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationAck {
    private Long documentId;
    private Long version;
}
