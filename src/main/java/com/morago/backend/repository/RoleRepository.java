package com.morago.backend.repository;

import com.morago.backend.entity.Role;
import com.morago.backend.entity.enumFiles.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(Roles name);
    List<Role> findAllByName(Roles name);

    default Optional<Role> findByNameEnum(String name) {
        try {
            return findByName(Roles.valueOf(name));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }
}
