package me.tojaeung.shoesland.exception;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;

import lombok.*;
import me.tojaeung.shoesland.enums.ErrorCode;

@Getter
@Builder
public class ErrorResponse {
  private final LocalDateTime timestamp = LocalDateTime.now();
  private final boolean success;
  private final int status;
  private final String error;
  private final String code;
  private final String message;

  public static ResponseEntity<ErrorResponse> toResponseEntity(ErrorCode errorCode) {
    return ResponseEntity
        .status(errorCode.getHttpStatus())
        .body(ErrorResponse.builder()
            .status(errorCode.getHttpStatus().value())
            .error(errorCode.getHttpStatus().name())
            .code(errorCode.name())
            .message(errorCode.getMessage())
            .build());
  }
}
