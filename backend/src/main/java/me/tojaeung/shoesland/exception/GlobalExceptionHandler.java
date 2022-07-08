package me.tojaeung.shoesland.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.extern.slf4j.Slf4j;
import me.tojaeung.shoesland.dto.Response;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(value = { CustomException.class })
  protected ResponseEntity<Response> handleCustomException(CustomException e) {
    log.warn("일반 예외발생 : {}", Response.logFail(e.getHttpStatus(), e.getMessage()));
    return Response.fail(e.getHttpStatus(), e.getMessage());
  }

  @ExceptionHandler(value = { MethodArgumentNotValidException.class })
  protected ResponseEntity<Response> handleValidationException(
      MethodArgumentNotValidException e) {
    String validationMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
    log.warn("유효성 예외발생 : {}", Response.logFail(HttpStatus.BAD_REQUEST, validationMessage));
    return Response.fail(HttpStatus.BAD_REQUEST, validationMessage);
  }

  @ExceptionHandler(value = { Exception.class })
  protected ResponseEntity<Response> handleValidationException(
      Exception e) {
    log.error("서버 에러발생 : {}", e);
    return Response.fail(HttpStatus.BAD_REQUEST, "알 수 없는 에러가 발생하였습니다.");
  }
}
