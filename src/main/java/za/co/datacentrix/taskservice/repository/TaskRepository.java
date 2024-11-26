package za.co.datacentrix.taskservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import za.co.datacentrix.taskservice.entities.Task;
import za.co.datacentrix.taskservice.entities.User;

import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findByAssignedTo(User user, Pageable pageable);

}
