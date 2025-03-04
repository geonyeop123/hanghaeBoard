package hanghaeboard.api.controller.comment.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateCommentRequest {

    @NotNull(message = "댓글 작성자 정보가 없습니다.")
    private Long userId;
    @NotBlank(message = "내용은 필수 입력입니다.")
    private String content;

    @Builder
    public CreateCommentRequest(Long userId, String content) {
        this.userId = userId;
        this.content = content;
    }
}
