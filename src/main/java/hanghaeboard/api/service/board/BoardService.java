package hanghaeboard.api.service.board;

import hanghaeboard.api.controller.board.request.CreateBoardRequest;
import hanghaeboard.api.controller.board.request.UpdateBoardRequest;
import hanghaeboard.api.exception.exception.AuthorityException;
import hanghaeboard.api.service.board.response.*;
import hanghaeboard.domain.board.Board;
import hanghaeboard.domain.board.BoardRepository;
import hanghaeboard.domain.user.Role;
import hanghaeboard.domain.user.User;
import hanghaeboard.domain.user.UserRepository;
import hanghaeboard.util.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Transactional
    public CreateBoardResponse createBoard(CreateBoardRequest request, String jwtToken) {
        String username = jwtUtil.getUsername(jwtToken);
        User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("회원 정보가 올바르지 않습니다."));

        Board board = request.toEntity(user);
        Board savedBoard = boardRepository.save(board);

        return CreateBoardResponse.from(savedBoard);
    }

    public List<FindBoardWithCommentResponse> findAllBoard(){
        return boardRepository.findAllBoard();
    }

    public FindBoardResponse findBoardById(Long id){

        Board findBoard = findBoardEntityById(id);

        if(findBoard.isDeleted()){
            throw new EntityNotFoundException("삭제된 게시물입니다.");
        }

        return FindBoardResponse.from(findBoard);
    }

    public FindBoardWithCommentResponse findBoardByIdWithComments(Long id){

        FindBoardWithCommentResponse response = boardRepository.findBoardWithComment(id)
                .orElseThrow(() -> new EntityNotFoundException("조회된 게시물이 없습니다."));

        if(response.isDeleted()){
            throw new EntityNotFoundException("삭제된 게시물입니다.");
        }

        return response;
    }

    @Transactional
    public UpdateBoardResponse updateBoard(UpdateBoardRequest request, Long id, String jwtToken) {
        Board findBoard = findBoardEntityById(id);

        validAuthority(findBoard, jwtToken);

        if(findBoard.isDeleted()){
            throw new EntityNotFoundException("삭제된 게시물입니다.");
        }

        findBoard.changeBoard(request.getTitle(), request.getContent());

        return UpdateBoardResponse.from(findBoard);
    }

    private void validAuthority(Board board, String jwtToken){
        String username = jwtUtil.getUsername(jwtToken);
        Role role = jwtUtil.getRole(jwtToken);
        if(Role.ADMIN != role && board.isNotWriter(username)){
            throw new AuthorityException("권한이 없습니다.");
        }
    }

    @Transactional
    public DeleteBoardResponse deleteBoard(Long id, String jwtToken){

        Board findBoard = findBoardEntityById(id);
        validAuthority(findBoard, jwtToken);

        LocalDateTime deletedDatetime = LocalDateTime.now();

        if(!findBoard.isDeleted()){
            findBoard.delete(deletedDatetime);
        }

        return DeleteBoardResponse.from(findBoard);
    }

    private Board findBoardEntityById(Long id) {
        return boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("조회된 게시물이 없습니다."));
    }

    public FindBoardWithCommentResponse findBoardWithComment(Long id){
        return null;
    }

}
