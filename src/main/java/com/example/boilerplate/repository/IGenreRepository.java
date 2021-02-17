package com.example.boilerplate.repository;

import com.example.boilerplate.entity.Genre;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IGenreRepository extends JpaRepository<Genre, UUID> {

    Optional<Genre> findOneByName(String name);

    Optional<Genre> findOneById(UUID id);
}
