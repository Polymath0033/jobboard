package com.polymath.jobboard.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class JobSeekers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "user_id",nullable = false,unique = true)
    private Users user;
    private String firstName;
    private String lastName;
    private String resumeUrl;
    private String skills;
//    Wanted to change this to allow longer text
    private String experiences;
}
