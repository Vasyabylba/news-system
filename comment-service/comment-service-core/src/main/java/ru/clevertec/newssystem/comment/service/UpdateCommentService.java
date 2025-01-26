package ru.clevertec.newssystem.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.clevertec.newssystem.comment.domain.Comment;
import ru.clevertec.newssystem.comment.exception.CommentBadRequestException;
import ru.clevertec.newssystem.comment.exception.NewsBadRequestException;
import ru.clevertec.newssystem.comment.port.CommentDomainMapper;
import ru.clevertec.newssystem.comment.port.input.comment.UpdateCommentUseCase;
import ru.clevertec.newssystem.comment.port.input.comment.command.UpdateCommentUseCaseCommand;
import ru.clevertec.newssystem.comment.port.input.comment.result.UpdateCommentUseCaseResult;
import ru.clevertec.newssystem.comment.port.output.client.ReadNewsClientPort;
import ru.clevertec.newssystem.comment.port.output.persistence.ReadCommentPersistencePort;
import ru.clevertec.newssystem.comment.port.output.persistence.UpdateCommentPersistencePort;
import ru.clevertec.newssystem.comment.port.output.persistence.command.UpdateCommentPersistencePortCommand;
import ru.clevertec.newssystem.comment.port.output.persistence.result.CommentPersistencePortResult;

import java.util.UUID;

/**
 * A service for updating comments. Implements {@link UpdateCommentUseCase}.
 */
@Service
@RequiredArgsConstructor
public class UpdateCommentService implements UpdateCommentUseCase {

    private final CommentDomainMapper commentMapper;

    private final UpdateCommentPersistencePort updateCommentPersistencePort;

    private final ReadCommentPersistencePort readCommentPersistencePort;

    private final ReadNewsClientPort readNewsClientPort;

    /**
     * Method for updating the comment
     */
    @Override
    public UpdateCommentUseCaseResult updateComment(UUID newsId, UUID commentId, UpdateCommentUseCaseCommand command) {
        checkIfNewsExists(newsId);

        Comment comment = getComment(commentId, newsId);

        commentMapper.updateWithNull(command, comment);
        UpdateCommentPersistencePortCommand updateCommentPersistencePortCommand =
                commentMapper.toUpdateCommentPersistencePortCommand(comment);
        CommentPersistencePortResult commentPersistencePortResult =
                updateCommentPersistencePort.updateComment(updateCommentPersistencePortCommand);
        Comment updatedComment = commentMapper.toComment(commentPersistencePortResult);

        return commentMapper.toUpdateCommentUseCaseResult(updatedComment);
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

    private Comment getComment(UUID commentId, UUID newsId) {
        checkIfCommentIdIsNotNull(commentId);

        return commentMapper.toComment(readCommentPersistencePort.readComment(commentId, newsId));
    }

    private void checkIfCommentIdIsNotNull(UUID commentId) {
        if (commentId == null) {
            throw CommentBadRequestException.byCommentIdIsNull();
        }
    }

}
