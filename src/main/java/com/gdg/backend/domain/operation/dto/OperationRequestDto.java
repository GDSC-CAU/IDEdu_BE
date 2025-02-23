package com.gdg.backend.domain.operation.dto;

import com.gdg.backend.domain.enums.Operation;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationRequestDto {
    @NotNull(message = "operation은 null일 수 없습니다.")
    Operation operation;

    @NotNull(message = "documentId는 null일 수 없습니다.")
    Long documentId;

    String content;

    @NotNull(message = "position은 null일 수 없습니다.")
    Long position;

    Long baseVersion;
}
