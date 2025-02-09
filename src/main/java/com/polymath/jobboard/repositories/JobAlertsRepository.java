package com.polymath.jobboard.repositories;

import com.polymath.jobboard.models.JobAlerts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JobAlertsRepository extends JpaRepository<JobAlerts, Long> {
    boolean existsByJobSeekerId(Long id);
    JobAlerts findByJobSeekerId(Long id);

  //  Optional<JobAlerts> findById(Long aLong);
}
