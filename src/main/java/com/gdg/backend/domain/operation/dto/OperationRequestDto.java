package com.gdg.backend.domain.operation.dto;

import com.gdg.backend.domain.enums.OperationType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationRequestDto {
    @NotNull(message = "operation은 null일 수 없습니다.")
    OperationType operation;

    @NotNull(message = "documentId는 null일 수 없습니다.")
    Long documentId;

    String insertContent;

    Integer deleteLength;

    @NotNull(message = "position은 null일 수 없습니다.")
    Long position;

    Long baseVersion;

    Long userId; // todo 추후에 jwt 헤더에서 유저 정보 가져오는 걸로 바꾸기

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(operation).append(" ");
        if(operation.equals(OperationType.INSERT)) sb.append(insertContent + " ");
        if(operation.equals(OperationType.DELETE)) sb.append(deleteLength + " ");
        sb.append("pos=").append(position).append(" ")
          .append(String.format("docId=%d, version=%d, userId=%d", documentId, baseVersion, userId));
        return sb.toString();
    }
}
