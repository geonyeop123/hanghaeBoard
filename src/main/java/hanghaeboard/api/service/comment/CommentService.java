package hanghaeboard.api.service.comment;

import hanghaeboard.api.controller.comment.request.CreateCommentRequest;
import hanghaeboard.api.controller.comment.request.UpdateCommentRequest;
import hanghaeboard.api.exception.exception.AuthorityException;
import hanghaeboard.api.service.comment.response.CreateCommentResponse;
import hanghaeboard.api.service.comment.response.DeleteCommentResponse;
import hanghaeboard.api.service.comment.response.UpdateCommentResponse;
import hanghaeboard.domain.board.Board;
import hanghaeboard.domain.board.BoardRepository;
import hanghaeboard.domain.comment.Comment;
import hanghaeboard.domain.comment.CommentRepository;
import hanghaeboard.domain.user.Role;
import hanghaeboard.domain.user.User;
import hanghaeboard.domain.user.UserRepository;
import hanghaeboard.util.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Transactional
    public CreateCommentResponse createComment(CreateCommentRequest request, String jwtToken, Long boardId){
        String username = jwtUtil.getUsername(jwtToken);

        Board board = boardRepository.findById(boardId)
                .orElseThrow(()-> new EntityNotFoundException("조회된 게시물이 없습니다."));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("일치하는 회원이 없습니다."));

        Comment comment = Comment.builder().user(user).board(board).content(request.getContent()).build();
        Comment savedComment = commentRepository.save(comment);

        return CreateCommentResponse.from(savedComment);
    }

    @Transactional
    public UpdateCommentResponse updateComment(UpdateCommentRequest request, String jwtToken, Long commentId){

        Comment comment = findCommentById(commentId);

        validAuthority(comment, jwtToken);

        if(comment.getBoard().isDeleted()){
            throw new EntityNotFoundException("삭제된 게시물입니다.");
        }

        comment.modifyContent(request.getContent());

        return UpdateCommentResponse.from(comment);
    }

    private void validAuthority(Comment comment, String jwtToken) {
        String username = jwtUtil.getUsername(jwtToken);
        Role role = jwtUtil.getRole(jwtToken);

        if(Role.ADMIN != role && comment.isNotWriteUser(username)){
            throw new AuthorityException("권한이 없습니다.");
        }
    }

    private Comment findCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("조회된 댓글이 없습니다."));
    }

    @Transactional
    public DeleteCommentResponse deleteComment(String jwtToken, Long commentId){

        Comment comment = findCommentById(commentId);
        LocalDateTime deletedDateTime = LocalDateTime.now();

        validAuthority(comment, jwtToken);

        if(!comment.isDeleted()){
            comment.delete(deletedDateTime);
        }

        return DeleteCommentResponse.from(comment);
    }
}
