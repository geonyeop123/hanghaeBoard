package hanghaeboard.api.service.comment.response;

import hanghaeboard.api.service.board.response.FindBoardResponse;
import hanghaeboard.domain.comment.Comment;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CreateCommentResponse {
    private Long id;
    private FindBoardResponse board;
    private String content;
    private LocalDateTime createdDatetime;

    @Builder
    private CreateCommentResponse(Long id, FindBoardResponse board, String content, LocalDateTime createdDatetime) {
        this.id = id;
        this.board = board;
        this.content = content;
        this.createdDatetime = createdDatetime;
    }

    public static CreateCommentResponse from(Comment comment){
        return CreateCommentResponse.builder()
                .id(comment.getId())
                .board(FindBoardResponse.from(comment.getBoard()))
                .content(comment.getContent())
                .createdDatetime(comment.getCreatedDatetime())
                .build();
    }
}
