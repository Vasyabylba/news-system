package ru.clevertec.exceptionhandler.handler;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.ConstraintViolationException;
import lombok.Builder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.clevertec.exceptionhandler.exception.ResourceBadRequestException;
import ru.clevertec.exceptionhandler.exception.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * A controller advice for exception handling
 */
@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(value = ResourceNotFoundException.class, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseEntity<GenericErrorResponse> handleUnprocessableEntityException(Exception ex) {
        return ResponseEntity.unprocessableEntity()
                .body(generateGenericErrorResponse(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY));
    }

    @ExceptionHandler(value = ResourceBadRequestException.class, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<GenericErrorResponse> handleBadRequestException(Exception ex) {
        return ResponseEntity.badRequest()
                .body(generateGenericErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ValidationErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex
    ) {
        List<Violation> violations = ex.getBindingResult().getAllErrors().stream()
                .map(objectError -> {
                    String property = objectError instanceof FieldError error ?
                            error.getField() :
                            objectError.getObjectName();
                    String message = objectError.getDefaultMessage();
                    return new Violation(property, message);
                })
                .toList();
        return ResponseEntity.badRequest()
                .body(generateValidationErrorResponse(violations));
    }

    @ExceptionHandler(value = ConstraintViolationException.class, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ValidationErrorResponse> handleConstraintValidationException(
            ConstraintViolationException ex
    ) {
        List<Violation> violations = ex.getConstraintViolations().stream()
                .map(violation ->
                        new Violation(
                                violation.getPropertyPath().toString(),
                                violation.getMessage()
                        ))
                .toList();
        return ResponseEntity.badRequest()
                .body(generateValidationErrorResponse(violations));
    }

    private GenericErrorResponse generateGenericErrorResponse(String message, HttpStatus httpStatus) {
        return GenericErrorResponse.builder()
                .code(httpStatus.value())
                .status(httpStatus)
                .message(message)
                .time(getCurrentDateTime())
                .build();
    }

    private ValidationErrorResponse generateValidationErrorResponse(List<Violation> violations) {
        return ValidationErrorResponse.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .status(HttpStatus.BAD_REQUEST)
                .errors(violations)
                .time(getCurrentDateTime())
                .build();
    }

    private LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now();
    }

    @Builder
    public record Violation(String property, String message) {

    }

    @Builder
    public record GenericErrorResponse(

            int code,

            HttpStatus status,

            String message,

            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime time

    ) {

    }

    @Builder
    public record ValidationErrorResponse(

            int code,

            HttpStatus status,

            List<Violation> errors,

            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime time

    ) {

    }

}
