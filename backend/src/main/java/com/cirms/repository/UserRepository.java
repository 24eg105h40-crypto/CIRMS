package com.cirms.repository;

import com.cirms.entity.Role;
import com.cirms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<User> findByStudentId(String studentId);
    boolean existsByStudentId(String studentId);
    List<User> findByRole(Role role);
}
