package com.polymath.jobboard.repositories;

import com.polymath.jobboard.models.JobSeekers;
import com.polymath.jobboard.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobSeekersRepository extends JpaRepository<JobSeekers, Long> {

    //Optional<JobSeekers> findById(Long id);
    Optional<JobSeekers> findByUser(Users user);
    @Query("""
    select jobSeeker from JobSeekers jobSeeker where jobSeeker.user.email=:email
""")
    Optional<JobSeekers> findByJobSeekerEmail(String email);
}
