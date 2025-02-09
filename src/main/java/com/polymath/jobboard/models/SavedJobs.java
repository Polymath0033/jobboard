package com.polymath.jobboard.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
public class SavedJobs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "job_id",nullable = false)
    private Jobs jobs;
    @ManyToOne
    @JoinColumn(name = "job_seeker_id",nullable = false)
    private JobSeekers jobSeekers;
    @CreationTimestamp
    private LocalDateTime savedAt;
}
