package za.co.datacentrix.taskservice.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class InvalidPasswordException extends RuntimeException{

    private final String msg;
}
