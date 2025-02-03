package com.polymath.jobboard.repositories;

import com.polymath.jobboard.models.SavedJobs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SavedJobsRepository extends JpaRepository<SavedJobs,Long> {
}
