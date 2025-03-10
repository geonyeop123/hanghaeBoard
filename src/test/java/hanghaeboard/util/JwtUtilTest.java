package hanghaeboard.util;

import hanghaeboard.domain.user.User;
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
        User user = User.builder().username("yeop")
                .password("12345678")
                .build();

        LocalDateTime now = LocalDateTime.now();

        // when
        String token = jwtUtil.generateToken(user, now);

        // then
        assertThat(token).isNotBlank();
    }

    @DisplayName("유효한 토큰인 경우 검증에 통과한다.")
    @Test
    void validateToken() {
        // given

        User user = User.builder().username("yeop")
                .password("12345678")
                .build();

        LocalDateTime now = LocalDateTime.now();

        // when
        String token = jwtUtil.generateToken(user, now);

        // when // then
        boolean validatedToken = jwtUtil.validateToken(token);

        assertThat(validatedToken).isTrue();
    }

    public String generateToken(User user, LocalDateTime localDateTime){
        return jwtUtil.generateToken(user, localDateTime);
    }

    @DisplayName("토큰의 유효기간이 만료된 경우 사용할 수 없다.")
    @Test
    void afterExpiredToken() {
        User user = User.builder().username("yeop")
                .password("12345678")
                .build();

        LocalDateTime dateTime = LocalDateTime.now().minusDays(1);

        // given
        String token = generateToken(user, dateTime);

        // when // then
        assertThatThrownBy(() -> jwtUtil.validateToken(token))
                .isInstanceOf(ExpiredJwtException.class);
    }

    @DisplayName("변조된 토큰인 경우 사용할 수 없다.")
    @Test
    void notSignatureToken() {
        // given
        User user = User.builder().username("yeop")
                .password("12345678")
                .build();

        String token = generateToken(user, LocalDateTime.now());

        String finalToken = token.substring(0, token.length() - 1) + "X";

        // when // then
        assertThatThrownBy(() -> jwtUtil.validateToken(finalToken))
                .isInstanceOf(SignatureException.class);
    }

    @DisplayName("token에서 username을 가져올 수 있다.")
    @Test
    void getUsername() {
        // given
        User user = User.builder().username("yeop")
                .password("12345678")
                .build();

        String token = generateToken(user, LocalDateTime.now());

        // when
        String username = jwtUtil.getUsername(token);

        // then
        assertThat(username).isEqualTo("yeop");
    }

}