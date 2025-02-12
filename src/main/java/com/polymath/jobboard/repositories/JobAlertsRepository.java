package com.polymath.jobboard.repositories;

import com.polymath.jobboard.models.JobAlerts;
import com.polymath.jobboard.models.enums.AlertFrequency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobAlertsRepository extends JpaRepository<JobAlerts, Long> {
    boolean existsByJobSeekerId(Long id);
    JobAlerts findByJobSeekerId(Long id);
    List<JobAlerts> findAllByFrequency(AlertFrequency frequency);

  //  Optional<JobAlerts> findById(Long aLong);
}
