package hanghaeboard.api.service.board;

import hanghaeboard.api.controller.board.request.CreateBoardRequest;
import hanghaeboard.api.controller.board.request.DeleteBoardRequest;
import hanghaeboard.api.controller.board.request.UpdateBoardRequest;
import hanghaeboard.api.exception.exception.InvalidPasswordException;
import hanghaeboard.api.service.board.response.CreateBoardResponse;
import hanghaeboard.api.service.board.response.DeleteBoardResponse;
import hanghaeboard.api.service.board.response.FindBoardResponse;
import hanghaeboard.api.service.board.response.UpdateBoardResponse;
import hanghaeboard.domain.board.Board;
import hanghaeboard.domain.board.BoardRepository;
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

    @Transactional
    public CreateBoardResponse createBoard(CreateBoardRequest request) {

        Board board = request.toEntity();
        Board savedBoard = boardRepository.save(board);

        return CreateBoardResponse.from(savedBoard);
    }

    public List<FindBoardResponse> findAllBoard(){
        return boardRepository.findAllBoard();
    }

    public FindBoardResponse findBoardById(Long id){

        Board findBoard = boardRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("조회된 게시물이 없습니다."));

        if(findBoard.isDeleted()){
            throw new EntityNotFoundException("삭제된 게시물입니다.");
        }

        return FindBoardResponse.from(findBoard);
    }

    @Transactional
    public UpdateBoardResponse updateBoard(Long id, UpdateBoardRequest request) {
        Board findBoard = findBoard(id, request.getPassword());

        if(findBoard.isDeleted()){
            throw new EntityNotFoundException("삭제된 게시물입니다.");
        }

        findBoard.changeBoard(request.getWriter(), request.getTitle(), request.getContent());

        return UpdateBoardResponse.from(findBoard);
    }

    private Board findBoard(Long id, String request) {
        Board findBoard = boardRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("조회된 게시물이 없습니다."));

        if (!findBoard.isNotCorrectPassword(request)) {
            throw new InvalidPasswordException("비밀번호가 올바르지 않습니다.");
        }
        return findBoard;
    }

    @Transactional
    public DeleteBoardResponse deleteBoard(Long id, DeleteBoardRequest request){
        Board savedBoard = findBoard(id, request.getPassword());

        LocalDateTime deletedDatetime = LocalDateTime.now();

        if(!savedBoard.isDeleted()){
            savedBoard.delete(deletedDatetime);
        }

        return DeleteBoardResponse.from(savedBoard);
    }

}
