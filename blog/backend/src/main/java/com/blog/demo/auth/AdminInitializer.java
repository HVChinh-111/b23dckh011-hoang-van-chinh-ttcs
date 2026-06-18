package com.blog.demo.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.blog.demo.auth.entity.AdminAccount;
import com.blog.demo.auth.repository.AdminAccountRepository;
import com.blog.demo.common.config.AdminSeedProperties;
import com.blog.demo.profile.entity.AuthorProfile;
import com.blog.demo.profile.repository.AuthorProfileRepository;

import lombok.RequiredArgsConstructor;

/**
 * Seeds the single admin account and its (empty) author profile on first
 * startup (BR05.1). The system is closed — there is no public registration.
 */
@Component
@org.springframework.core.annotation.Order(1)
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminInitializer.class);

    private final AdminAccountRepository adminAccountRepository;
    private final AuthorProfileRepository authorProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminSeedProperties adminSeedProperties;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (adminAccountRepository.count() > 0) {
            return;
        }
        AdminAccount admin = new AdminAccount(
                adminSeedProperties.email(),
                passwordEncoder.encode(adminSeedProperties.password()));
        admin = adminAccountRepository.save(admin);

        AuthorProfile profile = new AuthorProfile(admin);
        profile.setFullName("Admin");
        profile.setContactEmail(adminSeedProperties.email());
        authorProfileRepository.save(profile);

        log.info("Seeded admin account: {}", adminSeedProperties.email());
    }
}
