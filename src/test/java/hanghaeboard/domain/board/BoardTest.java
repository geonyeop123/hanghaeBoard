package hanghaeboard.domain.board;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BoardTest {


    @DisplayName("념겨받은 비밀번호와 현재 비밀번호가 일치하지 않는 경우 true를 반환한다.")
    @Test
    void isNotCorrectPassword() {
        // given
        Board board = makeBoard("yeop", "password", "title", "content");


        // when // then
        boolean isCorrectPassword = board.isNotCorrectPassword("1234");

        // then
        assertThat(isCorrectPassword).isTrue();
    }

    @DisplayName("넘겨받은 비밀번호와 현재 비밀번호가 일치하는 경우 false를 반환한다.")
    @Test
    void isCorrectPassword() {
        // given
        Board board = makeBoard("yeop", "password", "title", "content");

        // when // then
        boolean isCorrectPassword = board.isNotCorrectPassword("password");

        // then
        assertThat(isCorrectPassword).isFalse();
    }

    @DisplayName("게시물의 내용을 변경할 수 있다.")
    @Test
    void changeBoard() {
        // given
        Board board = makeBoard("yeop", "password", "title", "content");

        // when
        board.changeBoard("yeop1", "changeTitle", "changeContent");

        // then
        assertThat(board.getWriter()).isEqualTo("yeop1");
        assertThat(board.getTitle()).isEqualTo("changeTitle");
        assertThat(board.getContent()).isEqualTo("changeContent");
    }

    Board makeBoard(String writer, String password, String title, String content) {
        return Board.builder().writer(writer).password(password).title(title).content(content).build();
    }

}