package com.example.pioneerpixel.exception;

import com.example.pioneerpixel.dto.PioneerPixelErrorDto;
import com.example.pioneerpixel.exception.custom.PhoneAndEmailOperationException;
import com.example.pioneerpixel.exception.custom.TransferException;
import com.example.pioneerpixel.exception.custom.UserNotFoundException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class PioneerPixelExceptionHandler extends ResponseEntityExceptionHandler {
  @ExceptionHandler({
    UserNotFoundException.class,
    TransferException.class,
    PhoneAndEmailOperationException.class
  })
  public ResponseEntity<Object> handleNotFoundExceptions(Exception ex, WebRequest request) {
    PioneerPixelErrorDto errorDto = PioneerPixelErrorDto.builder().message(ex.getMessage()).build();
    return handleExceptionInternal(ex, errorDto, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Object> handleIllegalArgument(
      IllegalArgumentException ex, WebRequest request) {
    PioneerPixelErrorDto errorDto = PioneerPixelErrorDto.builder().message(ex.getMessage()).build();
    return handleExceptionInternal(
        ex, errorDto, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {

    Map<String, String> validationErrors = new HashMap<>();
    for (FieldError error : ex.getBindingResult().getFieldErrors()) {
      validationErrors.put(error.getField(), error.getDefaultMessage());
    }

    PioneerPixelErrorDto errorDto =
        PioneerPixelErrorDto.builder().message("Validation failed").build();

    return handleExceptionInternal(ex, errorDto, headers, HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleGenericException(Exception ex, WebRequest request) {
    PioneerPixelErrorDto errorDto =
        PioneerPixelErrorDto.builder().message("Unexpected error: " + ex.getMessage()).build();
    return handleExceptionInternal(
        ex, errorDto, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
  }
}
