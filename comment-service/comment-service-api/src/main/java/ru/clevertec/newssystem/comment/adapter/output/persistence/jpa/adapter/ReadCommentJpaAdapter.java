package ru.clevertec.newssystem.comment.adapter.output.persistence.jpa.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Window;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.clevertec.newssystem.comment.adapter.output.persistence.jpa.PersistenceJpaCommentMapper;
import ru.clevertec.newssystem.comment.adapter.output.persistence.jpa.entity.CommentEntity;
import ru.clevertec.newssystem.comment.adapter.output.persistence.jpa.repository.CommentRepository;
import ru.clevertec.newssystem.comment.adapter.output.persistence.jpa.specification.CommentSpecification;
import ru.clevertec.newssystem.comment.exception.CommentNotFoundException;
import ru.clevertec.newssystem.comment.filter.CommentFilter;
import ru.clevertec.newssystem.comment.port.output.persistence.ReadCommentPersistencePort;
import ru.clevertec.newssystem.comment.port.output.persistence.result.CommentPersistencePortResult;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ReadCommentJpaAdapter implements ReadCommentPersistencePort {

    private static final String MANDATORY_SORT_COMMENT_FIELD = "createdAt";

    private final CommentRepository commentRepository;

    private final PersistenceJpaCommentMapper commentMapper;

    @Override
    public Window<CommentPersistencePortResult> readAllComments(UUID newsId,
                                                                CommentFilter commentFilter,
                                                                Pageable pageable) {
        Specification<CommentEntity> commentSpecification = toSpecification(newsId, commentFilter);
        Window<CommentEntity> commentWindow = commentRepository.findBy(
                commentSpecification, fluentQuery -> fluentQuery
                        .sortBy(pageable.getSort().and(Sort.by(Sort.Order.desc(MANDATORY_SORT_COMMENT_FIELD))))
                        .limit(pageable.getPageSize())
                        .scroll(pageable.toScrollPosition())
        );

        return commentWindow.map(commentMapper::toCommentPersistencePortResult);
    }

    @Cacheable(cacheManager = "redisCacheManager",value = "comment", key = "#commentId.toString()")
    @Override
    public CommentPersistencePortResult readComment(UUID commentId, UUID newsId) {
        CommentEntity comment = commentRepository.findByIdAndNewsId(commentId, newsId)
                .orElseThrow(() -> CommentNotFoundException.byCommentId(commentId));

        return commentMapper.toCommentPersistencePortResult(comment);
    }

    private Specification<CommentEntity> toSpecification(UUID newsId, CommentFilter commentFilter) {
        return Specification.where(CommentSpecification.newsIdSpec(newsId))
                .and(CommentSpecification.createdAtGteSpec(commentFilter.createdAtGte()))
                .and(CommentSpecification.createdAtLteSpec(commentFilter.createdAtLte()))
                .and(CommentSpecification.textContainsSpec(commentFilter.textContains()))
                .and(CommentSpecification.usernameSpec(commentFilter.username()))
                .and(CommentSpecification.usernameStartsSpec(commentFilter.usernameStarts()));
    }

}
