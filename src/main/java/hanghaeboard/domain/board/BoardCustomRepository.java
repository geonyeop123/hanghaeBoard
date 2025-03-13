package hanghaeboard.domain.board;

import hanghaeboard.api.service.board.response.FindBoardResponse;
import hanghaeboard.api.service.board.response.FindBoardWithCommentResponse;

import java.util.List;
import java.util.Optional;

public interface BoardCustomRepository {
    List<FindBoardResponse> findAllBoard();
    Optional<FindBoardWithCommentResponse> findBoardWithComment(Long boardId);
}
