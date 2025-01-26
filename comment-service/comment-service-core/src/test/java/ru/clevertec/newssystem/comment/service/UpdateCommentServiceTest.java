package ru.clevertec.newssystem.comment.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.newssystem.comment.domain.Comment;
import ru.clevertec.newssystem.comment.exception.CommentBadRequestException;
import ru.clevertec.newssystem.comment.exception.NewsBadRequestException;
import ru.clevertec.newssystem.comment.port.CommentDomainMapper;
import ru.clevertec.newssystem.comment.port.input.comment.command.UpdateCommentUseCaseCommand;
import ru.clevertec.newssystem.comment.port.input.comment.result.UpdateCommentUseCaseResult;
import ru.clevertec.newssystem.comment.port.output.client.ReadNewsClientPort;
import ru.clevertec.newssystem.comment.port.output.client.result.ReadNewsClientPortResult;
import ru.clevertec.newssystem.comment.port.output.persistence.ReadCommentPersistencePort;
import ru.clevertec.newssystem.comment.port.output.persistence.UpdateCommentPersistencePort;
import ru.clevertec.newssystem.comment.port.output.persistence.command.UpdateCommentPersistencePortCommand;
import ru.clevertec.newssystem.comment.port.output.persistence.result.CommentPersistencePortResult;
import ru.clevertec.newssystem.comment.util.TestData;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateCommentServiceTest {

    public static final String NEWS_ID_MUST_NOT_BE_NULL = "News id must not be null";

    public static final String COMMENT_ID_MUST_NOT_BE_NULL = "Comment id must not be null";

    @Mock
    private CommentDomainMapper commentMapper;

    @Mock
    private UpdateCommentPersistencePort updateCommentPersistencePort;

    @Mock
    private ReadCommentPersistencePort readCommentPersistencePort;

    @Mock
    private ReadNewsClientPort readNewsClientPort;

    @InjectMocks
    private UpdateCommentService updateCommentService;

    @Test
    void shouldUpdateComment() {
        // given
        UUID newsId = UUID.randomUUID();
        UUID commentId = UUID.randomUUID();
        UpdateCommentUseCaseCommand updateCommentUseCaseCommand = TestData.createUpdateCommentUseCaseCommand();

        ReadNewsClientPortResult readNewsClientPortResult = TestData.createReadNewsClientPortResult(newsId);

        CommentPersistencePortResult commentPersistencePortResult =
                TestData.createCommentPersistencePortResult(newsId, commentId);
        Comment originalComment = TestData.createComment(commentPersistencePortResult);

        Comment updatingComment = TestData.toUpdatingComment(updateCommentUseCaseCommand, originalComment);
        UpdateCommentPersistencePortCommand updateCommentPersistencePortCommand =
                TestData.createUpdateCommentPersistencePortCommand(updatingComment);
        CommentPersistencePortResult updateCommentPersistencePortResult =
                TestData.createCommentPersistencePortResult(updateCommentPersistencePortCommand);
        Comment updatedComment = TestData.createUpdatedComment(updateCommentPersistencePortResult);
        UpdateCommentUseCaseResult expected = TestData.createUpdateCommentUseCaseResult(updatedComment);

        when(readNewsClientPort.readNews(newsId))
                .thenReturn(readNewsClientPortResult);
        when(readCommentPersistencePort.readComment(commentId, newsId))
                .thenReturn(commentPersistencePortResult);
        when(commentMapper.toComment(commentPersistencePortResult))
                .thenReturn(originalComment);
        when(commentMapper.updateWithNull(updateCommentUseCaseCommand, originalComment))
                .thenReturn(updatingComment);
        when(commentMapper.toUpdateCommentPersistencePortCommand(updatingComment))
                .thenReturn(updateCommentPersistencePortCommand);
        when(updateCommentPersistencePort.updateComment(updateCommentPersistencePortCommand))
                .thenReturn(updateCommentPersistencePortResult);
        when(commentMapper.toComment(updateCommentPersistencePortResult))
                .thenReturn(updatedComment);
        when(commentMapper.toUpdateCommentUseCaseResult(updatedComment))
                .thenReturn(expected);

        // when
        UpdateCommentUseCaseResult actual =
                updateCommentService.updateComment(newsId, commentId, updateCommentUseCaseCommand);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.id()).isEqualTo(expected.id());
        assertThat(actual.createdAt()).isEqualTo(expected.createdAt());
        assertThat(actual.lastModifiedAt()).isEqualTo(expected.lastModifiedAt());
        assertThat(actual.text()).isEqualTo(expected.text());
        assertThat(actual.username()).isEqualTo(expected.username());

        verify(readNewsClientPort, times(1)).readNews(newsId);
        verify(readCommentPersistencePort, times(1)).readComment(commentId, newsId);
        verify(commentMapper, times(1)).toComment(commentPersistencePortResult);
        verify(commentMapper, times(1)).updateWithNull(updateCommentUseCaseCommand, originalComment);
        verify(commentMapper, times(1)).toUpdateCommentPersistencePortCommand(updatingComment);
        verify(updateCommentPersistencePort, times(1))
                .updateComment(updateCommentPersistencePortCommand);
        verify(commentMapper, times(1)).toComment(updateCommentPersistencePortResult);
        verify(commentMapper, times(1)).toUpdateCommentUseCaseResult(updatedComment);
    }

    @Test
    void shouldThrowNewsBadRequestException_whenNewsIdIsNull() {
        // given
        UUID newsId = null;
        UUID commentId = UUID.randomUUID();
        UpdateCommentUseCaseCommand updateCommentUseCaseCommand = TestData.createUpdateCommentUseCaseCommand();

        // when, then
        NewsBadRequestException expected = assertThrows(
                NewsBadRequestException.class,
                () -> updateCommentService.updateComment(newsId, commentId, updateCommentUseCaseCommand)
        );

        assertThat(expected)
                .hasMessageContaining(NEWS_ID_MUST_NOT_BE_NULL);

        verifyNoInteractions(readNewsClientPort);
        verifyNoInteractions(readCommentPersistencePort);
        verifyNoInteractions(commentMapper);
        verifyNoInteractions(updateCommentPersistencePort);
    }

    @Test
    void shouldThrowCommentBadRequestException_whenCommentIdIsNull() {
        // given
        UUID newsId = UUID.randomUUID();
        UUID commentId = null;
        UpdateCommentUseCaseCommand updateCommentUseCaseCommand = TestData.createUpdateCommentUseCaseCommand();

        ReadNewsClientPortResult readNewsClientPortResult = TestData.createReadNewsClientPortResult(newsId);

        when(readNewsClientPort.readNews(newsId))
                .thenReturn(readNewsClientPortResult);

        // when, then
        CommentBadRequestException expected = assertThrows(
                CommentBadRequestException.class,
                () -> updateCommentService.updateComment(newsId, commentId, updateCommentUseCaseCommand)
        );

        assertThat(expected)
                .hasMessageContaining(COMMENT_ID_MUST_NOT_BE_NULL);

        verifyNoMoreInteractions(readNewsClientPort);
        verifyNoInteractions(readCommentPersistencePort);
        verifyNoInteractions(commentMapper);
        verifyNoInteractions(updateCommentPersistencePort);
    }

}