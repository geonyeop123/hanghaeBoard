package hanghaeboard.api.service.board.response;

import hanghaeboard.api.service.comment.response.FindCommentResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class FindBoardWithCommentResponse {
    private Long id;
    private String writer;
    private String title;
    private String content;
    private List<FindCommentResponse> comments = new ArrayList<>();

    private LocalDateTime createdDatetime;
    private LocalDateTime lastModifiedDatetime;
    private LocalDateTime deletedDatetime;

    @Builder
    public FindBoardWithCommentResponse(Long id, String writer, String title, String content, List<FindCommentResponse> comments, LocalDateTime createdDatetime, LocalDateTime lastModifiedDatetime, LocalDateTime deletedDatetime) {
        this.id = id;
        this.writer = writer;
        this.title = title;
        this.content = content;
        this.comments = comments;
        this.createdDatetime = createdDatetime;
        this.lastModifiedDatetime = lastModifiedDatetime;
        this.deletedDatetime = deletedDatetime;
    }

    public FindBoardWithCommentResponse(Long id, String writer, String title, String content, LocalDateTime createdDatetime, LocalDateTime lastModifiedDatetime, LocalDateTime deletedDatetime) {
        this.id = id;
        this.writer = writer;
        this.title = title;
        this.content = content;
        this.createdDatetime = createdDatetime;
        this.lastModifiedDatetime = lastModifiedDatetime;
        this.deletedDatetime = deletedDatetime;
    }

    public void setComments(List<FindCommentResponse> comments) {
        this.comments.addAll(comments);
    }

    public boolean isDeleted(){
        return deletedDatetime != null;
    }
}
