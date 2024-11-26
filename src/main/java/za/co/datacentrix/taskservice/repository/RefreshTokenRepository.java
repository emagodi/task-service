package za.co.datacentrix.taskservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.datacentrix.taskservice.entities.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

}
