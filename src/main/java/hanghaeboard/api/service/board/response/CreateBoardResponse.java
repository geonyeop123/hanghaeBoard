package hanghaeboard.api.service.board.response;

import hanghaeboard.domain.board.Board;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CreateBoardResponse {

    private Long id;
    private String writer;
    private String title;
    private String content;
    private LocalDateTime createdDatetime;

    @Builder
    private CreateBoardResponse(Long id, String writer, String title, String content, LocalDateTime createdDatetime) {
        this.id = id;
        this.writer = writer;
        this.title = title;
        this.content = content;
        this.createdDatetime = createdDatetime;
    }

    public static CreateBoardResponse from(Board board) {
        return CreateBoardResponse.builder()
                .id(board.getId())
                .writer(board.getUser().getUsername())
                .title(board.getTitle())
                .content(board.getContent())
                .createdDatetime(board.getCreatedDatetime())
                .build();
    }
}
