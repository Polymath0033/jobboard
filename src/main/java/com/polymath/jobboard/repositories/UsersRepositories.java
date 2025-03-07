package com.polymath.jobboard.repositories;

import com.polymath.jobboard.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepositories extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);
}
