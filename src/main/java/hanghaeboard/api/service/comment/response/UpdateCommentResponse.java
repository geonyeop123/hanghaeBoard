package hanghaeboard.api.service.comment.response;

import hanghaeboard.api.service.board.response.FindBoardResponse;
import hanghaeboard.domain.comment.Comment;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class UpdateCommentResponse {
    private Long id;
    private String content;
    private LocalDateTime createdDatetime;
    private LocalDateTime lastModifiedDatetime;

    @Builder
    private UpdateCommentResponse(Long id, String content, LocalDateTime createdDatetime, LocalDateTime lastModifiedDatetime) {
        this.id = id;
        this.content = content;
        this.createdDatetime = createdDatetime;
        this.lastModifiedDatetime = lastModifiedDatetime;
    }

    public static UpdateCommentResponse from(Comment comment){
        return UpdateCommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdDatetime(comment.getCreatedDatetime())
                .lastModifiedDatetime(comment.getLastModifiedDatetime())
                .build();
    }
}
