package hanghaeboard.api.service.user.response;

import hanghaeboard.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FindUser {

    private Long userId;
    private String username;

    @Builder
    public FindUser(Long userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    public static FindUser from(User user) {
        return new FindUser(user.getId(), user.getUsername());
    }
}
