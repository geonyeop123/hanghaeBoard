package hanghaeboard.domain.board;

import hanghaeboard.api.service.board.response.FindBoardResponse;
import hanghaeboard.api.service.board.response.FindBoardWithCommentResponse;
import hanghaeboard.api.service.comment.response.FindCommentResponse;
import hanghaeboard.config.AuditingConfig;
import hanghaeboard.domain.comment.Comment;
import hanghaeboard.domain.comment.CommentRepository;
import hanghaeboard.domain.user.User;
import hanghaeboard.domain.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@SpringBootTest
@ActiveProfiles("test")
@Import(AuditingConfig.class)
@Transactional
class BoardRepositoryTest {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @AfterEach
    void tearDown() {
        commentRepository.deleteAllInBatch();
        boardRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("게시물을 생성할 수 있다.")
    @Test
    void createBoard() {
        // given
        User user = userRepository.save(User.builder().username("yeop").password("12345678").build());
        Board board = makeBoard(user, "hi", "hihihi");

        // when
        boardRepository.save(board);

        // then
        List<Board> all = boardRepository.findAll();
        assertThat(all).hasSize(1);
        assertThat(all)
                .extracting("user.username", "title", "content")
                .containsExactlyInAnyOrder(tuple("yeop","hi", "hihihi"));
    }

    @DisplayName("생성 일자로 내림차순 정렬된 게시물 목록을 조회할 수 있다.")
    @Test
    void findAllBoard() throws Exception {
        // given
        User user = userRepository.save(User.builder().username("yeop").password("12345678").build());
        Board board1 = makeBoard(user, "title1", "content1");
        Thread.sleep(10);
        Board board2 = makeBoard(user, "title2", "content2");
        Thread.sleep(10);
        Board board3 = makeBoard(user, "title3", "content3");

        boardRepository.saveAll(List.of(board1, board2, board3));

        // when
        List<FindBoardResponse> allBoard = boardRepository.findAllBoard();

        // then
        assertThat(allBoard).extracting("writer", "title", "content")
                .containsExactly(
                        tuple("yeop", "title3", "content3")
                        , tuple( "yeop", "title2", "content2")
                        , tuple( "yeop", "title1", "content1")
                );
    }

    @DisplayName("게시물 목록을 조회할 때 삭제되지 않은 게시물만 조회한다.")
    @Test
    void findAllBoard_notDeleted() throws Exception {
        // given
        User user = userRepository.save(User.builder().username("yeop").password("12345678").build());
        Board board1 = makeBoard(user, "title1", "content1");
        Thread.sleep(10);
        Board board2 = makeBoard(user, "title2", "content2");
        Thread.sleep(10);
        Board board3 = makeBoard(user, "title3", "content3");

        LocalDateTime deletedDatetime = LocalDateTime.now();

        board3.delete(deletedDatetime);

        boardRepository.saveAll(List.of(board1, board2, board3));

        // when
        List<FindBoardResponse> allBoard = boardRepository.findAllBoard();

        // then
        assertThat(allBoard).hasSize(2);
        assertThat(allBoard).extracting("writer", "title", "content")
                .containsExactly(
                        tuple( "yeop", "title2", "content2")
                        , tuple( "yeop", "title1", "content1")
                );
    }

    @DisplayName("게시물을 id로 조회할 수 있다.")
    @Test
    void findBoardById() {
        // given
        User user = userRepository.save(User.builder().username("yeop").password("12345678").build());
        Board board = makeBoard(user, "title1", "content1");
        Board save = boardRepository.save(board);
        Long id = save.getId();

        // when
        Board findById = boardRepository.findById(id).orElseThrow();

        // then
        assertThat(findById.getId()).isEqualTo(id);
        assertThat(findById.getTitle()).isEqualTo("title1");
    }

    @DisplayName("게시물을 수정할 수 있다.")
    @Test
    void modifyBoard() {
        // given
        User user = userRepository.save(User.builder().username("yeop").password("12345678").build());
        Board saved = boardRepository.save(makeBoard(user, "title1", "content1"));

        // when
        Board board = boardRepository.findById(saved.getId()).orElseThrow();
        board.changeBoard("changeTitle", "changeContent");

        // then
        Board updatedBoard = boardRepository.findById(board.getId()).orElseThrow();
        assertThat(updatedBoard.getTitle()).isEqualTo("changeTitle");
        assertThat(updatedBoard.getContent()).isEqualTo("changeContent");
    }

    @DisplayName("게시물을 삭제할 수 있다.")
    @Test
    void deleteBoard() {
        // given
        User user = userRepository.save(User.builder().username("yeop").password("12345678").build());
        Board saved = boardRepository.save(makeBoard(user, "title1", "content1"));
        LocalDateTime deletedDatetime = LocalDateTime.of(2025, 2, 24, 14, 40);
        Board board = boardRepository.findById(saved.getId()).orElseThrow();

        // when
        board.delete(deletedDatetime);

        // then
        Board deletedBoard = boardRepository.findById(board.getId()).orElseThrow();
        assertThat(deletedBoard.getDeletedDatetime()).isEqualTo(deletedDatetime);
    }

    private static Board makeBoard(User user, String title, String content) {
        return Board.builder().user(user).title(title).content(content).build();
    }

    @DisplayName("댓글을 포함한 게시물을 조회할 수 있다.")
    @Test
    void findBoardWithComment() {
        // given
        User user = userRepository.save(User.builder().username("yeop").password("12345678").build());
        Board board = boardRepository.save(makeBoard(user, "title1", "content1"));
        commentRepository.saveAll(List.of(makeComment(user, board, "comment1")
                , makeComment(user, board, "comment2")
                , makeComment(user, board, "comment3")));
        Long boardId = board.getId();
        // when
        FindBoardWithCommentResponse boardWithComment = boardRepository.findBoardWithComment(boardId)
                .orElseThrow();
        // then
        assertThat(boardWithComment.getWriter()).isEqualTo("yeop");
        assertThat(boardWithComment.getTitle()).isEqualTo("title1");
        assertThat(boardWithComment.getContent()).isEqualTo("content1");

        List<FindCommentResponse> comments = boardWithComment.getComments();
        assertThat(comments).extracting("writer", "content")
                .containsExactlyInAnyOrder(
                        tuple("yeop", "comment1")
                        , tuple("yeop", "comment2")
                        ,tuple("yeop", "comment3")
                );
    }

    @DisplayName("댓글을 없는 게시물을 조회할 수 있다.")
    @Test
    void findBoardWithCommentIsCommentsEmpty() {
        // given
        User user = userRepository.save(User.builder().username("yeop").password("12345678").build());
        Board board = boardRepository.save(makeBoard(user, "title1", "content1"));
        Long boardId = board.getId();
        // when
        FindBoardWithCommentResponse boardWithComment = boardRepository.findBoardWithComment(boardId)
                .orElseThrow();
        // then
        assertThat(boardWithComment.getWriter()).isEqualTo("yeop");
        assertThat(boardWithComment.getTitle()).isEqualTo("title1");
        assertThat(boardWithComment.getContent()).isEqualTo("content1");
        assertThat(boardWithComment.getComments()).isEmpty();
    }

    private static Comment makeComment(User user, Board board, String content) {
        return Comment.builder().user(user).board(board).content(content).build();
    }

}