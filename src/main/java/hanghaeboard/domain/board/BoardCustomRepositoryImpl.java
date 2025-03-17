package hanghaeboard.domain.board;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hanghaeboard.api.service.board.response.FindBoardWithCommentResponse;
import hanghaeboard.api.service.comment.response.FindCommentResponse;
import hanghaeboard.domain.comment.QComment;
import hanghaeboard.domain.user.QUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class BoardCustomRepositoryImpl implements BoardCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<FindBoardWithCommentResponse> findAllBoard() {
        QBoard board = QBoard.board;
        QUser boardUser = new QUser("boardUser");
        QComment comment = QComment.comment;
        QUser commentUser = new QUser("commentUser");

        // 전체 목록 조회
        List<Tuple> result = queryFactory.select(
                board.id, board.user.username, board.title, board.content, board.createdDatetime, board.lastModifiedDatetime, board.deletedDatetime
                , comment.id, comment.user.username, comment.content, comment.createdDatetime, comment.lastModifiedDatetime, comment.deletedDatetime
                )
                .from(board)
                    .join(board.user, boardUser)
                    .leftJoin(comment)
                        .on(comment.board.id.eq(board.id)
                            .and(comment.deletedDatetime.isNull()))
                    .leftJoin(comment.user, commentUser)
                .where(board.deletedDatetime.isNull())
                    .orderBy(board.createdDatetime.desc(), comment.createdDatetime.desc())
                .fetch();

        // 맵 생성
        Map<Long, FindBoardWithCommentResponse> map = new LinkedHashMap<>();

        for(Tuple tuple : result){

            Long boardId = tuple.get(board.id);

            // 최초 객체일 시 생성
            map.computeIfAbsent(boardId, id ->
                    FindBoardWithCommentResponse.builder()
                            .id(boardId)
                            .writer(tuple.get(board.user.username))
                            .title(tuple.get(board.title))
                            .content(tuple.get(board.content))
                            .createdDatetime(tuple.get(board.createdDatetime))
                            .lastModifiedDatetime(tuple.get(board.lastModifiedDatetime))
                            .build());

            // 댓글이 있을 시 댓글 추가
            if(null != tuple.get(comment.id)){

                FindCommentResponse commentResponse = FindCommentResponse.builder()
                        .id(tuple.get(comment.id))
                        .writer(tuple.get(comment.user.username))
                        .content(tuple.get(comment.content))
                        .createdDatetime(tuple.get(comment.createdDatetime))
                        .lastModifiedDatetime(tuple.get(comment.lastModifiedDatetime))
                        .build();

                map.get(boardId).getComments().add(commentResponse);
            }
        }

        return new ArrayList<>(map.values());
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
