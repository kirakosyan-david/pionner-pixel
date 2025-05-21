package com.example.pioneerpixel.exception.custom;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class PhoneAndEmailOperationException extends RuntimeException {

  public PhoneAndEmailOperationException() {
    super();
  }

  public PhoneAndEmailOperationException(String message) {
    super(message);
  }

  public PhoneAndEmailOperationException(String message, Throwable cause) {
    super(message, cause);
  }

  public PhoneAndEmailOperationException(Throwable cause) {
    super(cause);
  }

  public PhoneAndEmailOperationException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
