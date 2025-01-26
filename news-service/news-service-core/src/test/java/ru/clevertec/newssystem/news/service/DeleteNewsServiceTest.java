package ru.clevertec.newssystem.news.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.newssystem.news.exception.NewsBadRequestException;
import ru.clevertec.newssystem.news.port.output.persistence.DeleteNewsPersistencePort;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class DeleteNewsServiceTest {

    public static final String NEWS_ID_MUST_NOT_BE_NULL = "News id must not be null";

    @Mock
    private DeleteNewsPersistencePort deleteNewsPersistencePort;

    @InjectMocks
    private DeleteNewsService deleteNewsService;

    @Test
    void shouldDeleteNews() {
        // given
        UUID newsId = UUID.randomUUID();

        doNothing()
                .when(deleteNewsPersistencePort).deleteNewsById(newsId);

        // when
        deleteNewsService.deleteNews(newsId);

        // then
        verify(deleteNewsPersistencePort).deleteNewsById(newsId);
    }

    @Test
    void shouldThrowNewsBadRequestException_whenNewsIdIsNull() {
        // given
        UUID newsId = null;

        // when, then
        NewsBadRequestException expected = assertThrows(
                NewsBadRequestException.class,
                () -> deleteNewsService.deleteNews(newsId)
        );

        assertThat(expected)
                .hasMessageContaining(NEWS_ID_MUST_NOT_BE_NULL);

        verifyNoInteractions(deleteNewsPersistencePort);
    }

}