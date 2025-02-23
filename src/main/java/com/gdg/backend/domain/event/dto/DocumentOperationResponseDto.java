package com.gdg.backend.domain.event.dto;


import com.gdg.backend.domain.enums.Operation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentOperationResponseDto {
    Operation operation;
    Long documentId;
    String content;
    Long position;
    Long version;

    public static DocumentOperationResponseDto of(DocumentOperationRequestDto request) {
        DocumentOperationResponseDto response = new DocumentOperationResponseDto();
        response.setOperation(request.getOperation());
        response.setDocumentId(request.getDocumentId());
        response.setContent(request.getContent());
        response.setPosition(request.getPosition());
        response.setVersion(request.getBaseVersion());
        return response;
    }
}
