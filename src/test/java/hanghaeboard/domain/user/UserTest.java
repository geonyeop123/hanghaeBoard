package hanghaeboard.domain.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class UserTest {

    @DisplayName("회원을 생성할 수 있다.")
    @Test
    void createUser() {
        // given // when
        User user = makeUser("yeop", "password");

        // then
        assertThat(user).isNotNull();
    }

    @DisplayName("회원을 생성할 때 아이디는 4글자 이상이어야 한다.")
    @Test
    void createUser_usernameMinLengthValidation() {
        // given // when // then
        assertThatThrownBy(() -> makeUser("yeo", "password")).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("아이디는 4글자 이상, 10글자 이하여야 합니다.");
    }

    @DisplayName("회원을 생성할 때 아이디는 10글자 이하여야 한다.")
    @Test
    void createUser_usernameMaxLengthValidation() {
        // given // when // then
        assertThatThrownBy(() -> makeUser("yeopyeopyep", "password")).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("아이디는 4글자 이상, 10글자 이하여야 합니다.");
    }

    @DisplayName("회원을 생성할 때 아이디는 소문자 영문과 숫자로만 이루어져야 한다.")
    @Test
    void createUser_usernameRegexp() {
        // given // when // then
        assertThatThrownBy(() -> makeUser("YEOP", "password")).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("아이디는 소문자 영문과 숫자로만 이루어져야 합니다.");
    }

    @DisplayName("회원을 생성할 때 비밀번호는 8글자 이상이어야 한다.")
    @Test
    void createUser_passwordMinLengthValidation() {
        // given // when // then
        assertThatThrownBy(() -> makeUser("yeop", "passwor")).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("비밀번호는 8글자 이상, 15글자 이하여야 합니다.");
    }

    @DisplayName("회원을 생성할 때 비밀번호는 15글자 이하여야 한다.")
    @Test
    void createUser_passwordMaxLengthValidation() {
        // given // when // then
        assertThatThrownBy(() -> makeUser("yeop", "passwordpassword")).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("비밀번호는 8글자 이상, 15글자 이하여야 합니다.");
    }

    @DisplayName("회원을 생성할 때 비밀번호는 소문자 영문과 대문자, 숫자로만 이루어져야 한다.")
    @Test
    void createUser_passwordRegexp() {
        // given // when // then
        assertThatThrownBy(() -> makeUser("yeop", "pass!!@#$")).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("비밀번호는 소문자 혹은 대문자 영문과 숫자로만 이루어져야 합니다.");
    }

    private static User makeUser(String username, String password) {
        return User.builder()
                .username(username)
                .password(password).build();
    }

}