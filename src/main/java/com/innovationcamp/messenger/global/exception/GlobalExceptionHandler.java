package com.innovationcamp.messenger.global.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.innovationcamp.messenger.domain.user.entity.User;
import jakarta.validation.ValidationException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@RestControllerAdvice
@Slf4j(topic = "GlobalExceptionHandler")
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponseDto> illegalArgumentExceptionHandler(IllegalArgumentException e) {
        ExceptionResponseDto responseDto = new ExceptionResponseDto(HttpStatus.BAD_REQUEST,e.getClass().getSimpleName(), e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
    }


    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ExceptionResponseDto> nullPointerExceptionHandler(NullPointerException e) {
        ExceptionResponseDto responseDto = new ExceptionResponseDto(HttpStatus.BAD_REQUEST,e.getClass().getSimpleName(), e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<ExceptionResponseDto> jsonProcessingExceptionHandler(JsonProcessingException e) {
        ExceptionResponseDto responseDto = new ExceptionResponseDto(HttpStatus.BAD_REQUEST,e.getClass().getSimpleName(), e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ExceptionResponseDto> validationExceptionHandler(ValidationException e) {
        ExceptionResponseDto responseDto = new ExceptionResponseDto(HttpStatus.BAD_REQUEST,e.getClass().getSimpleName(), e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponseDto> validationExceptionHandler(MethodArgumentNotValidException e) {
        ExceptionResponseDto responseDto = new ExceptionResponseDto(HttpStatus.BAD_REQUEST,e.getClass().getSimpleName(), e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
    }
}