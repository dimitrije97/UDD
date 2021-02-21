package com.example.boilerplate.repository;

import com.example.boilerplate.entity.Book;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IBookRepository extends JpaRepository<Book, UUID> {

    Optional<Book> findOneByPath(String path);

    Optional<Book> findOneById(UUID id);
}
