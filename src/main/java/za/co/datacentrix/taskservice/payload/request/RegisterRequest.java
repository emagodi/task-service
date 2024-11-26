package za.co.datacentrix.taskservice.payload.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import za.co.datacentrix.taskservice.enums.Role;
import za.co.datacentrix.taskservice.validation.StrongPassword;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "firstname is required")
    private String firstname;

    @NotBlank(message = "lastname is required")
    private String lastname;

    @Column(unique=true)
   // @NotBlank(message = "email is required")
    @Email(message = "email format is not valid")
    private String email;

    @NotBlank(message = "password is required")
    @StrongPassword
    private String password;

    @Column(unique=true)
    private String dcNumber;

    @NotNull
    private Role role;

    private Long departmentId;

}
