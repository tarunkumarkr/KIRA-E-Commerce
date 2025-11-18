package com.tekpyramid.kira.Authservice.service;

import com.tekpyramid.kira.Authservice.entity.Auth;
import com.tekpyramid.kira.Authservice.filter.CustomUserDetails;
import com.tekpyramid.kira.Authservice.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AuthRepository authRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Auth auth = authRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new CustomUserDetails(
                auth.getId(),                    // ⭐ CUSTOMER ID mapped correctly
                auth.getUserName(),              // ⭐ username
                auth.getPassword(),              // ⭐ password
                auth.getRole().name()            // ⭐ role
        );
    }
}
