package hanghaeboard.api.service.user;

import hanghaeboard.api.controller.user.request.CreateUserRequest;
import hanghaeboard.api.controller.user.request.LoginRequest;
import hanghaeboard.api.exception.exception.InvalidPasswordException;
import hanghaeboard.api.service.user.response.FindUser;
import hanghaeboard.api.service.user.response.LoginResponse;
import hanghaeboard.domain.user.Role;
import hanghaeboard.domain.user.User;
import hanghaeboard.domain.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class UserServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
    }

    @DisplayName("회원가입을 할 수 있다.")
    @Test
    void join() {
        // given
        CreateUserRequest request = CreateUserRequest.builder().username("yeop").password("Pass12!@").build();

        // when
        FindUser savedUser = userService.join(request);

        // then
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("yeop");
        assertThat(savedUser.getRole()).isEqualTo(Role.USER);
    }

    @DisplayName("관리자 용으로 회원가입을 할 수 있다.")
    @Test
    void join_admin() {
        // given
        CreateUserRequest request = CreateUserRequest.builder()
                .username("yeop")
                .password("Pass12!@")
                .role(Role.ADMIN)
                .build();

        // when
        FindUser savedUser = userService.join(request);

        // then
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("yeop");
        assertThat(savedUser.getRole()).isEqualTo(Role.ADMIN);
    }

    @DisplayName("동일한 username을 가진 회원이 있다면 해당 username으로 회원가입을 할 수 없다.")
    @Test
    void join_duplicateUsername() {
        // given
        userRepository.save(User.builder().username("yeop").password("Pass12!@").build());
        CreateUserRequest request = CreateUserRequest.builder().username("yeop").password("12345678").build();

        // when // then
        assertThatThrownBy(() -> userService.join(request)).isInstanceOf(DuplicateKeyException.class)
                                                            .hasMessage("이미 존재하는 ID입니다.");
    }

    @DisplayName("회원의 id로 회원을 조회할 수 있다.")
    @Test
    void findUserById() {
        // given
        User user = User.builder().username("yeop").password("Pass12!@").build();
        User save = userRepository.save(user);
        Long id = save.getId();
        // when
        FindUser findUser = userService.findUserById(id);

        // then
        assertThat(findUser.getUserId()).isEqualTo(id);
        assertThat(findUser.getUsername()).isEqualTo("yeop");
    }

    @DisplayName("id에 해당하는 회원이 없는 경우 조회할 수 없다.")
    @Test
    void findUserByIdWithoutUser() {
        // given
        Long id = 1L;
        // when // then
        assertThatThrownBy(() -> userService.findUserById(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("일치하는 회원이 없습니다.");
    }

    @DisplayName("로그인하여 JWT토큰을 생성할 수 있다.")
    @Test
    void loginGetJwt() {
        // given
        User user = User.builder().username("yeop").password("Pass12!@").build();
        User save = userRepository.save(user);
        Long id = save.getId();

        LoginRequest request = LoginRequest.builder().username("yeop").password("Pass12!@").build();

        // when
        LoginResponse response = userService.login(request);

        // then
        assertThat(response.getJwtToken()).isNotNull();
    }

    @DisplayName("로그인 시 username에 해당하는 user가 없는 경우 로그인을 할 수 없다.")
    @Test
    void login_notFoundUser() {
        // given
        User user = User.builder().username("yeop").password("Pass12!@").build();
        User save = userRepository.save(user);
        Long id = save.getId();

        LoginRequest request = LoginRequest.builder().username("yeop1").password("Pass12!@").build();

        // when // then
        assertThatThrownBy(() -> userService.login(request)).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("일치하는 회원이 없습니다.");
    }

    @DisplayName("로그인 시 비밀번호가 일치하지 않는 경우 로그인을 할 수 없다.")
    @Test
    void login_isNotCorrectPassword() {
        // given
        User user = User.builder().username("yeop").password("Pass12!@").build();
        User save = userRepository.save(user);
        Long id = save.getId();

        LoginRequest request = LoginRequest.builder().username("yeop").password("password").build();

        // when // then
        assertThatThrownBy(() -> userService.login(request)).isInstanceOf(InvalidPasswordException.class)
                .hasMessage("비밀번호가 올바르지 않습니다.");
    }

}