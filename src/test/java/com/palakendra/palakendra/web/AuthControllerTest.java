package com.palakendra.palakendra.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.palakendra.palakendra.config.SecurityConfig;
import com.palakendra.palakendra.domain.entity.User;
import com.palakendra.palakendra.domain.entity.enums.Role;
import com.palakendra.palakendra.domain.repo.OrganizationRepository;
import com.palakendra.palakendra.domain.repo.UserRepository;
import com.palakendra.palakendra.dto.auth.LoginRequest;
import com.palakendra.palakendra.security.JwtAuthFilter;
import com.palakendra.palakendra.security.JwtService;
import com.palakendra.palakendra.service.OtpService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(
        controllers = AuthController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthFilter.class)
        }
)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    @MockBean AuthenticationManager authManager;
    @MockBean JwtService jwt;
    @MockBean UserRepository users;
    @MockBean OrganizationRepository orgs;
    @MockBean PasswordEncoder encoder;
    @MockBean OtpService otpService;

    @Test
    void login_returnsToken() throws Exception {
        var principal = org.springframework.security.core.userdetails.User
                .withUsername("admin").password("x").roles("ADMIN").build();
        Authentication authenticated = new UsernamePasswordAuthenticationToken(
                principal, "admin@123", principal.getAuthorities());
        when(authManager.authenticate(any(Authentication.class))).thenReturn(authenticated);

        var user = User.builder().id(1L).username("admin").role(Role.ADMIN).active(true).build();
        when(users.findByUsername("admin")).thenReturn(Optional.of(user));
        when(jwt.generate(eq("admin"), eq(Map.of("role","ADMIN","uid",1L)))).thenReturn("eyJ.mock.token");

        var req = new LoginRequest("admin", "admin@123");

        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(om.writeValueAsBytes(req)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.accessToken").value(containsString(".")));
    }
}
