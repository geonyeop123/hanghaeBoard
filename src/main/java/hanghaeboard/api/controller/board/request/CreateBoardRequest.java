package hanghaeboard.api.controller.board.request;

import hanghaeboard.domain.board.Board;
import hanghaeboard.domain.user.User;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateBoardRequest {

    @NotBlank(message = "제목은 필수 입력입니다.")
    private String title;

    @NotBlank(message = "내용은 필수 입력입니다.")
    private String content;

    @Builder
    private CreateBoardRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public Board toEntity(User user) {
        return Board.builder()
                .user(user)
                .title(title)
                .content(content)
                .build();
    }
}
