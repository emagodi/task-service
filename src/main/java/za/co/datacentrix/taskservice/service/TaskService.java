package za.co.datacentrix.taskservice.service;

import org.springframework.data.domain.Page;
import za.co.datacentrix.taskservice.entities.Task;
import za.co.datacentrix.taskservice.payload.request.TaskCreateRequest;
import za.co.datacentrix.taskservice.payload.request.TaskUpdateRequest;

import java.util.Optional;

public interface TaskService {

    public Task createTask(TaskCreateRequest taskCreateRequest);
    public Optional<Task> getTaskById(Long id);
    public Task updateTask(Long id, TaskUpdateRequest taskUpdateRequest);
    public void deleteTask(Long id);
    public Page<Task> getTasksByUserId(Long userId, int page, int size);

    public Page<Task> getAllTasks(int page, int size);


}
