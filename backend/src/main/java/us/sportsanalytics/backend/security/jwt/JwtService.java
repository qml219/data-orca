package us.sportsanalytics.backend.security.jwt;

import java.util.Date;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;

import lombok.RequiredArgsConstructor;
// import us.sportsanalytics.backend.models.domain.User;
import us.sportsanalytics.backend.security.CustomUserDetails;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final SecretService secretService;
    private final long EXPIRATION = 1000 * 60 * 60;

    public String generateToken(CustomUserDetails user) {
        String userRole = user.getAuthorities().stream().findFirst().map(GrantedAuthority::getAuthority)
                .map(r -> r.startsWith("ROLE_") ? r.substring(5) : r)
                .orElseThrow(() -> new IllegalStateException("User has no role"));

        return Jwts.builder().subject(user.getId().toString()).claim("username", user.getUsername())
                .claim("role", userRole)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION)).signWith(secretService.getKey())
                .compact();
    }

    public String extractSubject(String token) {
        return Jwts.parser().verifyWith(secretService.getKey()).build().parseSignedClaims(token).getPayload()
                .getSubject();
    }
}
