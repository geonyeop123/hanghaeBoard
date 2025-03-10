package hanghaeboard.domain.board;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hanghaeboard.api.service.board.response.FindBoardResponse;
import hanghaeboard.domain.user.QUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BoardCustomRepositoryImpl implements BoardCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<FindBoardResponse> findAllBoard() {
        QBoard board = QBoard.board;
        QUser user = QUser.user;

        return queryFactory.select(
                Projections.constructor(FindBoardResponse.class, board.id, user.username
                        , board.title, board.content, board.createdDatetime, board.lastModifiedDatetime))
                .from(board)
                .join(board.user, user)
                .where(board.deletedDatetime.isNull())
                .orderBy(board.createdDatetime.desc()).fetch();
    }
}
