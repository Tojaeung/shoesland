package me.tojaeung.shoesland.exception;

import lombok.*;
import me.tojaeung.shoesland.enums.ErrorCode;

@Getter
@AllArgsConstructor
public class CustomException extends RuntimeException {
  private final ErrorCode errorCode;
}
