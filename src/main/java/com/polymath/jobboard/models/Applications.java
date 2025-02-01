package com.polymath.jobboard.models;

import com.polymath.jobboard.models.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
public class Applications {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "job_id",nullable = false)
    private Jobs jobs;
    @ManyToOne
    @JoinColumn(name = "job_seeker_id",nullable = false)
    private JobSeekers jobSeekers;
    @Column(nullable = false)
    private String resumeUrl;
    private String coverLetter;
    @CreationTimestamp
    @Column(nullable = false,updatable = false)
    private LocalDateTime appliesAt;
    @Column(nullable = false)
    private ApplicationStatus status;
}
