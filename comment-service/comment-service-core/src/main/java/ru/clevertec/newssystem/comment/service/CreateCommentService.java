package ru.clevertec.newssystem.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.clevertec.newssystem.comment.domain.Comment;
import ru.clevertec.newssystem.comment.exception.NewsBadRequestException;
import ru.clevertec.newssystem.comment.port.CommentDomainMapper;
import ru.clevertec.newssystem.comment.port.input.comment.CreateCommentUseCase;
import ru.clevertec.newssystem.comment.port.input.comment.command.CreateCommentUseCaseCommand;
import ru.clevertec.newssystem.comment.port.input.comment.result.CreateCommentUseCaseResult;
import ru.clevertec.newssystem.comment.port.output.client.ReadNewsClientPort;
import ru.clevertec.newssystem.comment.port.output.persistence.CreateCommentPersistencePort;
import ru.clevertec.newssystem.comment.port.output.persistence.command.CreateCommentPersistencePortCommand;
import ru.clevertec.newssystem.comment.port.output.persistence.result.CommentPersistencePortResult;

import java.util.UUID;

/**
 * A service for creating comments. Implements {@link CreateCommentUseCase}.
 */
@Service
@RequiredArgsConstructor
public class CreateCommentService implements CreateCommentUseCase {

    private final CommentDomainMapper commentMapper;

    private final CreateCommentPersistencePort createCommentPersistencePort;

    private final ReadNewsClientPort readNewsClientPort;

    /**
     * Method for creating the comment
     */
    @Override
    public CreateCommentUseCaseResult createComment(UUID newsId, CreateCommentUseCaseCommand command) {
        checkIfNewsExists(newsId);

        Comment comment = commentMapper.toComment(newsId, command);

        CreateCommentPersistencePortCommand createCommentPersistencePortCommand =
                commentMapper.toCreateCommentPersistencePortCommand(comment);
        CommentPersistencePortResult commentPersistencePortResult =
                createCommentPersistencePort.createComment(createCommentPersistencePortCommand);

        Comment savedComment = commentMapper.toComment(commentPersistencePortResult);

        return commentMapper.toCreateCommentUseCaseResult(savedComment);
    }

    private void checkIfNewsExists(UUID newsId) {
        checkIfNewsIdIsNotNull(newsId);

        readNewsClientPort.readNews(newsId);
    }

    private void checkIfNewsIdIsNotNull(UUID newsId) {
        if (newsId == null) {
            throw NewsBadRequestException.byNewsIdIsNull();
        }
    }

}
