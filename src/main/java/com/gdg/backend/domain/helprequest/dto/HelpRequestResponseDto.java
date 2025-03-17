package com.gdg.backend.domain.helprequest.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HelpRequestResponseDto {
    private Long userId;
    private Long documentId;
    @Override
    public String toString() {
        return "HelpRequestResponseDto(userId=" + userId + ", documentId=" + documentId + ")";
    }
}
