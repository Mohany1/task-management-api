package com.mohamed.taskmanager.config;

import com.mohamed.taskmanager.model.Role;
import com.mohamed.taskmanager.model.User;
import com.mohamed.taskmanager.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/** Creates a default admin (admin / admin123) on first start. Change it in real use. */
@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedAdmin(UserRepository users, PasswordEncoder encoder) {
        return args -> {
            if (!users.existsByUsername("admin")) {
                users.save(new User("admin", "admin@example.com", encoder.encode("admin123"), Role.ADMIN));
            }
        };
    }
}
