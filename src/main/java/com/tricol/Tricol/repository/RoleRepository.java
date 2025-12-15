package com.tricol.Tricol.repository;

import com.tricol.Tricol.model.RoleApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleApp, Long> {
    Optional<RoleApp> findByName(String name);
}
