package AdvSe.LMS.utils.validation;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Arrays;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationException> handleValidationExceptions(
            MethodArgumentNotValidException exception) {
        ValidationException validationException = new ValidationException();
        validationException.setStatus(400);
        exception.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationException.addError(fieldName + " " + errorMessage);
        });
        return new ResponseEntity<>(validationException, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ValidationException> handleEnumValidationException(HttpMessageNotReadableException exception) {
        String errorDetails = "Invalid request body.";

        if (exception.getCause() instanceof InvalidFormatException enumException) {
            if (enumException.getTargetType() != null && enumException.getTargetType().isEnum()) {
                errorDetails = String.format("Invalid enum value: '%s' for the field: '%s'. The value must be one of: %s.",
                        enumException.getValue(), enumException.getPath().getLast().getFieldName(), Arrays.toString(enumException.getTargetType().getEnumConstants()));
            }
        }
        ValidationException validationException = new ValidationException();
        validationException.setStatus(400);
        validationException.addError(errorDetails);
        return new ResponseEntity<>(validationException, HttpStatus.BAD_REQUEST);
    }

}
