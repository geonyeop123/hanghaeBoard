package hanghaeboard.domain.board;


import hanghaeboard.domain.BaseEntity;
import hanghaeboard.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String title;

    private String content;

    public void changeBoard(String title, String content){
        this.title = title;
        this.content = content;
    }

    public boolean isNotWriter(String username){
        return !this.getUser().getUsername().equals(username);
    }

    public String getUsername(){
        return this.user.getUsername();
    }

    @Builder
    private Board(Long id, User user, String title, String content, LocalDateTime createdDatetime) {
        this.id = id;
        this.user = user;
        this.title = title;
        this.content = content;
    }
}
