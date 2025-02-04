package com.polymath.jobboard.repositories;

import com.polymath.jobboard.models.JobSeekers;
import com.polymath.jobboard.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JobSeekersRepository extends JpaRepository<JobSeekers, Long> {

    //Optional<JobSeekers> findById(Long id);
    Optional<JobSeekers> findByUser(Users user);

}
