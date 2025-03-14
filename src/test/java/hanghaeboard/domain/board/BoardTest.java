package hanghaeboard.domain.board;

import hanghaeboard.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BoardTest {


    @DisplayName("작성자와 파라미터가 동일한경우 false를 반환한다.")
    @Test
    void isWriter() {
        // given
        User user = User.builder().username("yeop").password("Pass12!@").build();
        Board board = makeBoard(user, "title", "content");
        // when
        boolean isNotWriter = board.isNotWriter("yeop");

        // then
        assertThat(isNotWriter).isFalse();
    }

    @DisplayName("작성자와 파라미터가 다른 경우 true를 반환한다.")
    @Test
    void isNotWriter() {
        // given
        User user = User.builder().username("yeop").password("Pass12!@").build();
        Board board = makeBoard(user, "title", "content");
        // when
        boolean isNotWriter = board.isNotWriter("another");

        // then
        assertThat(isNotWriter).isTrue();
    }

    @DisplayName("게시물의 내용을 변경할 수 있다.")
    @Test
    void changeBoard() {
        // given
        User user = User.builder().username("yeop").password("Pass12!@").build();
        Board board = makeBoard(user, "title", "content");

        // when
        board.changeBoard("changeTitle", "changeContent");

        // then
        assertThat(board.getTitle()).isEqualTo("changeTitle");
        assertThat(board.getContent()).isEqualTo("changeContent");
    }

    Board makeBoard(User user, String title, String content) {
        return Board.builder().user(user).title(title).content(content).build();
    }

}