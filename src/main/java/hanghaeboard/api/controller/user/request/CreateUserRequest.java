package hanghaeboard.api.controller.user.request;

import hanghaeboard.domain.user.Role;
import hanghaeboard.domain.user.User;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateUserRequest {

    @NotBlank(message = "아이디는 필수 입력입니다.")
    private String username;

    @NotBlank(message = "비밀번호는 필수 입력입니다.")
    private String password;

    private Role role;

    @Builder
    private CreateUserRequest(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public User toEntity(){
        return User.builder().username(this.username).password(this.password).role(role).build();
    }
}
