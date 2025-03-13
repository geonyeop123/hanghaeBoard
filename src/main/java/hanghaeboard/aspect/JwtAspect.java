package hanghaeboard.aspect;

import hanghaeboard.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Aspect
public class JwtAspect {

    private final JwtUtil jwtUtil;
    private final HttpServletRequest httpServletRequest;

    @Before("@annotation(hanghaeboard.annotation.AuthCheck)")
    public void authCheck() {
        String token = httpServletRequest.getHeader("Authorization");

        if(token == null || !token.startsWith("Bearer ")) {
            throw new JwtException("유효하지 않은 JWT 토큰입니다.");
        }

        token = token.substring(7);
        jwtUtil.validateToken(token);

    }
}
