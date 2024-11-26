package za.co.datacentrix.taskservice.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import za.co.datacentrix.taskservice.enums.Status;
import za.co.datacentrix.taskservice.handlers.BaseEntity;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "tasks")
public class Task extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User assignedTo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;


}
