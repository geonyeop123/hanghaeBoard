package hanghaeboard.domain.comment;

import hanghaeboard.domain.board.Board;
import hanghaeboard.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CommentTest {

    @DisplayName("내용을 수정할 수 있다.")
    @Test
    void modifyContent() {
        // given
        User user = User.builder().id(1L).username("yeop").password("Pass12!@").build();
        Comment comment = makeComment(user, "content");

        // when
        comment.modifyContent("modifiedContent");

        // then
        assertThat(comment.getContent()).isEqualTo("modifiedContent");
    }

    @DisplayName("작성자와 넘겨받은 유저와 동일하면 False를 반환한다.")
    @Test
    void isWriteUser() {
        // given
        User user = User.builder().id(1L).username("yeop").password("Pass12!@").build();
        Comment comment = makeComment(user, "content");

        // when
        boolean isNotWriteUser = comment.isNotWriteUser("yeop");

        // then
        assertThat(isNotWriteUser).isFalse();
    }

    @DisplayName("작성자와 넘겨받은 유저와 다르면 true를 반환한다.")
    @Test
    void isNotWriteUser() {
        // given
        User user = User.builder().id(1L).username("yeop").password("Pass12!@").build();
        Comment comment = makeComment(user, "content");

        // when
        boolean isNotWriteUser = comment.isNotWriteUser("another");

        // then
        assertThat(isNotWriteUser).isTrue();
    }

    @DisplayName("댓글을 삭제할 수 있다.")
    @Test
    void test() {
        // given
        User user = User.builder().id(1L).username("yeop").password("Pass12!@").build();
        Comment comment = makeComment(user, "content");

        // when
        comment.delete(LocalDateTime.now());

        // then
        assertThat(comment.isDeleted()).isTrue();
    }

    Comment makeComment(User user, String content){
        Board board = Board.builder().id(1L).user(user)
                .title("title").content("content").build();

        return Comment.builder().user(user).board(board).content(content).build();
    }

}