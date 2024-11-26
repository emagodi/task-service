package za.co.datacentrix.taskservice.payload.request;

public record ChangePassword(String password, String repeatPassword) {
}