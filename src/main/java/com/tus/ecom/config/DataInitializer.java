package com.tus.ecom.config;

import com.tus.ecom.model.ProductEntity;
import com.tus.ecom.model.RoleEntity;
import com.tus.ecom.model.UserEntity;
import com.tus.ecom.repository.ProductRepository;
import com.tus.ecom.repository.RoleRepository;
import com.tus.ecom.repository.UserRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProductRepository productRepository;

    public DataInitializer(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder, ProductRepository productRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.productRepository = productRepository;
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

        final String electronicsCategory = "Electronics";

        final List<ProductEntity> products = Arrays.asList(
                createProduct("Wireless Mouse", "Ergonomic mouse with RGB lighting", electronicsCategory, "Logitech", "images/mouse.png", new BigDecimal("29.99"), 45),
                createProduct("Mechanical Keyboard", "RGB mechanical keyboard with blue switches", electronicsCategory, "Keychron", "images/keyboard.png", new BigDecimal("89.50"), 18),
                createProduct("4K Monitor 27\"", "Ultra-thin 4K IPS monitor", electronicsCategory, "Dell", "images/monitor.png", new BigDecimal("249.99"), 12),
                createProduct("Noise Cancelling Headphones", "Over-ear wireless ANC headphones", "Audio", "Sony", "images/headphones.png", new BigDecimal("349.00"), 7),
                createProduct("Coffee Maker", "Automatic drip coffee machine", "Home", "Ninja", "images/coffeemaker.png", new BigDecimal("79.99"), 22)
        );

        if (productRepository.count() == 0) {
            productRepository.saveAll(products);
        }


    }

    private ProductEntity createProduct(String name, String description, String category, String brand, String image, BigDecimal price, Integer quantity) {
        ProductEntity p = new ProductEntity();
        p.setName(name);
        p.setDescription(description);
        p.setCategory(category);
        p.setBrand(brand);
        p.setImage(image);
        p.setPrice(price);
        p.setQuantity(quantity);
        return p;
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
        if (roleRepository.findByName(roleName).isEmpty()) {
            RoleEntity role = new RoleEntity();
            role.setName(roleName);
            roleRepository.save(role);
        }
    }

}
