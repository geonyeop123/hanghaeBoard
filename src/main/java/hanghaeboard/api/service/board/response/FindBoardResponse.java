package hanghaeboard.api.service.board.response;

import hanghaeboard.domain.board.Board;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class FindBoardResponse {

    private Long id;
    private String writer;
    private String title;
    private String content;
    private LocalDateTime createdDatetime;
    private LocalDateTime lastModifiedDatetime;

    @Builder
    public FindBoardResponse(Long id, String writer, String title, String content, LocalDateTime createdDatetime, LocalDateTime lastModifiedDatetime) {
        this.id = id;
        this.writer = writer;
        this.title = title;
        this.content = content;
        this.createdDatetime = createdDatetime;
        this.lastModifiedDatetime = lastModifiedDatetime;
    }

    public static FindBoardResponse from(Board board) {
        return FindBoardResponse.builder()
                .id(board.getId())
                .writer(board.getUsername())
                .title(board.getTitle())
                .content(board.getContent())
                .createdDatetime(board.getCreatedDatetime())
                .lastModifiedDatetime(board.getLastModifiedDatetime())
                .build();
    }
}
