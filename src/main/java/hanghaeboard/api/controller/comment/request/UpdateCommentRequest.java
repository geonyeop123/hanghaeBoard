package hanghaeboard.api.controller.comment.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateCommentRequest {
    @NotBlank(message = "내용은 필수 입력입니다.")
    private String content;

    @Builder
    private UpdateCommentRequest(String content) {
        this.content = content;
    }
}
