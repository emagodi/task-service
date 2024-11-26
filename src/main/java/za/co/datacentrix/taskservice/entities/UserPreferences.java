package za.co.datacentrix.taskservice.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import za.co.datacentrix.taskservice.enums.Priority;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "user_preferences")
public class UserPreferences {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private boolean emailNotifications;

    private String theme;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    private boolean pushNotifications;

    private String pushSubscription;


}
