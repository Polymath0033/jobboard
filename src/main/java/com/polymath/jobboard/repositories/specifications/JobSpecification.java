package com.polymath.jobboard.repositories.specifications;

import com.polymath.jobboard.dto.requests.AdvancedFilterRequest;
import com.polymath.jobboard.models.Employers;
import com.polymath.jobboard.models.Jobs;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JobSpecification {
    public static Specification<Jobs> advancedFilter(AdvancedFilterRequest filter) {
        return (root,query,criteriaBuilder)->{
            List<Predicate> predicates = new ArrayList<>();

            Join<Jobs, Employers> employersJoin = root.join("employers");

            if(filter.title()!=null&&!filter.title().trim().isEmpty()){
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%"+filter.title().toLowerCase()+"%"));
            }

            if(filter.companyName()!=null&&!filter.companyName().trim().isEmpty()){
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(employersJoin.get("companyName")), "%"+filter.companyName().toLowerCase()+"%"));
            }

            if(filter.description()!=null&&!filter.description().trim().isEmpty()){
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%"+filter.description().toLowerCase()+"%"));
            }

            if(filter.location()!=null&&!filter.location().trim().isEmpty()){
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("location")), "%"+filter.location().toLowerCase()+"%"));
            }

            if (filter.category()!=null&&!filter.category().trim().isEmpty()){
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("category")), "%"+filter.category().toLowerCase()+"%"));
            }

            if(filter.minSalary()!=null&&filter.minSalary()>=0){
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("salary"), filter.minSalary()));
            }

            if(filter.maxSalary()!=null&&filter.maxSalary()>=0){
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("salary"), filter.maxSalary()));
            }

            if(filter.startsAt()!=null){
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("postedAt"), filter.startsAt()));
            }

            if(filter.endsAt()!=null){
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("postedAt"), filter.endsAt()));
            }

            predicates.add(criteriaBuilder.or(criteriaBuilder.isNull(root.get("expiresAt")),criteriaBuilder.greaterThan(root.get("expiresAt"), LocalDateTime.now())));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
