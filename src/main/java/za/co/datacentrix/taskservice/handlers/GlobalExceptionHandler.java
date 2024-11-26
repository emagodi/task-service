package za.co.datacentrix.taskservice.handlers;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import za.co.datacentrix.taskservice.exception.AuthenticationException;
import za.co.datacentrix.taskservice.exception.InvalidPasswordException;
import za.co.datacentrix.taskservice.exception.UserNotFoundException;
;

import java.util.HashMap;

@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<String> handleInvalidPasswordException(InvalidPasswordException exp){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exp.getMsg());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException exp){
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exp.getMsg());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<String> handleAuthException(AuthenticationException exp){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exp.getMsg());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException exp){
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exp.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<za.co.datacentrix.taskservice.handlers.ErrorResponse> handleMethodArgException(MethodArgumentNotValidException exp){

        var errors = new HashMap<String, String >();
        exp.getBindingResult().getAllErrors()
                .forEach(error -> {
                    var fieldName = ((FieldError)error).getField();
                    var errorMessage = error.getDefaultMessage();

                    errors.put(fieldName, errorMessage);
                });
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse());
    }
}
