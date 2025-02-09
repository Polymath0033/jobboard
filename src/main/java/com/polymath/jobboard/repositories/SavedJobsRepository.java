package com.polymath.jobboard.repositories;

import com.polymath.jobboard.models.JobSeekers;
import com.polymath.jobboard.models.Jobs;
import com.polymath.jobboard.models.SavedJobs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedJobsRepository extends JpaRepository<SavedJobs,Long> {
    boolean existsByJobsAndJobSeekers(Jobs job, JobSeekers jobSeekers);
    Optional<SavedJobs> findByIdAndJobSeekers(Long id, JobSeekers jobSeekers);
    List<SavedJobs> findAllByJobSeekers(JobSeekers jobSeekers);
    Optional<SavedJobs> findSavedJobsByIdAndJobSeekers(Long id, JobSeekers jobSeekers);
}
