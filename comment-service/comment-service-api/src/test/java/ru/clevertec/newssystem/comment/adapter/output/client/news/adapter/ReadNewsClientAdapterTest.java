package ru.clevertec.newssystem.comment.adapter.output.client.news.adapter;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import ru.clevertec.newssystem.comment.adapter.output.client.news.ClientNewsMapper;
import ru.clevertec.newssystem.comment.adapter.output.client.news.NewsClient;
import ru.clevertec.newssystem.comment.adapter.output.client.news.dto.NewsResponse;
import ru.clevertec.newssystem.comment.exception.NewsNotFoundException;
import ru.clevertec.newssystem.comment.port.output.client.result.ReadNewsClientPortResult;
import ru.clevertec.newssystem.comment.util.TestData;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReadNewsClientAdapterTest {

    public static final String NEWS_WITH_ID_NOT_FOUND = "News with id '%s' not found";

    @Mock
    private NewsClient newsClient;

    @Mock
    private ClientNewsMapper newsMapper;

    @InjectMocks
    private ReadNewsClientAdapter readNewsClientAdapter;

    @Test
    void shouldReadNews_whenNewsIsExists() {
        //given
        UUID newsId = UUID.randomUUID();
        NewsResponse newsResponse = TestData.createNewsResponse(newsId);
        ReadNewsClientPortResult expected = TestData.toReadNewsClientPortResult(newsResponse);

        when(newsClient.getNews(newsId))
                .thenReturn(ResponseEntity.ok(newsResponse));
        when(newsMapper.toReadNewsClientPortResult(newsResponse))
                .thenReturn(expected);

        //when
        ReadNewsClientPortResult actual = readNewsClientAdapter.readNews(newsId);

        //then
        assertThat(actual)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", expected.id())
                .hasFieldOrPropertyWithValue("createdAt", expected.createdAt())
                .hasFieldOrPropertyWithValue("lastModifiedAt", expected.lastModifiedAt())
                .hasFieldOrPropertyWithValue("title", expected.title())
                .hasFieldOrPropertyWithValue("text", expected.text());

        verify(newsClient, times(1))
                .getNews(newsId);
        verify(newsMapper, times(1))
                .toReadNewsClientPortResult(newsResponse);
    }

    @Test
    void shouldThrowNewsNotFoundException_whenNewsNotFound() {
        //given
        UUID newsId = UUID.randomUUID();

        when(newsClient.getNews(newsId))
                .thenThrow(NewsNotFoundException.byNewsId(newsId));

        // when, then
        NewsNotFoundException expected = assertThrows(
                NewsNotFoundException.class,
                () -> readNewsClientAdapter.readNews(newsId)
        );

        Assertions.assertThat(expected)
                .hasMessageContaining(String.format(NEWS_WITH_ID_NOT_FOUND, newsId));

        verify(newsClient, times(1)).getNews(newsId);
        verifyNoInteractions(newsMapper);
    }

    @Test
    void shouldThrowNewsNotFoundException_whenNewsResponseIsNull() {
        //given
        UUID newsId = UUID.randomUUID();
        NewsResponse newsResponse = null;

        when(newsClient.getNews(newsId))
                .thenReturn(ResponseEntity.ok(null));

        // when, then
        NewsNotFoundException expected = assertThrows(
                NewsNotFoundException.class,
                () -> readNewsClientAdapter.readNews(newsId)
        );

        Assertions.assertThat(expected)
                .hasMessageContaining(String.format(NEWS_WITH_ID_NOT_FOUND, newsId));

        verify(newsClient, times(1))
                .getNews(newsId);
        verifyNoInteractions(newsMapper);
    }

}