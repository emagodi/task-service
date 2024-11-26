package za.co.datacentrix.taskservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import za.co.datacentrix.taskservice.entities.Task;
import za.co.datacentrix.taskservice.payload.request.TaskCreateRequest;
import za.co.datacentrix.taskservice.service.TaskService;
import za.co.datacentrix.taskservice.payload.request.TaskUpdateRequest;

import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Task> createTask(@RequestBody TaskCreateRequest taskCreateRequest) {
        Task createdTask = taskService.createTask(taskCreateRequest);
        return ResponseEntity.status(201).body(createdTask);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        Optional<Task> task = taskService.getTaskById(id);
        return task.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Task> updateTask(
            @PathVariable Long id,
            @RequestBody TaskUpdateRequest taskUpdateRequest) {

        Task updatedTask = taskService.updateTask(id, taskUpdateRequest);
        return ResponseEntity.ok(updatedTask); // Return the updated task with 200 OK status
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Page<Task>> getTasksByUserId(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Task> tasks = taskService.getTasksByUserId(userId, page, size);
        return ResponseEntity.ok(tasks);
    }
}
