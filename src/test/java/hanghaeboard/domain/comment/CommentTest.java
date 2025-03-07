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
        Comment comment = makeComment("content");

        // when
        comment.modifyContent("modifiedContent");

        // then
        assertThat(comment.getContent()).isEqualTo("modifiedContent");
    }

    @DisplayName("작성자와 넘겨받은 유저와 동일하면 False를 반환한다.")
    @Test
    void isWriteUser() {
        // given
        Comment comment = makeComment("yeop", "content");

        // when
        boolean isNotWriteUser = comment.isNotWriteUser("yeop");

        // then
        assertThat(isNotWriteUser).isFalse();
    }

    @DisplayName("작성자와 넘겨받은 유저와 다르면 true를 반환한다.")
    @Test
    void isNotWriteUser() {
        // given
        Comment comment = makeComment("yeop", "content");

        // when
        boolean isNotWriteUser = comment.isNotWriteUser("another");

        // then
        assertThat(isNotWriteUser).isTrue();
    }

    @DisplayName("댓글을 삭제할 수 있다.")
    @Test
    void test() {
        // given
        Comment comment = makeComment("content");

        // when
        comment.delete(LocalDateTime.now());

        // then
        assertThat(comment.isDeleted()).isTrue();
    }

    Comment makeComment(String writer, String content){
        User user = User.builder().id(1L).username(writer).password("12345678").build();
        Board board = Board.builder().id(1L).writer(writer).password("12345678")
                .title("title").content("content").build();

        return Comment.builder().user(user).board(board).content(content).build();
    }

    Comment makeComment(String content){
        return makeComment("yeop", content);
    }

}