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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CommentRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private CommentRepository commentRepository;

    @AfterEach
    void tearDown() {
        commentRepository.deleteAll();
        boardRepository.deleteAll();
        userRepository.deleteAll();
    }

    @DisplayName("댓글을 저장할 수 있다.")
    @Test
    void makeComment() {
        // given
        Comment comment = makeComment("comment");
        // when
        commentRepository.save(comment);

        // then
        List<Comment> all = commentRepository.findAll();
        assertThat(all).hasSize(1);
    }

    @DisplayName("댓글을 수정할 수 있다.")
    @Test
    void modifyComment() {
        // given
        Comment comment = makeComment("comment");
        Comment savedComment = commentRepository.save(comment);

        // when
        savedComment.modifyContent("modifyComment");

        // then
        Comment modifyComment = commentRepository.findById(savedComment.getId()).orElseThrow();
        assertThat(modifyComment.getContent()).isEqualTo("modifyComment");
    }

    Comment makeComment(String content){
        User user = userRepository.save(User.builder().username("yeop").password("12345678").build());
        Board board = boardRepository.save(Board.builder().writer("yeop").password("12345678")
                .title("title").content("content").build());

        return Comment.builder().user(user).board(board).content(content).build();
    }

}