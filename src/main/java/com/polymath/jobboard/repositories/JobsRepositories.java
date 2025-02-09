package com.polymath.jobboard.repositories;

import com.polymath.jobboard.models.Jobs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

public interface JobsRepositories extends JpaRepository<Jobs, Long>, JpaSpecificationExecutor<Jobs> {
    Optional<Jobs> findAndDeleteById(Long id);
    Page<Jobs> findAllByTitleContainingIgnoreCase(String title,Pageable pageable);
    @Query("""
select job from Jobs job where
lower(job.title) like lower(concat('%', :search, '%')) or
lower(job.description) like lower(concat('%', :search, '%')) or
lower(job.category) like lower(concat('%', :search, '%')) or
lower(job.location) like lower(concat('%', :search, '%')) or
lower(job.employers.companyName) like lower(concat('%', :search, '%') )
""")
    public Page<Jobs> advanceJobsSearch(@Param("search") String search, Pageable pageable);

    @Query(value = """
    select job.title,job.description,ts_rank(job.searchable_text,to_tsquery('english',:query)) as rank
     from Jobs job
     where job.searchable_text @@ to_tsquery('english',:query)
     order by
     rank DESC
""",nativeQuery = true)
    Page<Jobs> findSimilarJobs(@Param("query") String query, Pageable pageable);

}
