package com.gdg.backend.domain.operation.dto;


import com.gdg.backend.domain.enums.OperationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationResponseDto {
    OperationType operation;
    Long documentId;
    String insertContent;
    Integer deleteLength;
    Long position;
    Long version;
    Long userId;

    public static OperationResponseDto of(OperationRequestDto request) {
        OperationResponseDto response = new OperationResponseDto();
        response.setOperation(request.getOperation());
        response.setDocumentId(request.getDocumentId());
        response.setInsertContent(request.getInsertContent());
        response.setDeleteLength(request.getDeleteLength());
        response.setPosition(request.getPosition());
        response.setVersion(request.getBaseVersion());
        response.setUserId(request.getUserId());
        return response;
    }
}
