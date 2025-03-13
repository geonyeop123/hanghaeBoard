package hanghaeboard.domain.board;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hanghaeboard.api.service.board.response.FindBoardResponse;
import hanghaeboard.api.service.board.response.FindBoardWithCommentResponse;
import hanghaeboard.api.service.comment.response.FindCommentResponse;
import hanghaeboard.domain.comment.QComment;
import hanghaeboard.domain.user.QUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

    @Override
    public Optional<FindBoardWithCommentResponse> findBoardWithComment(Long boardId) {
        QBoard board = QBoard.board;
        QUser user = QUser.user;
        QComment comment = QComment.comment;

        FindBoardWithCommentResponse response = queryFactory.select(
                        Projections.constructor(FindBoardWithCommentResponse.class, board.id, user.username
                                , board.title, board.content, board.createdDatetime, board.lastModifiedDatetime, board.deletedDatetime))
                .from(board)
                .join(board.user, user)
                .where(board.id.eq(boardId))
                .fetchOne();

        if (response == null) {
            return Optional.empty();
        }

        List<FindCommentResponse> comments = queryFactory.select(
                    Projections.constructor(FindCommentResponse.class
                    , comment.id, user.username, comment.content, comment.createdDatetime, comment.lastModifiedDatetime))
                .from(comment)
                .join(comment.user, user)
                .where(comment.board.id.eq(boardId)
                        , comment.deletedDatetime.isNull())
                .orderBy(comment.createdDatetime.desc()).fetch();

        response.setComments(comments);

        return Optional.of(response);
    }
}
