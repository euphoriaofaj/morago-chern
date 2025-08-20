package com.morago.backend.config;

import com.morago.backend.entity.User;
import com.morago.backend.entity.enumFiles.Roles;
import com.morago.backend.repository.RoleRepository;
import com.morago.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Order(2)
public class TestUserSeeder implements CommandLineRunner {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        final String phone = "01012345673";

        if (userRepository.existsByUsername(phone)) {
            return;
        }

        var clientRole = roleRepository.findByName(Roles.ROLE_ADMIN)
                .orElseThrow(() -> new IllegalStateException("ROLE_ADMIN not found"));

        var user = new User();
        user.setUsername(phone);
        user.setPassword(passwordEncoder.encode("123456"));
        user.setRoles(Set.of(clientRole));

        userRepository.save(user);
    }
}