package us.sportsanalytics.backend.services;

import java.time.LocalDateTime;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import us.sportsanalytics.backend.models.domain.User;
import us.sportsanalytics.backend.models.domain.roles.Role;
import us.sportsanalytics.backend.models.dto.auth.LoginRequest;
import us.sportsanalytics.backend.models.dto.auth.LoginResponse;
import us.sportsanalytics.backend.models.dto.auth.RegisterUserRequest;
import us.sportsanalytics.backend.repositories.user.UserRepository;
import us.sportsanalytics.backend.security.CustomUserDetails;
import us.sportsanalytics.backend.security.jwt.JwtService;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    // private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    public User registerUser(RegisterUserRequest request) {

        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }

        String hashedPassword = encoder.encode(request.password());

        User user = new User(
                null,
                request.email(),
                request.username(),
                hashedPassword,
                Role.USER,
                LocalDateTime.now());

        return userRepository.save(user);

    }

    public LoginResponse login(LoginRequest request) {

        // User user;

        // if (EmailChecker.isValid(request.identifier())) {
        // String email = request.identifier().trim();
        // user = userRepository.findByEmail(email).orElseThrow(() -> new
        // ResponseStatusException(
        // HttpStatus.UNAUTHORIZED, "Invalid Email/Username or Password"));
        // } else {
        // String username = request.identifier().trim();
        // user = userRepository.findByUsername(username).orElseThrow(() -> new
        // ResponseStatusException(
        // HttpStatus.UNAUTHORIZED, "Invalid Email/Username or Password"));
        // }

        // if (!encoder.matches(request.password(), user.passwordHash())) {
        // throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid
        // Email/Username or Password");
        // }

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.identifier(),
                        request.password()));

        // auth.isAuthenticated() == true
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();

        String token = jwtService.generateToken(user);

        return new LoginResponse(token);
    }

}
