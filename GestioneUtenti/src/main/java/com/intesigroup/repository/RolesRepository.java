package com.intesigroup.repository;

import com.intesigroup.model.Roles;
import com.intesigroup.model.RolesType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolesRepository extends JpaRepository<Roles, Long> {

    Optional<Roles> findByNameRole(RolesType nameRole);
}