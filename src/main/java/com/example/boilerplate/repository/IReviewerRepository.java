package com.example.boilerplate.repository;

import com.example.boilerplate.entity.Reviewer;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IReviewerRepository extends JpaRepository<Reviewer, UUID> {

    Optional<Reviewer> findOneById(UUID id);
}
