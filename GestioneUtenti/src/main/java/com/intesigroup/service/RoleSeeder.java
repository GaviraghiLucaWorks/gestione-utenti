package com.intesigroup.service;

import com.intesigroup.model.Roles;
import com.intesigroup.model.RolesType;
import com.intesigroup.repository.RolesRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RoleSeeder implements CommandLineRunner {

    private final RolesRepository rolesRepository;

    public RoleSeeder(RolesRepository rolesRepository) {
        this.rolesRepository = rolesRepository;
    }

    @Override
    public void run(String... args) {
        for (RolesType roleType : RolesType.values()) {
            if (!rolesRepository.findByNameRole(roleType).isPresent()) {
                Roles newRole = new Roles(roleType);
                rolesRepository.save(newRole);
            }
        }
    }
}