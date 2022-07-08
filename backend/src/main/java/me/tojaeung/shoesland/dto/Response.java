package me.tojaeung.shoesland.dto;

import java.time.LocalDate;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import lombok.*;

@Builder
@Getter
@AllArgsConstructor
public class Response {
  private final LocalDate timestamp = LocalDate.now();
  private final Boolean isSuccess;
  private final int code;
  private final String status;
  private final String message;
  private final Object result;

  public static ResponseEntity<Response> success(HttpStatus httpStatus, String message, Object result) {
    Response response = Response.builder()
        .isSuccess(true)
        .code(httpStatus.value())
        .status(httpStatus.name())
        .message(message)
        .result(result)
        .build();

    return ResponseEntity.status(httpStatus).body(response);
  }

  public static ResponseEntity<Response> fail(HttpStatus httpStatus, String message) {
    Response response = Response.builder()
        .isSuccess(false)
        .code(httpStatus.value())
        .status(httpStatus.name())
        .message(message)
        .result(null)
        .build();

    return ResponseEntity.status(httpStatus).body(response);
  }

  public static Response logFail(HttpStatus httpStatus, String message) {
    return Response.builder()
        .isSuccess(false)
        .code(httpStatus.value())
        .status(httpStatus.name())
        .message(message)
        .result(null)
        .build();
  }
}
