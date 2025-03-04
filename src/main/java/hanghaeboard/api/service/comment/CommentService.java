package hanghaeboard.api.service.comment;

import hanghaeboard.api.controller.comment.request.CreateCommentRequest;
import hanghaeboard.api.service.comment.response.CreateCommentResponse;
import hanghaeboard.domain.board.Board;
import hanghaeboard.domain.board.BoardRepository;
import hanghaeboard.domain.comment.Comment;
import hanghaeboard.domain.comment.CommentRepository;
import hanghaeboard.domain.user.User;
import hanghaeboard.domain.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    public CreateCommentResponse createComment(CreateCommentRequest request, Long boardId){
        Board board = boardRepository.findById(boardId)
                .orElseThrow(()-> new EntityNotFoundException("조회된 게시물이 없습니다."));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("일치하는 회원이 없습니다."));

        Comment comment = Comment.builder().user(user).board(board).content(request.getContent()).build();
        Comment savedComment = commentRepository.save(comment);

        return CreateCommentResponse.from(savedComment);
    }
}
