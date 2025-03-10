package hanghaeboard.api.service.comment;

import hanghaeboard.api.controller.comment.request.CreateCommentRequest;
import hanghaeboard.api.controller.comment.request.UpdateCommentRequest;
import hanghaeboard.api.service.comment.response.CreateCommentResponse;
import hanghaeboard.api.service.comment.response.DeleteCommentResponse;
import hanghaeboard.api.service.comment.response.UpdateCommentResponse;
import hanghaeboard.domain.board.Board;
import hanghaeboard.domain.board.BoardRepository;
import hanghaeboard.domain.comment.Comment;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
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
        String jwtToken = jwtUtil.generateToken(user, LocalDateTime.now());
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
        User user = User.builder().id(1L).username("yeop123").password("12345678").build();
        String jwtToken = jwtUtil.generateToken(user, LocalDateTime.now());
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
        String jwtToken = jwtUtil.generateToken(user, LocalDateTime.now());

        CreateCommentRequest createCommentRequest = CreateCommentRequest.builder()
                .content("comment").build();

        // when // then
        assertThatThrownBy(() -> commentService.createComment(createCommentRequest, jwtToken, 1L))
                .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("조회된 게시물이 없습니다.");
    }

    @DisplayName("댓글을 수정할 수 있다.")
    @Test
    void modifyComment() {
        // given
        User user = userRepository.save(User.builder().username("yeop").password("12345678").build());
        String jwtToken = jwtUtil.generateToken(user, LocalDateTime.now());
        Board board = boardRepository.save(Board.builder().writer("yeop").password("12345678")
                .title("title").content("content").build());
        Comment savedComment = makeComment(user, board, "comment");

        String modifyComment = "modifyComment";
        UpdateCommentRequest request = UpdateCommentRequest.builder()
                .content(modifyComment).build();

        // when
        UpdateCommentResponse updateCommentResponse = commentService.updateComment(request, jwtToken, savedComment.getId());

        // then
        assertThat(updateCommentResponse.getContent()).isEqualTo(modifyComment);
    }

    @DisplayName("작성자가 아닌 경우 댓글을 수정할 수 없다.")
    @Test
    void modifyComment_notWriter() {
        // given
        User user = userRepository.save(User.builder().username("yeop").password("12345678").build());
        Board board = boardRepository.save(Board.builder().writer("yeop").password("12345678")
                .title("title").content("content").build());
        Comment savedComment = makeComment(user, board, "comment");

        User anotherUser = User.builder().id(1L).username("another").password("12345678").build();
        String jwtToken = jwtUtil.generateToken(anotherUser, LocalDateTime.now());

        String modifyComment = "modifyComment";
        UpdateCommentRequest request = UpdateCommentRequest.builder()
                .content(modifyComment).build();

        // when // then
        assertThatThrownBy(() -> commentService.updateComment(request, jwtToken, savedComment.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("댓글의 작성자만 수정할 수 있습니다.");
    }

    @DisplayName("게시물이 삭제된 경우 댓글을 수정할 수 없다.")
    @Test
    void modifyComment_deletedBoard() {
        // given
        User user = userRepository.save(User.builder().username("yeop").password("12345678").build());
        String jwtToken = jwtUtil.generateToken(user, LocalDateTime.now());
        Board board = boardRepository.save(Board.builder().writer("yeop").password("12345678")
                .title("title").content("content").build());

        board.delete(LocalDateTime.now());

        Comment savedComment = makeComment(user, board, "comment");

        String modifyComment = "modifyComment";
        UpdateCommentRequest request = UpdateCommentRequest.builder()
                .content(modifyComment).build();

        // when // then
        assertThatThrownBy(() -> commentService.updateComment(request, jwtToken, savedComment.getId()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("삭제된 게시물입니다.");
    }

    Comment makeComment(User user, Board board, String content){
        return commentRepository.save(Comment.builder().user(user).board(board).content("comment").build());
    }

    @DisplayName("댓글을 삭제할 수 있다.")
    @Test
    void deleteComment() {
        // given
        User user = userRepository.save(User.builder().username("yeop").password("12345678").build());
        String jwtToken = jwtUtil.generateToken(user, LocalDateTime.now());
        Board board = boardRepository.save(Board.builder().writer("yeop").password("12345678")
                .title("title").content("content").build());
        Comment savedComment = makeComment(user, board, "comment");

        // when
        DeleteCommentResponse deleteCommentResponse = commentService.deleteComment(jwtToken, savedComment.getId());

        // then
        assertThat(deleteCommentResponse.getDeletedDatetime()).isNotNull();
    }

    @DisplayName("작성자가 아닌 경우 댓글을 삭제할 수 없다.")
    @Test
    void deleteComment_notWriteUser() {
        // given
        User user = userRepository.save(User.builder().username("yeop").password("12345678").build());
        Board board = boardRepository.save(Board.builder().writer("yeop").password("12345678")
                .title("title").content("content").build());
        Comment savedComment = makeComment(user, board, "comment");

        User anotherUser = User.builder().username("another").password("12345678").build();
        String jwtToken = jwtUtil.generateToken(anotherUser, LocalDateTime.now());

        // when //then
        assertThatThrownBy(() -> commentService.deleteComment(jwtToken, savedComment.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("댓글의 작성자만 삭제할 수 있습니다.");
    }

}