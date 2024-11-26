package za.co.datacentrix.taskservice.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import za.co.datacentrix.taskservice.enums.Role;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {

    private String firstname;

    private String lastname;

    @Email(message = "Please enter a valid email format")
    private String email;

    private String password;

    private Role role;

    private Long departmentId;
}
