package za.co.datacentrix.taskservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.datacentrix.taskservice.entities.User;
import za.co.datacentrix.taskservice.entities.UserPreferences;

import java.util.Optional;

public interface UserPreferencesRepository extends JpaRepository<UserPreferences, Long> {
    UserPreferences findByUserId(Long userId);

}
