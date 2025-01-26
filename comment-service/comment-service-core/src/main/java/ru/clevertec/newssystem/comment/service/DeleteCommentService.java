package ru.clevertec.newssystem.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.clevertec.newssystem.comment.exception.CommentBadRequestException;
import ru.clevertec.newssystem.comment.exception.NewsBadRequestException;
import ru.clevertec.newssystem.comment.port.input.comment.DeleteCommentUseCase;
import ru.clevertec.newssystem.comment.port.output.client.ReadNewsClientPort;
import ru.clevertec.newssystem.comment.port.output.persistence.DeleteCommentPersistencePort;

import java.util.UUID;

/**
 * A service for deleting comments. Implements {@link DeleteCommentUseCase}.
 */
@Service
@RequiredArgsConstructor
public class DeleteCommentService implements DeleteCommentUseCase {

    private final ReadNewsClientPort readNewsClientPort;

    private final DeleteCommentPersistencePort deleteCommentPersistencePort;

    /**
     * Method for deleting the comment
     */
    @Override
    public void deleteComment(UUID newsId, UUID commentId) {
        checkIfNewsExists(newsId);

        checkIfCommentIdIsNotNull(commentId);

        deleteCommentPersistencePort.deleteComment(commentId, newsId);
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

    private void checkIfCommentIdIsNotNull(UUID commentId) {
        if (commentId == null) {
            throw CommentBadRequestException.byCommentIdIsNull();
        }
    }

}
