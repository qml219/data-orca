package us.sportsanalytics.backend.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import us.sportsanalytics.backend.models.domain.User;
import us.sportsanalytics.backend.models.dto.auth.LoginRequest;
import us.sportsanalytics.backend.models.dto.auth.LoginResponse;
import us.sportsanalytics.backend.models.dto.auth.RegisterUserRequest;
import us.sportsanalytics.backend.services.AuthService;

@RestController
@RequestMapping("api/auth")
public class AuthController {
    private final AuthService authService;
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public User register(@RequestBody RegisterUserRequest request) {
        return authService.registerUser(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
