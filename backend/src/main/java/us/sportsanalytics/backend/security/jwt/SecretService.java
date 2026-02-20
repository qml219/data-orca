package us.sportsanalytics.backend.security.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Service
public class SecretService {

    private final SecretKey jwtSecret;

    public SecretService(@Value("${JWT_SECRET}") String jwtSecret) {
        byte[] decoded = java.util.Base64.getDecoder().decode(jwtSecret);
        this.jwtSecret = new SecretKeySpec(decoded, "HmacSHA256");
    }

    public SecretKey getKey() {
        return this.jwtSecret;
    }
}
