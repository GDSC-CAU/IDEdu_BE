package com.gdg.backend.domain.operation.dto;


import com.gdg.backend.domain.enums.Operation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationResponseDto {
    Operation operation;
    Long documentId;
    String content;
    Long position;
    Long version;

    public static OperationResponseDto of(OperationRequestDto request) {
        OperationResponseDto response = new OperationResponseDto();
        response.setOperation(request.getOperation());
        response.setDocumentId(request.getDocumentId());
        response.setContent(request.getContent());
        response.setPosition(request.getPosition());
        response.setVersion(request.getBaseVersion());
        return response;
    }
}
