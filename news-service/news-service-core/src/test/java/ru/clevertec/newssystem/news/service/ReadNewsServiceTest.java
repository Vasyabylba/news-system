package ru.clevertec.newssystem.news.service;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Window;
import ru.clevertec.newssystem.news.domain.Comment;
import ru.clevertec.newssystem.news.domain.News;
import ru.clevertec.newssystem.news.exception.NewsBadRequestException;
import ru.clevertec.newssystem.news.filter.NewsFilter;
import ru.clevertec.newssystem.news.port.CommentDomainMapper;
import ru.clevertec.newssystem.news.port.NewsDomainMapper;
import ru.clevertec.newssystem.news.port.input.news.result.ReadNewsUseCaseResult;
import ru.clevertec.newssystem.news.port.output.client.ReadCommentClientPort;
import ru.clevertec.newssystem.news.port.output.client.result.ReadCommentClientPortResult;
import ru.clevertec.newssystem.news.port.output.persistence.ReadNewsPersistencePort;
import ru.clevertec.newssystem.news.port.output.persistence.result.NewsPersistencePortResult;
import ru.clevertec.newssystem.news.util.TestData;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReadNewsServiceTest {

    public static final String NEWS_ID_MUST_NOT_BE_NULL = "News id must not be null";

    @Mock
    private NewsDomainMapper newsMapper;

    @Mock
    private CommentDomainMapper commentMapper;

    @Mock
    private ReadNewsPersistencePort readNewsPersistencePort;

    @Mock
    private ReadCommentClientPort readCommentClientPort;

    @InjectMocks
    private ReadNewsService readNewsService;


    @Test
    void shouldReadAllNews() {
        // given
        NewsFilter newsFilter = TestData.createNewsFilter();
        int pageNumber = 0;
        int pageSize = 10;
        Pageable pageable = TestData.createUnsortedPageable(pageNumber, pageSize);

        Window<NewsPersistencePortResult> windowNewsPersistencePortResult =
                TestData.createWindowNewsPersistencePortResult(pageable);

        Window<ReadNewsUseCaseResult> expected = TestData.createWindowReadNewsUseCaseResult(
                windowNewsPersistencePortResult.getContent(),
                pageable
        );

        when(readNewsPersistencePort.readAllNews(newsFilter, pageable))
                .thenReturn(windowNewsPersistencePortResult);
        when(newsMapper.toNews(any(NewsPersistencePortResult.class)))
                .thenAnswer(invocation -> {
                    NewsPersistencePortResult input = invocation.getArgument(0);
                    return News.builder()
                            .id(input.id())
                            .createdAt(input.createdAt())
                            .lastModifiedAt(input.lastModifiedAt())
                            .title(input.title())
                            .text(input.text())
                            .build();
                });
        when(newsMapper.toReadNewsUseCaseResult(any(News.class)))
                .thenAnswer(invocation -> {
                    News input = invocation.getArgument(0);
                    return ReadNewsUseCaseResult.builder()
                            .id(input.getId())
                            .createdAt(input.getCreatedAt())
                            .lastModifiedAt(input.getLastModifiedAt())
                            .title(input.getTitle())
                            .text(input.getText())
                            .build();
                });

        // when
        Window<ReadNewsUseCaseResult> actual = readNewsService.readAllNews(newsFilter, pageable);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.getContent())
                .hasSize(expected.getContent().size())
                .containsExactlyElementsOf(expected.getContent());
        assertThat(actual.positionAt(expected.getContent().get(pageSize - 1)))
                .isEqualTo(expected.positionAt(pageSize - 1));

        verify(readNewsPersistencePort, times(1)).readAllNews(newsFilter, pageable);
        verify(newsMapper, times(pageSize)).toNews(any(NewsPersistencePortResult.class));
        verify(newsMapper, times(pageSize)).toReadNewsUseCaseResult(any(News.class));
    }


    @Nested
    class ReadNews {

        @Test
        void shouldReadNews() {
            // given
            UUID newsId = UUID.randomUUID();

            NewsPersistencePortResult newsPersistencePortResult =
                    TestData.createNewsPersistencePortResult(newsId);
            News news = TestData.createNews(newsPersistencePortResult);

            ReadNewsUseCaseResult expected = TestData.createReadNewsUseCaseResult(news);

            when(readNewsPersistencePort.readNewsById(newsId))
                    .thenReturn(newsPersistencePortResult);
            when(newsMapper.toNews(newsPersistencePortResult))
                    .thenReturn(news);
            when(newsMapper.toReadNewsUseCaseResult(news))
                    .thenReturn(expected);

            // when
            ReadNewsUseCaseResult actual = readNewsService.readNews(newsId);

            // then
            assertThat(actual).isNotNull();
            assertThat(actual.id()).isEqualTo(expected.id());
            assertThat(actual.createdAt()).isEqualTo(expected.createdAt());
            assertThat(actual.lastModifiedAt()).isEqualTo(expected.lastModifiedAt());
            assertThat(actual.title()).isEqualTo(expected.title());
            assertThat(actual.text()).isEqualTo(expected.text());

            verify(readNewsPersistencePort, times(1))
                    .readNewsById(newsId);
            verify(newsMapper, times(1))
                    .toNews(newsPersistencePortResult);
            verify(newsMapper, times(1))
                    .toReadNewsUseCaseResult(news);
        }


        @Test
        void shouldThrowNewsBadRequestException_whenNewsIdIsNull() {
            // given
            UUID newsId = null;

            // when, then
            NewsBadRequestException expected = assertThrows(
                    NewsBadRequestException.class,
                    () -> readNewsService.readNews(newsId)
            );

            assertThat(expected)
                    .hasMessageContaining(NEWS_ID_MUST_NOT_BE_NULL);

            verifyNoInteractions(readNewsPersistencePort);
            verifyNoInteractions(newsMapper);
        }

    }

    @Nested
    class ReadNewsWithComments {

        @Test
        void shouldReadNewsWithComments() {
            // given
            UUID newsId = UUID.randomUUID();
            int pageNumber = 0;
            int pageSize = 10;
            Pageable pageable = TestData.createUnsortedPageable(pageNumber, pageSize);

            NewsPersistencePortResult newsPersistencePortResult =
                    TestData.createNewsPersistencePortResult(newsId);

            News news = TestData.createNews(newsPersistencePortResult);

            Window<ReadCommentClientPortResult> readCommentClientPortResult =
                    TestData.createWindowReadCommentClientPortResult(pageable);

            Window<Comment> comments = TestData.toWindowComments(readCommentClientPortResult);

            ReadNewsUseCaseResult expected = TestData.toReadNewsUseCaseResult(news, comments);

            when(readNewsPersistencePort.readNewsById(newsId))
                    .thenReturn(newsPersistencePortResult);
            when(newsMapper.toNews(newsPersistencePortResult))
                    .thenReturn(news);
            when(readCommentClientPort.readCommentsByNews(newsId, pageable))
                    .thenReturn(readCommentClientPortResult);
            when(commentMapper.toComment(any(ReadCommentClientPortResult.class)))
                    .thenAnswer(invocation -> {
                        ReadCommentClientPortResult input = invocation.getArgument(0);
                        return Comment.builder()
                                .id(input.id())
                                .createdAt(input.createdAt())
                                .lastModifiedAt(input.lastModifiedAt())
                                .text(input.text())
                                .username(input.username())
                                .build();
                    });
            when(newsMapper.toReadNewsUseCaseResult(news, comments))
                    .thenReturn(expected);

            // when
            ReadNewsUseCaseResult actual = readNewsService.readNewsWithComments(newsId, pageable);

            // then
            assertThat(actual).isNotNull();
            assertThat(actual.id()).isEqualTo(expected.id());
            assertThat(actual.createdAt()).isEqualTo(expected.createdAt());
            assertThat(actual.lastModifiedAt()).isEqualTo(expected.lastModifiedAt());
            assertThat(actual.title()).isEqualTo(expected.title());
            assertThat(actual.text()).isEqualTo(expected.text());
        }

        @Test
        void shouldThrowNewsBadRequestException_whenNewsIdIsNull() {
            // given
            UUID newsId = null;
            int pageNumber = 0;
            int pageSize = 10;
            Pageable pageable = TestData.createUnsortedPageable(pageNumber, pageSize);

            // when, then
            NewsBadRequestException expected = assertThrows(
                    NewsBadRequestException.class,
                    () -> readNewsService.readNewsWithComments(newsId, pageable)
            );

            assertThat(expected)
                    .hasMessageContaining(NEWS_ID_MUST_NOT_BE_NULL);

            verifyNoInteractions(readNewsPersistencePort);
            verifyNoInteractions(readCommentClientPort);
            verifyNoInteractions(newsMapper);
        }

    }

}