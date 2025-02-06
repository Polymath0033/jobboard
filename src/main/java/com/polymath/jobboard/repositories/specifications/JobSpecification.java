package com.polymath.jobboard.repositories.specifications;

import com.polymath.jobboard.models.Jobs;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JobSpecification {
    public static Specification<Jobs> filterJobByTitleOrDescriptionOrLocationOrCategoryOrSalaryRange(String title, String description, String location, String category, Double minSalary, Double maxSalary, LocalDateTime startsAt,LocalDateTime endsAt) {
        return (root,query,criteriaBuilder)->{
            List<Predicate> predicates = new ArrayList<>();

            if(title!=null&&!title.isEmpty()){
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%"+title+"%"));
            }

            if(description!=null&&!description.isEmpty()){
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%"+description+"%"));
            }

            if(location!=null&&!location.isEmpty()){
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("location")), "%"+location+"%"));
            }

            if (category!=null&&!category.isEmpty()){
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("category")), "%"+category+"%"));
            }

            if(minSalary!=null&&minSalary>0){
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("salary"), minSalary));
            }

            if(maxSalary!=null&&maxSalary>0){
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("salary"), maxSalary));
            }

            if(startsAt!=null){
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("postedAt"), startsAt));
            }

            if(endsAt!=null){
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("postedAt"), endsAt));
            }

            predicates.add(criteriaBuilder.or(criteriaBuilder.isNull(root.get("expiresAt")),criteriaBuilder.greaterThan(root.get("expiresAt"), LocalDateTime.now())));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
