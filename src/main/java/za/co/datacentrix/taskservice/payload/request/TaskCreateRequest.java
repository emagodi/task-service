package za.co.datacentrix.taskservice.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import za.co.datacentrix.taskservice.enums.Status;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskCreateRequest {

    private String title;
    private String description;
    private Long assignedToUserId; // Only store the user ID
    private Status status;

}
