package com.foodapp.auth;

import com.foodapp.auth.dto.*;
import com.foodapp.common.exception.BadRequestException;
import com.foodapp.security.JwtTokenProvider;
import com.foodapp.user.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtTokenProvider tokenProvider;
    @Mock private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("Register — success with new email")
    void register_success() {
        SignupRequest request = new SignupRequest("Test User", "test@email.com", "pass123", "9999999999", Role.CUSTOMER);

        when(userRepository.existsByEmail("test@email.com")).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("$2a$encoded");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(tokenProvider.generateToken(authentication)).thenReturn("access-token");
        when(tokenProvider.generateRefreshToken("test@email.com")).thenReturn("refresh-token");

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals("Test User", response.getName());
        assertEquals(Role.CUSTOMER, response.getRole());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Register — fails with duplicate email")
    void register_duplicateEmail_throws() {
        SignupRequest request = new SignupRequest("Test", "exists@email.com", "pass", null, Role.CUSTOMER);
        when(userRepository.existsByEmail("exists@email.com")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Login — success with valid credentials")
    void login_success() {
        LoginRequest request = new LoginRequest("test@email.com", "password");
        User user = User.builder().id(1L).name("Test").email("test@email.com").role(Role.CUSTOMER).build();

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(user));
        when(tokenProvider.generateToken(authentication)).thenReturn("access-token");
        when(tokenProvider.generateRefreshToken("test@email.com")).thenReturn("refresh-token");

        AuthResponse response = authService.login(request);

        assertEquals("access-token", response.getAccessToken());
        assertEquals("Test", response.getName());
    }

    @Test
    @DisplayName("Login — fails with bad credentials")
    void login_badCredentials_throws() {
        LoginRequest request = new LoginRequest("test@email.com", "wrong");
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }
}
