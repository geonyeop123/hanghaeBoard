package hanghaeboard.api.service.comment;

import hanghaeboard.api.controller.comment.request.CreateCommentRequest;
import hanghaeboard.api.service.comment.response.CreateCommentResponse;
import hanghaeboard.domain.board.Board;
import hanghaeboard.domain.board.BoardRepository;
import hanghaeboard.domain.comment.CommentRepository;
import hanghaeboard.domain.user.User;
import hanghaeboard.domain.user.UserRepository;
import hanghaeboard.util.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
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
class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private JwtUtil jwtUtil;

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


    @DisplayName("댓글을 생성할 수 있다.")
    @Test
    void createComment() {
        // given

        User user = userRepository.save(User.builder().username("yeop").password("12345678").build());
        String jwtToken = jwtUtil.generateToken("yeop", LocalDateTime.now());
        Board board = boardRepository.save(Board.builder().writer("yeop").password("12345678")
                .title("title").content("content").build());

        CreateCommentRequest createCommentRequest = CreateCommentRequest.builder()
                .content("comment").build();

        // when
        CreateCommentResponse response = commentService.createComment(createCommentRequest, jwtToken, board.getId());

        // then
        assertThat(response).isNotNull();
        assertThat(response.getBoard().getId()).isEqualTo(board.getId());
        assertThat(response.getContent()).isEqualTo("comment");

    }

    @DisplayName("댓글을 생성할 때 댓글 작성 유저를 찾을 수 없는 경우 댓글을 생성할 수 없다.")
    @Test
    void createComment_notFoundUser() {
        // given
        String jwtToken = jwtUtil.generateToken("yeop123", LocalDateTime.now());
        Board board = boardRepository.save(Board.builder().writer("yeop").password("12345678")
                .title("title").content("content").build());

        CreateCommentRequest createCommentRequest = CreateCommentRequest.builder()
                .content("comment").build();

        // when // then
        assertThatThrownBy(() -> commentService.createComment(createCommentRequest, jwtToken, board.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("일치하는 회원이 없습니다.");
    }

    @DisplayName("댓글을 생성할 때 게시물을 찾을 수 없는 경우 댓글을 생성할 수 없다.")
    @Test
    void createComment_notFoundBoard() {
        // given
        User user = userRepository.save(User.builder().username("yeop").password("12345678").build());
        String jwtToken = jwtUtil.generateToken("yeop123", LocalDateTime.now());

        CreateCommentRequest createCommentRequest = CreateCommentRequest.builder()
                .content("comment").build();

        // when // then
        assertThatThrownBy(() -> commentService.createComment(createCommentRequest, jwtToken, 1L))
                .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("조회된 게시물이 없습니다.");
    }

}