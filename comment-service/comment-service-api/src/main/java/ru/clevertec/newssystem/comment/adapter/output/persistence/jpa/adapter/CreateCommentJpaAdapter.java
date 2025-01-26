package ru.clevertec.newssystem.comment.adapter.output.persistence.jpa.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Component;
import ru.clevertec.newssystem.comment.adapter.output.persistence.jpa.PersistenceJpaCommentMapper;
import ru.clevertec.newssystem.comment.adapter.output.persistence.jpa.entity.CommentEntity;
import ru.clevertec.newssystem.comment.adapter.output.persistence.jpa.repository.CommentRepository;
import ru.clevertec.newssystem.comment.port.output.persistence.CreateCommentPersistencePort;
import ru.clevertec.newssystem.comment.port.output.persistence.command.CreateCommentPersistencePortCommand;
import ru.clevertec.newssystem.comment.port.output.persistence.result.CommentPersistencePortResult;

@Component
@RequiredArgsConstructor
public class CreateCommentJpaAdapter implements CreateCommentPersistencePort {

    private final PersistenceJpaCommentMapper commentMapper;

    private final CommentRepository commentRepository;

    @CachePut(cacheManager = "redisCacheManager",value = "comment", key = "#result.id.toString()")
    @Override
    public CommentPersistencePortResult createComment(CreateCommentPersistencePortCommand command) {
        CommentEntity comment = commentMapper.toCommentEntity(command);

        CommentEntity savedComment = commentRepository.save(comment);

        return commentMapper.toCommentPersistencePortResult(savedComment);
    }

}
