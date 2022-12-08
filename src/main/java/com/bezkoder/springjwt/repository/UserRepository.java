package com.bezkoder.springjwt.repository;

// IMPORT "User" MODEL
import com.bezkoder.springjwt.models.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// "Repository" HAS ACCES TO "models"
@Repository
// FIND USER BY "username" OR "email" AND RETURN TRUE OR FALSE
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsername(String username);

  Boolean existsByUsername(String username);

  Boolean existsByEmail(String email);
}
