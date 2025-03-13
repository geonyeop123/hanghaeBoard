package hanghaeboard.domain.comment;

import hanghaeboard.api.service.comment.response.FindCommentResponse;

import java.util.List;

public interface CommentCustomRepository {
    List<FindCommentResponse> findAllComment();
}
