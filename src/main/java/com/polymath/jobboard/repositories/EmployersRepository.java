package com.polymath.jobboard.repositories;

import com.polymath.jobboard.models.Employers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployersRepository extends JpaRepository<Employers, Long> {
}
