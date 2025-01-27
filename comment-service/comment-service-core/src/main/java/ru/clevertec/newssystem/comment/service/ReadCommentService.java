package ru.clevertec.newssystem.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Window;
import org.springframework.stereotype.Service;
import ru.clevertec.newssystem.comment.domain.Comment;
import ru.clevertec.newssystem.comment.exception.CommentBadRequestException;
import ru.clevertec.newssystem.comment.exception.NewsBadRequestException;
import ru.clevertec.newssystem.comment.filter.CommentFilter;
import ru.clevertec.newssystem.comment.port.CommentDomainMapper;
import ru.clevertec.newssystem.comment.port.input.comment.ReadCommentUseCase;
import ru.clevertec.newssystem.comment.port.input.comment.result.ReadCommentUseCaseResult;
import ru.clevertec.newssystem.comment.port.output.client.ReadNewsClientPort;
import ru.clevertec.newssystem.comment.port.output.persistence.ReadCommentPersistencePort;
import ru.clevertec.newssystem.comment.port.output.persistence.result.CommentPersistencePortResult;

import java.util.UUID;

/**
 * A service for reading comments. Implements {@link ReadCommentUseCase}.
 */
@Service
@RequiredArgsConstructor
public class ReadCommentService implements ReadCommentUseCase {

    private final CommentDomainMapper commentMapper;

    private final ReadCommentPersistencePort readCommentPersistencePort;

    private final ReadNewsClientPort readNewsClientPort;

    /**
     * Method for reading all comments related to a news
     */
    @Override
    public Window<ReadCommentUseCaseResult> readAllComments(UUID newsId,
                                                            CommentFilter commentFilter,
                                                            Pageable pageable) {
        checkIfNewsExists(newsId);

        Window<CommentPersistencePortResult> commentPersistencePortResults =
                readCommentPersistencePort.readAllComments(newsId, commentFilter, pageable);

        Window<Comment> comments = commentPersistencePortResults.map(commentMapper::toComment);

        return comments.map(commentMapper::toReadCommentUseCaseResult);
    }

    /**
     * A method for reading the comment related to a news
     */
    public ReadCommentUseCaseResult readComment(UUID newsId, UUID commentId) {
        checkIfNewsExists(newsId);

        Comment comment = getComment(commentId, newsId);

        return commentMapper.toReadCommentUseCaseResult(comment);
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
