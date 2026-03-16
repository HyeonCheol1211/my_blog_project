package com.blog.backend.exception;

import com.blog.backend.exception.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({
            PostNotFoundException.class,
            UserNotFoundException.class,
            CommentNotFoundException.class,
            CategoryNotFoundException.class,
            AlreadyDeleteException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFoundExceptions(Exception e) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler({
            AccessDeniedException.class,
            PasswordNotCorrectException.class,
            AuthorOnlyException.class,
            LoginUserNotMatchException.class
    })
    public ResponseEntity<ErrorResponse> handleForbiddenExceptions(Exception e) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, e);
    }

    @ExceptionHandler({
            DuplicateUsernameException.class,
            DuplicateEmailException.class,
            AlreadyAddException.class
    })
    public ResponseEntity<ErrorResponse> handleConflictExceptions(Exception e) {
        return buildErrorResponse(HttpStatus.CONFLICT, e);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, Exception e) {
        log.error("{} 에러 발생: {}", status.value(), e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(status.value())
                .error(status.name())
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(status).body(errorResponse);
    }
}