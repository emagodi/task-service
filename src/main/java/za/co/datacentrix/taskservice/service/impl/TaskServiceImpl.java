package za.co.datacentrix.taskservice.service.impl;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import za.co.datacentrix.taskservice.entities.Task;
import za.co.datacentrix.taskservice.entities.User;
import za.co.datacentrix.taskservice.payload.request.TaskCreateRequest;
import za.co.datacentrix.taskservice.payload.request.TaskUpdateRequest;
import za.co.datacentrix.taskservice.repository.TaskRepository;
import za.co.datacentrix.taskservice.repository.UserRepository;
import za.co.datacentrix.taskservice.service.TaskService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import za.co.datacentrix.taskservice.utils.ObjectUtils;


import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.messaging.simp.SimpMessagingTemplate;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private SimpMessagingTemplate messagingTemplate; // Inject SimpMessagingTemplate

    public Task createTask(TaskCreateRequest taskCreateRequest) {
        User assignedUser = userRepository.findById(taskCreateRequest.getAssignedToUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Task task = new Task();
        task.setTitle(taskCreateRequest.getTitle());
        task.setDescription(taskCreateRequest.getDescription());
        task.setAssignedTo(assignedUser);
        task.setStatus(taskCreateRequest.getStatus());
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());

        Task savedTask = taskRepository.save(task);

        // Notify via WebSocket that a task was created
        messagingTemplate.convertAndSend("/topic/tasks", savedTask);

        return savedTask;
    }

    public Optional<Task> getTaskById(Long id) {
        String cacheKey = "task:" + id;

        // Check if the task is in the cache
        Task task = (Task) redisTemplate.opsForValue().get(cacheKey);
        if (task != null) {
            return Optional.of(task); // Return cached task
        }

        // Task not found in cache, retrieve from database
        Optional<Task> optionalTask = taskRepository.findById(id);
        optionalTask.ifPresent(t -> {
            // Store the task in cache with an optional expiration time
            redisTemplate.opsForValue().set(cacheKey, t, 5, TimeUnit.MINUTES);
        });

        return optionalTask; // Return the task, either from cache or database
    }

    public Task updateTask(Long id, TaskUpdateRequest taskUpdateRequest) {
        // Fetch the existing task
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // Copy non-null properties from the update request to the existing task
        ObjectUtils.copyNonNullProperties(taskUpdateRequest, task);

        // Handle the assignment of the user manually if it is provided
        if (taskUpdateRequest.getAssignedToUserId() != null) {
            User assignedUser = userRepository.findById(taskUpdateRequest.getAssignedToUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            task.setAssignedTo(assignedUser);
        }

        task.setUpdatedAt(LocalDateTime.now()); // Update the timestamp for the last update

        // Save the updated task
        Task updatedTask = taskRepository.save(task);

        // Invalidate the cache for this task
        String cacheKey = "task:" + id;
        redisTemplate.delete(cacheKey);

        // Notify via WebSocket that a task was updated
        messagingTemplate.convertAndSend("/topic/tasks", updatedTask);

        return updatedTask; // Return the updated task
    }

    public void deleteTask(Long id) {
        // Invalidate the cache for this task before deletion
        String cacheKey = "task:" + id;
        redisTemplate.delete(cacheKey);

        // Fetch the existing task to notify about its deletion
        Task taskToDelete = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // Delete the task from the repository
        taskRepository.deleteById(id);

        // Notify via WebSocket that a task was deleted
        messagingTemplate.convertAndSend("/topic/tasks", taskToDelete); // Optionally send deleted task info
    }

    public Page<Task> getTasksByUserId(Long userId, int page, int size) {
        String cacheKey = "tasks:user:" + userId + ":page:" + page + ":size:" + size;

        // Check if the tasks are in the cache
        Page<Task> cachedTasks = (Page<Task>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedTasks != null) {
            return cachedTasks; // Return cached tasks
        }

        // Fetch the user to ensure they exist
        User assignedUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create pageable object
        Pageable pageable = PageRequest.of(page, size);

        // Fetch tasks from the database
        Page<Task> tasks = taskRepository.findByAssignedTo(assignedUser, pageable);

        // Store the tasks in cache
        redisTemplate.opsForValue().set(cacheKey, tasks, 5, TimeUnit.MINUTES);

        return tasks; // Return the tasks from the database
    }

    public Page<Task> getAllTasks(int page, int size) {
        String cacheKey = "tasks:all:page:" + page + ":size:" + size;

        // Check if the tasks are in the cache
        Page<Task> cachedTasks = (Page<Task>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedTasks != null) {
            return cachedTasks; // Return cached tasks
        }

        // Create pageable object
        Pageable pageable = PageRequest.of(page, size);

        // Fetch tasks from the database
        Page<Task> tasks = taskRepository.findAll(pageable);

        // Store the tasks in cache
        redisTemplate.opsForValue().set(cacheKey, tasks, 5, TimeUnit.MINUTES);

        return tasks; // Return the tasks from the database
    }
}
