package za.co.datacentrix.taskservice.payload.request;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {
    @NotBlank(message = "New password cannot be blank")
    private String newPassword;

    @NotBlank(message = "Old password cannot be blank")
    private String oldPassword;

}