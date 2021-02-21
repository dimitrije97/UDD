package com.example.boilerplate.repository;

import com.example.boilerplate.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserRepository extends JpaRepository<User, UUID> {

    Optional<User> findOneByEmail(String email);

    Optional<User> findOneById(UUID id);
}
