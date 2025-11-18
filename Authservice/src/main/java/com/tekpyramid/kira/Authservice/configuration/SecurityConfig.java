package com.tekpyramid.kira.Authservice.configuration;

import com.tekpyramid.kira.Authservice.filter.SecurityFilter;
import com.tekpyramid.kira.Authservice.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final SecurityFilter securityFilter;

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                        // PUBLIC
                        .requestMatchers(
                                "/api/v1/auth/login",
                                "/api/v1/auth/signup/customer",
                                "/api/v1/auth/signup/vendor",
                                "/api/v1/customers/signup/customer"
                        ).permitAll()

                        // INTERNAL
                        .requestMatchers("/auth/**").authenticated()

                        // ============================
                        // CATEGORY ENDPOINTS (ADMIN)
                        // ============================
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/categories/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/auth/categories/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/auth/categories/**")
                        .hasAuthority("ADMIN")

                        // ============================
                        // CUSTOMER
                        // ============================
                        .requestMatchers(HttpMethod.GET, "/api/v1/auth/customers").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/auth/customers/*").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/auth/customers/*").hasAuthority("CUSTOMER")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/auth/customers/*").hasAnyAuthority("ADMIN", "CUSTOMER")

                        // ============================
                        // CART
                        // ============================
                        .requestMatchers("/api/v1/cart/**").hasAuthority("CUSTOMER")

                        // ============================
                        // VENDOR
                        // ============================
                        .requestMatchers(HttpMethod.GET, "/api/v1/vendors/*").hasAnyAuthority("VENDOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/vendors/all").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/vendors/*").hasAuthority("VENDOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/vendors/*").hasAnyAuthority("ADMIN", "VENDOR")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/vendors/*/status").hasAuthority("ADMIN")

                        .anyRequest().authenticated()
                )

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
