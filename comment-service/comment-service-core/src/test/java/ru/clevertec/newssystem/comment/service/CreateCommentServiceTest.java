package ru.clevertec.newssystem.comment.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.newssystem.comment.domain.Comment;
import ru.clevertec.newssystem.comment.exception.NewsBadRequestException;
import ru.clevertec.newssystem.comment.port.CommentDomainMapper;
import ru.clevertec.newssystem.comment.port.input.comment.command.CreateCommentUseCaseCommand;
import ru.clevertec.newssystem.comment.port.input.comment.result.CreateCommentUseCaseResult;
import ru.clevertec.newssystem.comment.port.output.client.ReadNewsClientPort;
import ru.clevertec.newssystem.comment.port.output.client.result.ReadNewsClientPortResult;
import ru.clevertec.newssystem.comment.port.output.persistence.CreateCommentPersistencePort;
import ru.clevertec.newssystem.comment.port.output.persistence.command.CreateCommentPersistencePortCommand;
import ru.clevertec.newssystem.comment.port.output.persistence.result.CommentPersistencePortResult;
import ru.clevertec.newssystem.comment.util.TestData;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateCommentServiceTest {

    public static final String NEWS_ID_MUST_NOT_BE_NULL = "News id must not be null";

    @Mock
    private CommentDomainMapper commentMapper;

    @Mock
    private CreateCommentPersistencePort createCommentPersistencePort;

    @Mock
    private ReadNewsClientPort readNewsClientPort;

    @InjectMocks
    private CreateCommentService createCommentService;

    @Test
    void shouldCreateComment() {
        // given
        UUID newsId = UUID.randomUUID();
        CreateCommentUseCaseCommand createCommentUseCaseCommand = TestData.createCreateCommentUseCaseCommand();

        ReadNewsClientPortResult readNewsClientPortResult = TestData.createReadNewsClientPortResult(newsId);
        Comment creatingComment = TestData.createCreatingComment(newsId, createCommentUseCaseCommand);
        CreateCommentPersistencePortCommand createCommentPersistencePortCommand =
                TestData.createCreateCommentPersistencePortCommand(creatingComment);
        CommentPersistencePortResult commentPersistencePortResult =
                TestData.createCreateCommentPersistencePortResult(createCommentPersistencePortCommand);
        Comment savedComment = TestData.createComment(commentPersistencePortResult);
        CreateCommentUseCaseResult expected = TestData.createCreateCommentUseCaseResult(savedComment);

        when(readNewsClientPort.readNews(newsId))
                .thenReturn(readNewsClientPortResult);
        when(commentMapper.toComment(newsId, createCommentUseCaseCommand))
                .thenReturn(creatingComment);
        when(commentMapper.toCreateCommentPersistencePortCommand(creatingComment))
                .thenReturn(createCommentPersistencePortCommand);
        when(createCommentPersistencePort.createComment(createCommentPersistencePortCommand))
                .thenReturn(commentPersistencePortResult);
        when(commentMapper.toComment(commentPersistencePortResult))
                .thenReturn(savedComment);
        when(commentMapper.toCreateCommentUseCaseResult(savedComment))
                .thenReturn(expected);

        // when
        CreateCommentUseCaseResult actual = createCommentService.createComment(newsId, createCommentUseCaseCommand);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.id()).isEqualTo(expected.id());
        assertThat(actual.createdAt()).isEqualTo(expected.createdAt());
        assertThat(actual.lastModifiedAt()).isEqualTo(expected.lastModifiedAt());
        assertThat(actual.text()).isEqualTo(expected.text());
        assertThat(actual.username()).isEqualTo(expected.username());

        verify(readNewsClientPort, times(1))
                .readNews(newsId);
        verify(commentMapper, times(1))
                .toComment(newsId, createCommentUseCaseCommand);
        verify(commentMapper, times(1))
                .toCreateCommentPersistencePortCommand(creatingComment);
        verify(createCommentPersistencePort, times(1))
                .createComment(createCommentPersistencePortCommand);
        verify(commentMapper, times(1))
                .toComment(commentPersistencePortResult);
        verify(commentMapper, times(1))
                .toCreateCommentUseCaseResult(savedComment);
    }

    @Test
    void shouldThrowNewsBadRequestException_whenNewsIdIsNull() {
        // given
        UUID newsId = null;
        CreateCommentUseCaseCommand createCommentUseCaseCommand = TestData.createCreateCommentUseCaseCommand();

        // when, then
        NewsBadRequestException expected = assertThrows(
                NewsBadRequestException.class,
                () -> createCommentService.createComment(newsId, createCommentUseCaseCommand)
        );

        assertThat(expected)
                .hasMessageContaining(NEWS_ID_MUST_NOT_BE_NULL);

        verifyNoInteractions(readNewsClientPort);
        verifyNoInteractions(commentMapper);
        verifyNoInteractions(createCommentPersistencePort);
    }

}