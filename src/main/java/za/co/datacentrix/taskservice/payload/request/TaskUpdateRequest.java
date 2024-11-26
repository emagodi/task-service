package za.co.datacentrix.taskservice.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import za.co.datacentrix.taskservice.enums.Status;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskUpdateRequest {

    private String title;
    private String description;
    private Long assignedToUserId;
    private Status status;

}