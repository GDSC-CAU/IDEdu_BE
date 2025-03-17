package com.gdg.backend.domain.operation.dto;

import com.gdg.backend.domain.enums.OperationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SyncOperationResponseDto {
    OperationType operation;
    Long userId;
    Long version;
    String content;
}
