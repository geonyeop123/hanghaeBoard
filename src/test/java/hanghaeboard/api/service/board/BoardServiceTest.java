package hanghaeboard.api.service.board;

import hanghaeboard.api.controller.board.request.CreateBoardRequest;
import hanghaeboard.api.controller.board.request.UpdateBoardRequest;
import hanghaeboard.api.exception.exception.AuthorityException;
import hanghaeboard.api.service.board.response.DeleteBoardResponse;
import hanghaeboard.api.service.board.response.FindBoardResponse;
import hanghaeboard.api.service.board.response.FindBoardWithCommentResponse;
import hanghaeboard.api.service.board.response.UpdateBoardResponse;
import hanghaeboard.api.service.comment.response.FindCommentResponse;
import hanghaeboard.domain.board.Board;
import hanghaeboard.domain.board.BoardRepository;
import hanghaeboard.domain.comment.Comment;
import hanghaeboard.domain.comment.CommentRepository;
import hanghaeboard.domain.user.User;
import hanghaeboard.domain.user.UserRepository;
import hanghaeboard.util.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BoardServiceTest {

    @Autowired
    private BoardService boardService;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private JwtUtil jwtUtil;

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
        User user = userRepository.save(User.builder().username("yeop").password("Pass12!@").build());
        CreateBoardRequest request = CreateBoardRequest.builder()
                .title("title")
                .content("content")
                .build();
        String token = jwtUtil.generateToken(user, LocalDateTime.now());
        // when
        boardService.createBoard(request, token);

        // then
        List<Board> all = boardRepository.findAll();
        assertThat(all).hasSize(1);
        assertThat(all).extracting("user.username", "title", "content")
                .containsExactlyInAnyOrder(tuple("yeop", "title", "content"));

    }

    @DisplayName("전체 게시물을 생성일자 내림차순으로 조회할 수 있다.")
    @Test
    void findAllBoard() throws Exception{
        // given
        User user = userRepository.save(User.builder().username("yeop").password("Pass12!@").build());
        boardRepository.save(makeBoard(user, "title1", "content1"));
        Thread.sleep(10);
        boardRepository.save(makeBoard(user, "title2", "content2"));
        Thread.sleep(10);
        boardRepository.save(makeBoard(user, "title3", "content3"));
        Thread.sleep(10);

        // when
        List<FindBoardWithCommentResponse> allBoard = boardService.findAllBoard();

        // then
        assertThat(allBoard).extracting("writer", "title", "content")
                .containsExactly(
                        tuple("yeop", "title3", "content3")
                        , tuple("yeop", "title2", "content2")
                        , tuple("yeop", "title1", "content1"));
    }

    @DisplayName("전체 게시물을 조회할때 게시물이 없는 경우 빈 리스트가 조회된다.")
    @Test
    void findAllBoardByEmpty() {

        // given // when
        List<FindBoardWithCommentResponse> allBoard = boardService.findAllBoard();

        // then
        assertThat(allBoard).isEmpty();
    }

    @DisplayName("전체 게시물을 조회할 때 댓글 목록도 함께 조회된다.")
    @Test
    void findAllBoardWithComments() {
        // given
        User user = userRepository.save(User.builder().username("yeop").password("Pass12!@").build());
        Board board = boardRepository.save(makeBoard(user, "title1", "content1"));
        commentRepository.saveAll(List.of(
                makeComment(user, board, "comment1")
                , makeComment(user, board, "comment2")));
        // when
        List<FindBoardWithCommentResponse> allBoard = boardService.findAllBoard();
        // then
        assertThat(allBoard).hasSize(1);
        List<FindCommentResponse> comments = allBoard.get(0).getComments();
        assertThat(comments).hasSize(2);
        assertThat(comments).extracting("writer", "content")
                .containsExactlyInAnyOrder(tuple("yeop", "comment1"), tuple("yeop", "comment2"));
    }

    @DisplayName("id로 게시물을 조회할 수 있다.")
    @Test
    void findBoardById() {
        // given
        User user = userRepository.save(User.builder().username("yeop").password("Pass12!@").build());
        Board saved = boardRepository.save(makeBoard(user, "title2", "content2"));
        Long id = saved.getId();
        // when
        FindBoardResponse boardById = boardService.findBoardById(id);

        // then
        assertThat(boardById).isNotNull();
        assertThat(boardById.getWriter()).isEqualTo("yeop");
        assertThat(boardById.getTitle()).isEqualTo("title2");
        assertThat(boardById.getContent()).isEqualTo("content2");
    }

    @DisplayName("id로 조회할 수 있는 게시물이 없는 경우 조회되지 않는다.")
    @Test
    void findBoardById_notFound() {
        // given
        Long id = 1L;

        // when // then
        assertThatThrownBy(() -> boardService.findBoardById(id)).isInstanceOf(EntityNotFoundException.class).hasMessage("조회된 게시물이 없습니다.");
    }

    private static Board makeBoard(User user, String title, String content) {
        return Board.builder().title(title).content(content).user(user).build();
    }

    @DisplayName("id로 게시물을 조회할 때 삭제된 게시물인 경우 조회할 수 없다.")
    @Test
    void findBoardById_deletedBoard() {
        // given
        User user = userRepository.save(User.builder().username("yeop").password("Pass12!@").build());
        Board board = makeBoard(user, "title2", "content2");
        LocalDateTime now = LocalDateTime.now();
        board.delete(now);
        Board saved = boardRepository.save(board);
        Long id = saved.getId();
        // when // then
        assertThatThrownBy(() -> boardService.findBoardById(id)).isInstanceOf(EntityNotFoundException.class).hasMessage("삭제된 게시물입니다.");

    }

    @DisplayName("게시물을 수정할 수 있다.")
    @Test
    void modifyBoard() {
        // given
        User user = userRepository.save(User.builder().username("yeop").password("Pass12!@").build());
        String jwtToken = jwtUtil.generateToken(user, LocalDateTime.now());

        Board saved = boardRepository.save(makeBoard(user, "title", "content"));
        Long id = saved.getId();

        UpdateBoardRequest request = UpdateBoardRequest.builder()
                .title("changeTitle")
                .content("changeContent")
                .build();

        // when
        UpdateBoardResponse updateBoardResponse = boardService.updateBoard(request, id, jwtToken);

        // then
        assertThat(updateBoardResponse).isNotNull();
        assertThat(updateBoardResponse.getWriter()).isEqualTo("yeop");
        assertThat(updateBoardResponse.getTitle()).isEqualTo("changeTitle");
        assertThat(updateBoardResponse.getContent()).isEqualTo("changeContent");
    }

    @DisplayName("게시물을 수정할 때 해당 게시물이 없는 경우 수정할 수 없다.")
    @Test
    void modityBoard_notFoundBoard() {
        // given
        User user = userRepository.save(User.builder().username("yeop").password("Pass12!@").build());
        String jwtToken = jwtUtil.generateToken(user, LocalDateTime.now());
        UpdateBoardRequest request = UpdateBoardRequest.builder()
                .title("changeTitle")
                .content("changeContent")
                .build();


        // when // then
        assertThatThrownBy(() -> boardService.updateBoard(request, 1L, jwtToken))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("조회된 게시물이 없습니다.");
    }

    @DisplayName("게시물을 수정할 때 작성자가 아닌 경우 수정할 수 없다.")
    @Test
    void modifyBoard_isNotCorrectPassword() {
        // given
        User user = userRepository.save(User.builder().username("yeop").password("Pass12!@").build());
        Board saved = boardRepository.save(makeBoard(user, "title", "content"));
        Long id = saved.getId();
        UpdateBoardRequest request = UpdateBoardRequest.builder()
                .title("changeTitle")
                .content("changeContent")
                .build();

        User anotherUser = User.builder().username("another").password("Pass12!@").build();
        String jwtToken = jwtUtil.generateToken(anotherUser, LocalDateTime.now());

        // when // then
        assertThatThrownBy(() -> boardService.updateBoard(request, id, jwtToken))
                .isInstanceOf(AuthorityException.class)
                .hasMessage("권한이 없습니다.");
    }

    @DisplayName("게시물을 수정할 때 삭제된 게시물은 수정할 수 없다.")
    @Test
    void modifyBoard_isDeletedBoard() {
        // given
        User user = userRepository.save(User.builder().username("yeop").password("Pass12!@").build());
        String jwtToken = jwtUtil.generateToken(user, LocalDateTime.now());
        Board board = makeBoard(user, "title", "content");
        LocalDateTime deletedDatetime = LocalDateTime.now();
        board.delete(deletedDatetime);

        Board saved = boardRepository.save(board);

        Long id = saved.getId();
        UpdateBoardRequest request = UpdateBoardRequest.builder()
                .title("changeTitle")
                .content("changeContent")
                .build();

        // when // then
        assertThatThrownBy(() -> boardService.updateBoard(request, id, jwtToken))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("삭제된 게시물입니다.");

    }

    @DisplayName("게시물을 삭제할 수 있다.")
    @Test
    void deleteBoard() {
        // given
        User user = userRepository.save(User.builder().username("yeop").password("Pass12!@").build());
        String jwtToken = jwtUtil.generateToken(user, LocalDateTime.now());
        Board saved = boardRepository.save(makeBoard(user, "title", "content"));
        Long id = saved.getId();

        // when
        DeleteBoardResponse deleteBoardResponse = boardService.deleteBoard(id, jwtToken);

        // then
        assertThat(deleteBoardResponse).isNotNull();
    }

    @DisplayName("이미 삭제된 게시물인 경우에는 삭제하지 않고, 이전 삭제 시간을 반환한다.")
    @Test
    void deleteBoard_already() throws Exception{
        // given
        User user = userRepository.save(User.builder().username("yeop").password("Pass12!@").build());
        String jwtToken = jwtUtil.generateToken(user, LocalDateTime.now());
        Board board = makeBoard(user, "title", "content");
        LocalDateTime deletedDatetime = LocalDateTime.of(2025, 3, 10, 19, 40);
        board.delete(deletedDatetime);
        Board saved = boardRepository.save(board);
        Long id = saved.getId();

        Thread.sleep(10);
        // when
        DeleteBoardResponse alreadyDeleteResponse = boardService.deleteBoard(id, jwtToken);

        // then
        assertThat(deletedDatetime).isEqualTo(alreadyDeleteResponse.getDeletedDatetime());
    }

    @DisplayName("게시물을 삭제할 때 해당 게시물이 없는 경우 삭제할 수 없다.")
    @Test
    void deleteBoard_notFoundBoard() {
        // given
        User user = userRepository.save(User.builder().username("yeop").password("Pass12!@").build());
        String jwtToken = jwtUtil.generateToken(user, LocalDateTime.now());

        // when // then
        assertThatThrownBy(() -> boardService.deleteBoard(1L, jwtToken))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("조회된 게시물이 없습니다.");
    }

    @DisplayName("게시물을 삭제할 때 작성자가 아닌 경우 삭제할 수 없다.")
    @Test
    void deleteBoard_isNotWriter() {
        // given
        User user = userRepository.save(User.builder().username("yeop").password("Pass12!@").build());
        Board saved = boardRepository.save(makeBoard(user, "title", "content"));
        Long id = saved.getId();

        User anotherUser = User.builder().username("another").password("Pass12!@").build();
        String jwtToken = jwtUtil.generateToken(anotherUser, LocalDateTime.now());

        // when // then
        assertThatThrownBy(() -> boardService.deleteBoard(id, jwtToken))
                .isInstanceOf(AuthorityException.class)
                .hasMessage("권한이 없습니다.");
    }

    @DisplayName("댓글이 포함된 게시물을 조회할 수 있다.")
    @Test
    void findBoardByIdWithComments() {
        // given
        User user = userRepository.save(User.builder().username("yeop").password("Pass12!@").build());
        Board board = boardRepository.save(makeBoard(user, "title", "content"));
        Long id = board.getId();
        commentRepository.saveAll(List.of(makeComment(user, board, "comment1")
                , makeComment(user, board, "comment2")
                , makeComment(user, board, "comment3")));

        // when
        FindBoardWithCommentResponse response = boardService.findBoardByIdWithComments(id);

        // then
        assertThat(response.getWriter()).isEqualTo("yeop");
        assertThat(response.getTitle()).isEqualTo("title");
        assertThat(response.getContent()).isEqualTo("content");

        List<FindCommentResponse> comments = response.getComments();
        assertThat(comments).hasSize(3);
        assertThat(comments).extracting("writer", "content")
                .containsExactlyInAnyOrder(
                        tuple("yeop", "comment1")
                        , tuple("yeop", "comment2")
                        ,tuple("yeop", "comment3")
                );
    }

    @DisplayName("게시물이 없는 경우 조회할 수 없다.")
    @Test
    void findBoardByIdWithComments_notFound() {
        // given // when // then
        assertThatThrownBy(() -> boardService.findBoardByIdWithComments(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("조회된 게시물이 없습니다.");
    }

    @DisplayName("댓글이 포함된 게시물이 삭제된 경우 조회할 수 없다.")
    @Test
    void findBoardByIdWithComments_deleted() {
        // given
        User user = userRepository.save(User.builder().username("yeop").password("Pass12!@").build());
        Board board = makeBoard(user, "title", "content");

        board.delete(LocalDateTime.now());

        Board saved = boardRepository.save(board);
        Long id = saved.getId();

        // when // then
        assertThatThrownBy(() -> boardService.findBoardByIdWithComments(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("삭제된 게시물입니다.");
    }

    private static Comment makeComment(User user, Board board, String content) {
        return Comment.builder().user(user).board(board).content(content).build();
    }
}