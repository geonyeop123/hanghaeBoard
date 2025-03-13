package hanghaeboard.domain.comment;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hanghaeboard.api.service.comment.response.FindCommentResponse;
import hanghaeboard.domain.user.QUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommentCustomRepositoryImpl implements CommentCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<FindCommentResponse> findAllComment() {
        QComment comment = QComment.comment;
        QUser user = QUser.user;

        return queryFactory.select(
                Projections.constructor(FindCommentResponse.class, comment.id, user.username
                        , comment.content, comment.createdDatetime, comment.lastModifiedDatetime))
                .from(comment)
                .join(comment.user, user)
                .where(comment.deletedDatetime.isNull())
                .orderBy(comment.createdDatetime.desc()).fetch();
    }
}
