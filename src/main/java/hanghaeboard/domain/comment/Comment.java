package hanghaeboard.domain.comment;

import hanghaeboard.domain.BaseEntity;
import hanghaeboard.domain.board.Board;
import hanghaeboard.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String content;

    public void modifyContent(String content) {
        this.content = content;
    }

    public boolean isNotWriteUser(String username){
        return !this.user.getUsername().equals(username);
    }

    @Builder
    private Comment(Long id, Board board, User user, String content) {
        this.id = id;
        this.board = board;
        this.user = user;
        this.content = content;
    }
}
