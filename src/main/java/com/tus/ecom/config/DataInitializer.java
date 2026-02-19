package com.tus.ecom.config;

import com.tus.ecom.model.RoleEntity;
import com.tus.ecom.model.UserEntity;
import com.tus.ecom.repository.RoleRepository;
import com.tus.ecom.repository.UserRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Value("${app.bootstrap.admin.username}")
    private String adminUsername;

    @Value("${app.bootstrap.admin.password}")
    private String adminPassword;

    @Value("${app.bootstrap.customer.username}")
    private String customerUsername;

    @Value("${app.bootstrap.customer.password}")
    private String customerPassword;

    @Override
    public void run(String @NonNull ... args) {

        createRoleIfNotExists("ADMIN");
        createRoleIfNotExists("CUSTOMER");

        RoleEntity adminRole = roleRepository.findByName("ADMIN").orElseThrow();
        RoleEntity customerRole = roleRepository.findByName("CUSTOMER").orElseThrow();

        createUserIfMissing(adminUsername, adminPassword, adminRole);
        createUserIfMissing(customerUsername, customerPassword, customerRole);

    }

    private void createUserIfMissing(String username, String rawPassword, RoleEntity role) {
        if (userRepository.findByUsername(username).isEmpty()) {
            UserEntity user = new UserEntity();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(rawPassword));
            user.setRole(role);
            userRepository.save(user);
        }
    }

    private void createRoleIfNotExists(String roleName) {
        roleRepository.findByName(roleName)
                .orElseGet(() -> {
                    RoleEntity role = new RoleEntity();
                    role.setName(roleName);
                    return roleRepository.save(role);
                });
    }

}
