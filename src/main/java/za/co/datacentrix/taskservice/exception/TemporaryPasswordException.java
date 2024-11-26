package za.co.datacentrix.taskservice.exception;

public class TemporaryPasswordException extends RuntimeException{
    public TemporaryPasswordException(String message) {
        super(message);
    }
}
