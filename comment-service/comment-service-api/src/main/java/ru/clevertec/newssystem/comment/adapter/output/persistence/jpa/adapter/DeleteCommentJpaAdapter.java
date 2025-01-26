package ru.clevertec.newssystem.comment.adapter.output.persistence.jpa.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;
import ru.clevertec.newssystem.comment.adapter.output.persistence.jpa.repository.CommentRepository;
import ru.clevertec.newssystem.comment.port.output.persistence.DeleteCommentPersistencePort;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DeleteCommentJpaAdapter implements DeleteCommentPersistencePort {

    private final CommentRepository commentRepository;

    @CacheEvict(cacheManager = "redisCacheManager",value = "comment", key = "#commentId.toString()")
    @Override
    public void deleteComment(UUID commentId, UUID newsId) {
        commentRepository.deleteByIdAndNewsId(commentId, newsId);
    }

}
