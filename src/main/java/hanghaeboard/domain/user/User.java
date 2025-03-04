package hanghaeboard.domain.user;

import hanghaeboard.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

import static hanghaeboard.constants.UserValidation.PASSWORD;
import static hanghaeboard.constants.UserValidation.USERNAME;

@Table(name = "users")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String username;

    private String password;

    @Builder
    private User(Long id, String username, String password) {
        validation(username, password);
        this.id = id;
        this.username = username;
        this.password = password;
    }

    private void validation(String username, String password){
        validationUsername(username);
        validationPassword(password);
    }

    private void validationUsername(String username){
        int length = username.length();
        if(length < USERNAME.getMin() || length > USERNAME.getMax()){
            throw new IllegalArgumentException("아이디는 4글자 이상, 10글자 이하여야 합니다.");
        }else if(!Pattern.matches(USERNAME.getRegexp(), username)){
            throw new IllegalArgumentException("아이디는 소문자 영문과 숫자로만 이루어져야 합니다.");
        }
    }

    private void validationPassword(String password) {
        int length = password.length();
        if(length < PASSWORD.getMin() || length > PASSWORD.getMax()){
            throw new IllegalArgumentException("비밀번호는 8글자 이상, 15글자 이하여야 합니다.");
        }else if(!Pattern.matches(PASSWORD.getRegexp(), password)){
            throw new IllegalArgumentException("비밀번호는 소문자 혹은 대문자 영문과 숫자로만 이루어져야 합니다.");
        }
    }

    public boolean isCorrectPassword(String password){
        return this.password.equals(password);
    }
}
