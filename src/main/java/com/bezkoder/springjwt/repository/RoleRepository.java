package com.bezkoder.springjwt.repository;

// "ERole" AND "Role" MODELS ARE IMPORTED
import com.bezkoder.springjwt.models.ERole;
import com.bezkoder.springjwt.models.Role;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// "Repository" HAS ACCES TO "models"
@Repository
// FIND USER BY "ERole"
public interface RoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByName(ERole name);
}
