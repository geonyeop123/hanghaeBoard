package hanghaeboard.api.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import hanghaeboard.api.controller.user.request.CreateUserRequest;
import hanghaeboard.api.controller.user.request.LoginRequest;
import hanghaeboard.api.exception.exception.InvalidPasswordException;
import hanghaeboard.api.service.user.UserService;
import hanghaeboard.api.service.user.response.FindUser;
import hanghaeboard.api.service.user.response.LoginResponse;
import hanghaeboard.domain.user.Role;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;


    @DisplayName("회원가입을 할 수 있다.")
    @Test
    void join()  throws Exception{
        // given
        CreateUserRequest request = CreateUserRequest.builder()
                .username("yeop")
                .password("Pass12!@")
                .build();

        FindUser response = FindUser.builder()
                .userId(1L)
                .username("yeop")
                .build();

        when(userService.join(any())).thenReturn(response);

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.username").value("yeop"))
                ;
    }

    @DisplayName("관리자 계정으로 회원가입을 할 수 있다.")
    @Test
    void join_admin()  throws Exception{
        // given
        CreateUserRequest request = CreateUserRequest.builder()
                .username("yeop")
                .password("Pass12!@")
                .role(Role.ADMIN)
                .build();

        FindUser response = FindUser.builder()
                .userId(1L)
                .username("yeop")
                .role(Role.ADMIN)
                .build();

        when(userService.join(any())).thenReturn(response);

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.username").value("yeop"))
                .andExpect(jsonPath("$.data.role").value("ADMIN"))
        ;
    }

    @DisplayName("회원가입 시 동일한 username으로 회원가입을 할 수 없다.")
    @Test
    void join_duplicateUsername() throws Exception{
        CreateUserRequest request = CreateUserRequest.builder()
                .username("yeop")
                .password("Pass12!@")
                .build();

        when(userService.join(any())).thenThrow(new DuplicateKeyException("이미 존재하는 ID입니다."));

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("409"))
                .andExpect(jsonPath("$.message").value("이미 존재하는 ID입니다."))
                .andExpect(jsonPath("$.data").isEmpty())
        ;
    }

    @DisplayName("로그인을 할 수 있다.")
    @Test
    void login()  throws Exception{
        // given
        LoginRequest request = LoginRequest.builder()
                .username("yeop")
                .password("Pass12!@")
                .build();

        LoginResponse response = LoginResponse.builder()
                .jwtToken("token")
                .build();

        when(userService.login(any())).thenReturn(response);

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users/login")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.AUTHORIZATION))
                .andExpect(header().string(HttpHeaders.AUTHORIZATION, Matchers.startsWith("Bearer ")))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").isEmpty())
        ;
    }

    @DisplayName("로그인 시 username에 해당하는 user가 없는 경우 로그인을 할 수 없다.")
    @Test
    void login_notFoundUser()  throws Exception{
        // given
        LoginRequest request = LoginRequest.builder()
                .username("yeop")
                .password("Pass12!@")
                .build();


        when(userService.login(any())).thenThrow(new IllegalArgumentException("일치하는 회원이 없습니다."));

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users/login")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("일치하는 회원이 없습니다."))
                .andExpect(jsonPath("$.data").isEmpty())
        ;
    }

    @DisplayName("로그인 시 비밀번호가 틀린 경우 로그인을 할 수 없다.")
    @Test
    void login_isNotCorrectPassword()  throws Exception{
        // given
        LoginRequest request = LoginRequest.builder()
                .username("yeop")
                .password("Pass12!@")
                .build();

        when(userService.login(any())).thenThrow(new InvalidPasswordException("비밀번호가 올바르지 않습니다."));

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users/login")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("비밀번호가 올바르지 않습니다."))
                .andExpect(jsonPath("$.data").isEmpty())
        ;
    }

}