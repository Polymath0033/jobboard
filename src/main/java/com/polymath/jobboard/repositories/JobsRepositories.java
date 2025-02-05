package com.polymath.jobboard.repositories;

import com.polymath.jobboard.models.Jobs;
import org.springframework.data.repository.CrudRepository;

public interface JobsRepositories extends CrudRepository<Jobs, Long> {
}
