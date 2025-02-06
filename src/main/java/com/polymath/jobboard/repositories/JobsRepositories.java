package com.polymath.jobboard.repositories;

import com.polymath.jobboard.models.Jobs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface JobsRepositories extends JpaRepository<Jobs, Long>, JpaSpecificationExecutor<Jobs> {
    Optional<Jobs> findAndDeleteById(Long id);
    Page<Jobs> findAllByTitleContainingIgnoreCase(String title,Pageable pageable);
//    Page<Jobs> findAllByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrLocationContainingIgnoreCaseOrCategoryContainingIgnoreCase(String search, Pageable pageable);
   // Page<Jobs> filterJobs();

}
