package com.example.boilerplate.repository;

import com.example.boilerplate.entity.Writer;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IWriterRepository extends JpaRepository<Writer, UUID> {

    Optional<Writer> findOneById(UUID id);
}
