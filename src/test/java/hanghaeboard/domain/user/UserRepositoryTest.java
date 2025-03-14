package hanghaeboard.domain.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @DisplayName("회원을 저장할 수 있다.")
    @Test
    void saveUser() {
        // given
        User user = User.builder().username("yeop").password("Pass12!@").build();

        // when
        userRepository.save(user);

        // then
        List<User> all = userRepository.findAll();

        assertThat(all).hasSize(1);
        User findUser = all.get(0);
        assertThat(findUser.getId()).isEqualTo(1L);
        assertThat(findUser.getUsername()).isEqualTo("yeop");
        assertThat(findUser.getPassword()).isEqualTo("Pass12!@");
    }

    @DisplayName("username으로 회원을 조회할 수 있다.")
    @Test
    void findByUsername() {
        // given
        User user = User.builder().username("yeop").password("Pass12!@").build();
        userRepository.save(user);

        // when
        Optional<User> findUser = userRepository.findByUsername("yeop");

        // then
        assertThat(findUser.isPresent()).isTrue();
        assertThat(findUser.get().getUsername()).isEqualTo("yeop");
    }
}