package com.polymath.jobboard.repositories;

import com.polymath.jobboard.models.Employers;
import com.polymath.jobboard.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployersRepository extends JpaRepository<Employers, Long> {
    Optional<Employers> findByUser(Users user);
}
