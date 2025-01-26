package ru.clevertec.newssystem.comment.service;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Window;
import ru.clevertec.newssystem.comment.domain.Comment;
import ru.clevertec.newssystem.comment.domain.News;
import ru.clevertec.newssystem.comment.exception.CommentBadRequestException;
import ru.clevertec.newssystem.comment.exception.NewsBadRequestException;
import ru.clevertec.newssystem.comment.filter.CommentFilter;
import ru.clevertec.newssystem.comment.port.CommentDomainMapper;
import ru.clevertec.newssystem.comment.port.input.comment.result.ReadCommentUseCaseResult;
import ru.clevertec.newssystem.comment.port.output.client.ReadNewsClientPort;
import ru.clevertec.newssystem.comment.port.output.client.result.ReadNewsClientPortResult;
import ru.clevertec.newssystem.comment.port.output.persistence.ReadCommentPersistencePort;
import ru.clevertec.newssystem.comment.port.output.persistence.result.CommentPersistencePortResult;
import ru.clevertec.newssystem.comment.util.TestData;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReadCommentServiceTest {

    public static final String NEWS_ID_MUST_NOT_BE_NULL = "News id must not be null";

    public static final String COMMENT_ID_MUST_NOT_BE_NULL = "Comment id must not be null";

    @Mock
    private CommentDomainMapper commentMapper;

    @Mock
    private ReadCommentPersistencePort readCommentPersistencePort;

    @Mock
    private ReadNewsClientPort readNewsClientPort;

    @InjectMocks
    private ReadCommentService readCommentService;

    @Nested
    class ReadAllComments {

        @Test
        void shouldReadAllComments() {
            //given
            UUID newsId = UUID.randomUUID();
            CommentFilter commentFilter = TestData.createCommentFilter();
            int pageNumber = 0;
            int pageSize = 10;
            Pageable pageable = TestData.createUnsortedPageable(pageNumber, pageSize);

            ReadNewsClientPortResult readNewsClientPortResult = TestData.createReadNewsClientPortResult(newsId);

            Window<CommentPersistencePortResult> readCommentPersistencePortResults =
                    TestData.createWindowCommentPersistencePortResult(newsId, pageable);

            Window<ReadCommentUseCaseResult> expected = TestData.createWindowReadCommentUseCaseResult(
                    readCommentPersistencePortResults.getContent(),
                    pageable
            );

            when(readNewsClientPort.readNews(newsId))
                    .thenReturn(readNewsClientPortResult);
            when(readCommentPersistencePort.readAllComments(newsId, commentFilter, pageable))
                    .thenReturn(readCommentPersistencePortResults);
            when(commentMapper.toComment(any(CommentPersistencePortResult.class)))
                    .thenAnswer(invocation -> {
                        CommentPersistencePortResult input = invocation.getArgument(0);
                        return Comment.builder()
                                .id(input.id())
                                .createdAt(input.createdAt())
                                .lastModifiedAt(input.lastModifiedAt())
                                .text(input.text())
                                .username(input.username())
                                .news(News.builder().id(input.newsId()).build())
                                .build();
                    });
            when(commentMapper.toReadCommentUseCaseResult(any(Comment.class)))
                    .thenAnswer(invocation -> {
                        Comment input = invocation.getArgument(0);
                        return ReadCommentUseCaseResult.builder()
                                .id(input.getId())
                                .createdAt(input.getCreatedAt())
                                .lastModifiedAt(input.getLastModifiedAt())
                                .text(input.getText())
                                .username(input.getUsername())
                                .build();
                    });

            //when
            Window<ReadCommentUseCaseResult> actual = readCommentService.readAllComments(
                    newsId,
                    commentFilter,
                    pageable
            );

            // then
            assertThat(actual).isNotNull();
            assertThat(actual.getContent())
                    .hasSize(expected.getContent().size())
                    .containsExactlyElementsOf(expected.getContent());
            assertThat(actual.positionAt(expected.getContent().get(pageSize - 1)))
                    .isEqualTo(expected.positionAt(pageSize - 1));

            verify(readNewsClientPort, times(1))
                    .readNews(newsId);
            verify(readCommentPersistencePort, times(1))
                    .readAllComments(newsId, commentFilter, pageable);
            verify(commentMapper, times(pageSize))
                    .toComment(any(CommentPersistencePortResult.class));
            verify(commentMapper, times(pageSize))
                    .toReadCommentUseCaseResult(any(Comment.class));
        }

        @Test
        void shouldThrowNewsBadRequestException_whenNewsIdIsNull() {
            // given
            UUID newsId = null;
            CommentFilter commentFilter = TestData.createCommentFilter();
            int pageNumber = 0;
            int pageSize = 10;
            Pageable pageable = TestData.createUnsortedPageable(pageNumber, pageSize);

            // when, then
            NewsBadRequestException expected = assertThrows(
                    NewsBadRequestException.class,
                    () -> readCommentService.readAllComments(newsId, commentFilter, pageable)
            );

            AssertionsForClassTypes.assertThat(expected)
                    .hasMessageContaining(NEWS_ID_MUST_NOT_BE_NULL);

            verifyNoInteractions(readNewsClientPort);
            verifyNoInteractions(readCommentPersistencePort);
            verifyNoInteractions(commentMapper);
        }

    }

    @Nested
    class ReadComment {

        @Test
        void shouldReadComment() {
            // given
            UUID newsId = UUID.randomUUID();
            UUID commentId = UUID.randomUUID();

            ReadNewsClientPortResult readNewsClientPortResult = TestData.createReadNewsClientPortResult(newsId);
            CommentPersistencePortResult commentPersistencePortResult =
                    TestData.createCommentPersistencePortResult(newsId, commentId);
            Comment comment = TestData.createComment(commentPersistencePortResult);

            ReadCommentUseCaseResult expected = TestData.createReadCommentUseCaseResult(comment);

            when(readNewsClientPort.readNews(newsId))
                    .thenReturn(readNewsClientPortResult);
            when(readCommentPersistencePort.readComment(commentId, newsId))
                    .thenReturn(commentPersistencePortResult);
            when(commentMapper.toComment(commentPersistencePortResult))
                    .thenReturn(comment);
            when(commentMapper.toReadCommentUseCaseResult(comment))
                    .thenReturn(expected);

            // when
            ReadCommentUseCaseResult actual = readCommentService.readComment(newsId, commentId);

            // then
            assertThat(actual)
                    .isNotNull()
                    .hasFieldOrPropertyWithValue("id", expected.id())
                    .hasFieldOrPropertyWithValue("createdAt", expected.createdAt())
                    .hasFieldOrPropertyWithValue("lastModifiedAt", expected.lastModifiedAt())
                    .hasFieldOrPropertyWithValue("text", expected.text())
                    .hasFieldOrPropertyWithValue("username", expected.username());

            verify(readNewsClientPort, times(1)).readNews(newsId);
            verify(readCommentPersistencePort, times(1)).readComment(commentId, newsId);
            verify(commentMapper, times(1)).toComment(commentPersistencePortResult);
            verify(commentMapper, times(1)).toReadCommentUseCaseResult(comment);
        }


        @Test
        void shouldThrowNewsBadRequestException_whenNewsIdIsNull() {
            // given
            UUID newsId = null;
            UUID commentId = UUID.randomUUID();

            // when, then
            NewsBadRequestException expected = assertThrows(
                    NewsBadRequestException.class,
                    () -> readCommentService.readComment(newsId, commentId)
            );

            assertThat(expected)
                    .hasMessageContaining(NEWS_ID_MUST_NOT_BE_NULL);

            verifyNoInteractions(readNewsClientPort);
            verifyNoInteractions(readCommentPersistencePort);
            verifyNoInteractions(commentMapper);
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
                    () -> readCommentService.readComment(newsId, commentId)
            );

            assertThat(expected)
                    .hasMessageContaining(COMMENT_ID_MUST_NOT_BE_NULL);

            verifyNoMoreInteractions(readNewsClientPort);
            verifyNoInteractions(readCommentPersistencePort);
            verifyNoInteractions(commentMapper);
        }

    }

}