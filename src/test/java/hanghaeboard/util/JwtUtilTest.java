package hanghaeboard.util;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class JwtUtilTest {

    @Autowired
    JwtUtil jwtUtil;

    @DisplayName("토큰을 발급받을 수 있다.")
    @Test
    void generateToken() {
        // given
        String username = "yeop";

        LocalDateTime now = LocalDateTime.now();

        // when
        String token = jwtUtil.generateToken(username, now);

        // then
        assertThat(token).isNotBlank();
    }

    @DisplayName("유효한 토큰인 경우 검증에 통과한다.")
    @Test
    void validateToken() {
        // given

        String username = "yeop";

        LocalDateTime now = LocalDateTime.now();

        // when
        String token = jwtUtil.generateToken(username, now);

        // when // then
        boolean validatedToken = jwtUtil.validateToken(token);

        assertThat(validatedToken).isTrue();
    }

    public String generateToken(String username, LocalDateTime localDateTime){
        return jwtUtil.generateToken(username, localDateTime);
    }

    @DisplayName("토큰의 유효기간이 만료된 경우 사용할 수 없다.")
    @Test
    void afterExpiredToken() {
        // given
        String token = generateToken("yeop", LocalDateTime.now().minusDays(1));

        // when // then
        assertThatThrownBy(() -> jwtUtil.validateToken(token))
                .isInstanceOf(ExpiredJwtException.class);
    }

    @DisplayName("변조된 토큰인 경우 사용할 수 없다.")
    @Test
    void notSignatureToken() {
        // given
        String token = generateToken("yeop", LocalDateTime.now().minusDays(1));

        String finalToken = token.substring(0, token.length() - 1) + "X";

        // when // then
        assertThatThrownBy(() -> jwtUtil.validateToken(finalToken))
                .isInstanceOf(SignatureException.class);
    }

    @DisplayName("token에서 username을 가져올 수 있다.")
    @Test
    void getUsername() {
        // given
        String token = generateToken("yeop", LocalDateTime.now());

        // when
        String username = jwtUtil.getUsername(token);

        // then
        assertThat(username).isEqualTo("yeop");
    }

}