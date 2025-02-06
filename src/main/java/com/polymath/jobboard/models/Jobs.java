package com.polymath.jobboard.models;

import com.polymath.jobboard.models.enums.JobStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
public class Jobs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "employer_id",nullable = false)
    private Employers employers;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String description;
    private String location;
    private String category;
    private Double salary;
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime postedAt;
    private LocalDateTime expiresAt;
    @Enumerated(EnumType.STRING)
    private JobStatus status;
}
