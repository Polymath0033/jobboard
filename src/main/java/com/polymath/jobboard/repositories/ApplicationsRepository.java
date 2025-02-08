package com.polymath.jobboard.repositories;

import com.polymath.jobboard.models.Applications;
import com.polymath.jobboard.models.JobSeekers;
import com.polymath.jobboard.models.Jobs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationsRepository extends JpaRepository<Applications, Long> {
    boolean existsByJobsAndJobSeekers(Jobs jobs, JobSeekers jobSeekers);
    List<Applications> findAllByJobSeekers(JobSeekers jobSeekers);
    @Query("""
select applications from Applications applications join fetch applications.jobs jobs join fetch jobs.employers employers join fetch employers.user user
where jobs.id=:jobId and user.email=:email
""")
    List<Applications> findAllApplicationByJobIdAndEmployerEmail(Long jobId,String email);

    @Query("""
select applications from Applications applications join fetch applications.jobs jobs join fetch jobs.employers employers join fetch employers.user user
where user.email=:employerEmail
""")
    List<Applications> findAllByJobsEmployerEmail(String employerEmail);

    @Query("""
select applications from Applications applications
join fetch applications.jobs jobs
join fetch jobs.employers employers
join fetch employers.user user
where applications.id=:applicationId
and applications.jobSeekers.id=:jobSeekerId
and user.email=:employerEmail
""")
    Optional<Applications> findApplicationsByApplicationJobSeekerIdEmployerEmail(Long applicationId,Long jobSeekerId,String employerEmail);

    @Query("""
select applications from Applications applications join fetch applications.jobs jobs where applications.id=:applicationId and jobs.id=:jobId and applications.jobSeekers.id=:jobSeekerId
""")
    Optional<Applications> findApplicationsByApplicationJobIdJobSeekerId(Long applicationId,Long jobId,Long jobSeekerId);
}
