package hanghaeboard.util;

import hanghaeboard.domain.user.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${spring.jwt.secret-key}")
    private String SECRET_KEY;
    @Value("${spring.jwt.expiration-time}")
    private long EXPIRATION_TIME;

    public String generateToken(User user, LocalDateTime now) {

        return Jwts.builder()
                .header().add("typ", "JWT")
                .and()
                .issuer("superAdmin")
                .subject(user.getUsername())
                .claim("role", user.getRole())
                .issuedAt(convertToDate(now))
                .expiration(convertToDate(now.plusSeconds(EXPIRATION_TIME)))
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                .compact();
    }

    public boolean validateToken(String token) {
        Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                .build()
                .parseSignedClaims(token);

        return true;
    }

    public String getUsername(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    private Date convertToDate(LocalDateTime expiration) {
        return Date.from(expiration.atZone(ZoneId.systemDefault()).toInstant());
    }
}
