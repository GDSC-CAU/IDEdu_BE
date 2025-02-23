package com.gdg.backend.domain.event.dto;

import com.gdg.backend.domain.enums.Operation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentOperationRequestDto {
    @NotBlank
    Operation operation;

    @NotNull
    Long documentId;

    String content;

    Long position;

    Long baseVersion;
}
