package com.kira.userservice.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component("auditorAware")
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        // 🧠 Later: extract username from JWT token
        return Optional.of("SYSTEM"); // for now, static fallback
    }
}
