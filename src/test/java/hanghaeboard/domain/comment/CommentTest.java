package hanghaeboard.domain.comment;

import hanghaeboard.domain.board.Board;
import hanghaeboard.domain.board.BoardRepository;
import hanghaeboard.domain.user.User;
import hanghaeboard.domain.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class CommentTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        commentRepository.deleteAll();
        boardRepository.deleteAll();
        userRepository.deleteAll();
    }

    @DisplayName("댓글을 저장할 수 있다.")
    @Test
    void saveComment() {
        // given
        User user = userRepository.save(User.builder().username("yeop").password("12345678").build());
        Board board = boardRepository.save(Board.builder().writer("yeop").password("12345678").title("title").content("content").build());
        // when
        Comment comment = Comment.builder().board(board).user(user).build();
        commentRepository.save(comment);
        // then
        List<Comment> all = commentRepository.findAll();
        assertThat(all).hasSize(1);

    }

}