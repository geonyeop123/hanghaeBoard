package hanghaeboard.api.service.comment.response;

import hanghaeboard.domain.comment.Comment;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class DeleteCommentResponse {
    private LocalDateTime deletedDatetime;

    @Builder
    private DeleteCommentResponse(LocalDateTime deletedDatetime) {
        this.deletedDatetime = deletedDatetime;
    }

    public static DeleteCommentResponse from(Comment comment){
        return DeleteCommentResponse.builder().deletedDatetime(comment.getDeletedDatetime()).build();
    }
}
