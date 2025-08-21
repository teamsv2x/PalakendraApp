package com.palakendra.palakendra.security;

import com.palakendra.palakendra.domain.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository users;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String id = username == null ? "" : username.trim();
        var u = users.findByUsername(id)
                .or(() -> users.findByPhone(id))          // <- optional fallback
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        String pw = (u.getPassword() == null ? "" : u.getPassword()); // allow OTP-only users

        return org.springframework.security.core.userdetails.User
                .withUsername(u.getUsername())                  // make sure this matches JWT `sub`
                .password(pw)                                   // bcrypt for admins/managers, empty for OTP users
                .roles(u.getRole().name())                      // e.g. CUSTOMER -> ROLE_CUSTOMER
                .disabled(!u.isActive())
                .build();
    }
}
