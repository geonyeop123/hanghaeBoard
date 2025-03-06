package hanghaeboard.api.controller.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import hanghaeboard.api.controller.comment.request.CreateCommentRequest;
import hanghaeboard.api.service.comment.CommentService;
import hanghaeboard.api.service.comment.response.CreateCommentResponse;
import hanghaeboard.domain.board.Board;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CommentService commentService;

    @DisplayName("댓글을 작성할 수 있다.")
    @Test
    void createComment() throws Exception{
        // given
        Board board = Board.builder().id(1L).writer("yeop").title("title").content("content").build();
        LocalDateTime createdDatetime = LocalDateTime.of(2025, 3, 4, 23, 0);
        CreateCommentResponse response = CreateCommentResponse.builder()
                .id(1L)
                .board(board)
                .content("comment")
                .createdDatetime(createdDatetime)
                .build();

        CreateCommentRequest request = CreateCommentRequest.builder()
                .content("comment")
                .build();

        when(commentService.createComment(any(), any(), any())).thenReturn(response);

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/boards/1/comments")
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.board.id").value(1))
                .andExpect(jsonPath("$.data.board.writer").value("yeop"))
                .andExpect(jsonPath("$.data.board.title").value("title"))
                .andExpect(jsonPath("$.data.board.content").value("content"))
                .andExpect(jsonPath("$.data.content").value("comment"))
                .andExpect(jsonPath("$.data.createdDatetime").value("2025-03-04T23:00:00"))
        ;
    }

    @DisplayName("댓글을 작성할 때 내용이 없으면 댓글을 작성할 수 없다.")
    @Test
    void createCommentWithoutContent() throws Exception{
        // given
        Board board = Board.builder().id(1L).writer("yeop").title("title").content("content").build();
        LocalDateTime createdDatetime = LocalDateTime.of(2025, 3, 4, 23, 0);
        CreateCommentResponse response = CreateCommentResponse.builder()
                .id(1L)
                .board(board)
                .content("comment")
                .createdDatetime(createdDatetime)
                .build();

        CreateCommentRequest request = CreateCommentRequest.builder()
                .build();

        when(commentService.createComment(any(), any(), any())).thenReturn(response);

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/boards/1/comments")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer token")
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("내용은 필수 입력입니다."))
                .andExpect(jsonPath("$.data").isEmpty())
        ;
    }

}