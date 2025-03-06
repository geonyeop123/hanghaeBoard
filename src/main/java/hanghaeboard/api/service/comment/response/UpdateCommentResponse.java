package hanghaeboard.api.service.comment.response;

import hanghaeboard.domain.board.Board;
import hanghaeboard.domain.comment.Comment;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class UpdateCommentResponse {
    private Long id;
    private Board board;
    private String content;
    private LocalDateTime createdDatetime;
    private LocalDateTime lastModifiedDatetime;

    @Builder
    private UpdateCommentResponse(Long id, Board board, String content, LocalDateTime createdDatetime, LocalDateTime lastModifiedDatetime) {
        this.id = id;
        this.board = board;
        this.content = content;
        this.createdDatetime = createdDatetime;
        this.lastModifiedDatetime = lastModifiedDatetime;
    }

    public static UpdateCommentResponse from(Comment comment){
        return UpdateCommentResponse.builder()
                .id(comment.getId())
                .board(comment.getBoard())
                .content(comment.getContent())
                .createdDatetime(comment.getCreatedDatetime())
                .lastModifiedDatetime(comment.getLastModifiedDatetime())
                .build();
    }
}
