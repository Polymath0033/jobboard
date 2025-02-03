package com.polymath.jobboard.repositories;

import com.polymath.jobboard.models.JobAlerts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobAlertsRepository extends JpaRepository<JobAlerts, Long> {
}
