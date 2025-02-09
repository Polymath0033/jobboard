package com.polymath.jobboard.models;

import com.polymath.jobboard.models.enums.AlertFrequency;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class JobAlerts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "job_seeker_id",nullable = false)
    private JobSeekers jobSeeker;
    @Column(nullable = false)
    private String searchedQuery;
    @Enumerated(EnumType.STRING)
    private AlertFrequency frequency=AlertFrequency.DAILY;
}
