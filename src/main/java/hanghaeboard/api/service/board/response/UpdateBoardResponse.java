package hanghaeboard.api.service.board.response;


import hanghaeboard.domain.board.Board;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class UpdateBoardResponse {

    private Long id;
    private String writer;
    private String title;
    private String content;
    private LocalDateTime createdDatetime;
    private LocalDateTime lastModifiedDatetime;

    @Builder
    private UpdateBoardResponse(Long id, String writer, String title, String content, LocalDateTime createdDatetime, LocalDateTime lastModifiedDatetime) {
        this.id = id;
        this.writer = writer;
        this.title = title;
        this.content = content;
        this.createdDatetime = createdDatetime;
        this.lastModifiedDatetime = lastModifiedDatetime;
    }

    public static UpdateBoardResponse from(Board board) {
        return UpdateBoardResponse.builder()
                .id(board.getId())
                .writer(board.getUsername())
                .title(board.getTitle())
                .content(board.getContent())
                .createdDatetime(board.getCreatedDatetime())
                .lastModifiedDatetime(board.getLastModifiedDatetime())
                .build();
    }


}
