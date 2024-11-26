package za.co.datacentrix.taskservice.payload.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import za.co.datacentrix.taskservice.enums.Priority;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferencesRequest {
    private boolean emailNotifications;
    private String theme; // e.g., "light" or "dark"
    private String language; // e.g., "en", "fr", etc.
    private Priority priority; // e.g., LOW, MEDIUM, HIGH
    private boolean pushNotifications;


}