package me.tojaeung.shoesland.exception;

import org.springframework.http.HttpStatus;

import lombok.*;

@Getter
public class CustomException extends RuntimeException {
  private final HttpStatus httpStatus;

  public CustomException(String message, HttpStatus httpStatus) {
    super(message);
    this.httpStatus = httpStatus;
  }
}
