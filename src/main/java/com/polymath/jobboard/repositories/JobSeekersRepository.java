package com.polymath.jobboard.repositories;

import com.polymath.jobboard.models.JobSeekers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobSeekersRepository extends JpaRepository<JobSeekers, Long> {
}
