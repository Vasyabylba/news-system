package ru.clevertec.newssystem.comment.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.newssystem.comment.exception.CommentBadRequestException;
import ru.clevertec.newssystem.comment.exception.NewsBadRequestException;
import ru.clevertec.newssystem.comment.port.output.client.ReadNewsClientPort;
import ru.clevertec.newssystem.comment.port.output.client.result.ReadNewsClientPortResult;
import ru.clevertec.newssystem.comment.port.output.persistence.DeleteCommentPersistencePort;
import ru.clevertec.newssystem.comment.util.TestData;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteCommentServiceTest {

    public static final String NEWS_ID_MUST_NOT_BE_NULL = "News id must not be null";

    public static final String COMMENT_ID_MUST_NOT_BE_NULL = "Comment id must not be null";

    @Mock
    private DeleteCommentPersistencePort deleteCommentPersistencePort;

    @Mock
    private ReadNewsClientPort readNewsClientPort;

    @InjectMocks
    private DeleteCommentService deleteCommentService;

    @Test
    void shouldDeleteComment() {
        // given
        UUID newsId = UUID.randomUUID();
        UUID commentId = UUID.randomUUID();

        ReadNewsClientPortResult readNewsClientPortResult = TestData.createReadNewsClientPortResult(newsId);

        when(readNewsClientPort.readNews(newsId))
                .thenReturn(readNewsClientPortResult);
        doNothing()
                .when(deleteCommentPersistencePort).deleteComment(commentId, newsId);

        // when
        deleteCommentService.deleteComment(newsId, commentId);

        // then
        verify(readNewsClientPort)
                .readNews(newsId);
        verify(deleteCommentPersistencePort)
                .deleteComment(commentId, newsId);
    }

    @Test
    void shouldThrowNewsBadRequestException_whenNewsIdIsNull() {
        // given
        UUID newsId = null;
        UUID commentId = UUID.randomUUID();

        // when, then
        NewsBadRequestException expected = assertThrows(
                NewsBadRequestException.class,
                () -> deleteCommentService.deleteComment(newsId, commentId)
        );

        assertThat(expected)
                .hasMessageContaining(NEWS_ID_MUST_NOT_BE_NULL);

        verifyNoInteractions(readNewsClientPort);
        verifyNoInteractions(deleteCommentPersistencePort);
    }

    @Test
    void shouldThrowCommentBadRequestException_whenCommentIdIsNull() {
        // given
        UUID newsId = UUID.randomUUID();
        UUID commentId = null;

        ReadNewsClientPortResult readNewsClientPortResult = TestData.createReadNewsClientPortResult(newsId);

        when(readNewsClientPort.readNews(newsId))
                .thenReturn(readNewsClientPortResult);

        // when, then
        CommentBadRequestException expected = assertThrows(
                CommentBadRequestException.class,
                () -> deleteCommentService.deleteComment(newsId, commentId)
        );

        assertThat(expected)
                .hasMessageContaining(COMMENT_ID_MUST_NOT_BE_NULL);

        verifyNoMoreInteractions(readNewsClientPort);
        verifyNoInteractions(deleteCommentPersistencePort);
    }

}