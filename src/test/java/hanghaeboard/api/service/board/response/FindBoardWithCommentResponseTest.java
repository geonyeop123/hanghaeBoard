package hanghaeboard.api.service.board.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class FindBoardWithCommentResponseTest {

    @DisplayName("builder pattern으로 생성 시 comments가 null이 아닌 빈 객체이다.")
    @Test
    void test() {
        // given // when
        FindBoardWithCommentResponse response =
                FindBoardWithCommentResponse.builder()
                        .id(1L)
                        .title("title")
                        .content("content")
                        .writer("writer")
                        .build();

        // then
        assertThat(response.getComments()).isNotNull();
    }
}