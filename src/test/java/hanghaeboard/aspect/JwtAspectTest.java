package hanghaeboard.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import hanghaeboard.api.controller.comment.request.CreateCommentRequest;
import hanghaeboard.domain.user.User;
import hanghaeboard.util.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class JwtAspectTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    JwtUtil jwtUtil;

    @DisplayName("유효하지 않은 토큰으로 API를 요청할 수 없다.")
    @Test
    void autoCheck_notValidateToken() throws Exception {

        CreateCommentRequest request = CreateCommentRequest.builder()
                .content("comment")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/boards/1/comments")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("401"))
                .andExpect(jsonPath("$.message").value("유효하지 않은 JWT 토큰입니다."))
                .andExpect(jsonPath("$.data").isEmpty())
                ;
    }

    @DisplayName("유효한 토큰으로 API를 요청할 수 있다.")
    @Test
    void autoCheck_validateToken() throws Exception {

        CreateCommentRequest request = CreateCommentRequest.builder()
                .content("comment")
                .build();

        User user = User.builder().username("yeop").password("12345678").build();
        String token = "Bearer " + jwtUtil.generateToken(user, LocalDateTime.now());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/boards/1/comments")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("401"))
                .andExpect(jsonPath("$.message").value("유효하지 않은 JWT 토큰입니다."))
                .andExpect(jsonPath("$.data").isEmpty())
        ;
    }


}