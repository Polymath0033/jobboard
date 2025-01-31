package com.polymath.jobboard.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Employers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "user_id",unique = true,nullable = false)
    private Users user;
    private String companyName;
    private String companyDescription;
    private String logoUrl;
    private String websiteUrl;
}
