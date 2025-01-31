package com.polymath.jobboard.repositories;

import com.polymath.jobboard.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepositories extends JpaRepository<Users, Long> {
    Users findByEmail(String email);

}
