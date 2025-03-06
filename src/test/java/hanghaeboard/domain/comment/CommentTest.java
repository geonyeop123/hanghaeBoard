package hanghaeboard.domain.comment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CommentTest {

    @DisplayName("내용을 수정할 수 있다.")
    @Test
    void modifyContent() {
        // given
        Comment comment = Comment.builder().content("content").build();

        // when
        comment.modifyContent("modifiedContent");

        // then
        assertThat(comment.getContent()).isEqualTo("modifiedContent");
    }

}