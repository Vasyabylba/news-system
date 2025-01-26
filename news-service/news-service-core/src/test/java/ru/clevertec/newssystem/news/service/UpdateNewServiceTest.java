package ru.clevertec.newssystem.news.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.newssystem.news.domain.News;
import ru.clevertec.newssystem.news.exception.NewsBadRequestException;
import ru.clevertec.newssystem.news.port.NewsDomainMapper;
import ru.clevertec.newssystem.news.port.input.news.command.UpdateNewsUseCaseCommand;
import ru.clevertec.newssystem.news.port.input.news.result.UpdateNewsUseCaseResult;
import ru.clevertec.newssystem.news.port.output.persistence.ReadNewsPersistencePort;
import ru.clevertec.newssystem.news.port.output.persistence.UpdateNewsPersistencePort;
import ru.clevertec.newssystem.news.port.output.persistence.command.UpdateNewsPersistencePortCommand;
import ru.clevertec.newssystem.news.port.output.persistence.result.NewsPersistencePortResult;
import ru.clevertec.newssystem.news.util.TestData;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateNewServiceTest {

    public static final String NEWS_ID_MUST_NOT_BE_NULL = "News id must not be null";

    @Mock
    private NewsDomainMapper newsMapper;

    @Mock
    private UpdateNewsPersistencePort updateNewsPersistencePort;

    @Mock
    private ReadNewsPersistencePort readNewsPersistencePort;

    @InjectMocks
    private UpdateNewService updateNewsService;

    @Test
    void shouldUpdateNews() {
        // given
        UUID newsId = UUID.randomUUID();
        UpdateNewsUseCaseCommand updateNewsUseCaseCommand = TestData.createUpdateNewsUseCaseCommand();

        NewsPersistencePortResult readNewsPersistencePortResult =
                TestData.createNewsPersistencePortResult(newsId);
        News originalNews = TestData.createNews(readNewsPersistencePortResult);

        News updatingNews = TestData.toUpdatingNews(updateNewsUseCaseCommand, originalNews);
        UpdateNewsPersistencePortCommand updateNewsPersistencePortCommand =
                TestData.createUpdateNewsPersistencePortCommand(updatingNews);
        NewsPersistencePortResult updateNewsPersistencePortResult =
                TestData.createNewsPersistencePortResult(updateNewsPersistencePortCommand);
        News updatedNews = TestData.createUpdatedNews(updateNewsPersistencePortResult);
        UpdateNewsUseCaseResult expected = TestData.createUpdateNewsUseCaseResult(updatedNews);

        when(readNewsPersistencePort.readNewsById(newsId))
                .thenReturn(readNewsPersistencePortResult);
        when(newsMapper.toNews(readNewsPersistencePortResult))
                .thenReturn(originalNews);
        when(newsMapper.updateWithNull(updateNewsUseCaseCommand, originalNews))
                .thenReturn(updatingNews);
        when(newsMapper.toUpdateNewsPersistencePortCommand(updatingNews))
                .thenReturn(updateNewsPersistencePortCommand);
        when(updateNewsPersistencePort.updateNews(updateNewsPersistencePortCommand))
                .thenReturn(updateNewsPersistencePortResult);
        when(newsMapper.toNews(updateNewsPersistencePortResult))
                .thenReturn(updatedNews);
        when(newsMapper.toUpdateNewsUseCaseResult(updatedNews))
                .thenReturn(expected);

        // when
        UpdateNewsUseCaseResult actual =
                updateNewsService.updateNews(newsId, updateNewsUseCaseCommand);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.id()).isEqualTo(expected.id());
        assertThat(actual.createdAt()).isEqualTo(expected.createdAt());
        assertThat(actual.lastModifiedAt()).isEqualTo(expected.lastModifiedAt());
        assertThat(actual.title()).isEqualTo(expected.title());
        assertThat(actual.text()).isEqualTo(expected.text());

        verify(readNewsPersistencePort, times(1)).readNewsById(newsId);
        verify(newsMapper, times(1)).toNews(readNewsPersistencePortResult);
        verify(newsMapper, times(1)).updateWithNull(updateNewsUseCaseCommand, originalNews);
        verify(newsMapper, times(1)).toUpdateNewsPersistencePortCommand(updatingNews);
        verify(updateNewsPersistencePort, times(1))
                .updateNews(updateNewsPersistencePortCommand);
        verify(newsMapper, times(1)).toNews(updateNewsPersistencePortResult);
        verify(newsMapper, times(1)).toUpdateNewsUseCaseResult(updatedNews);
    }

    @Test
    void shouldThrowNewsBadRequestException_whenNewsIdIsNull() {
        // given
        UUID newsId = null;
        UpdateNewsUseCaseCommand updateNewsUseCaseCommand = TestData.createUpdateNewsUseCaseCommand();

        // when, then
        NewsBadRequestException expected = assertThrows(
                NewsBadRequestException.class,
                () -> updateNewsService.updateNews(newsId, updateNewsUseCaseCommand)
        );

        assertThat(expected)
                .hasMessageContaining(NEWS_ID_MUST_NOT_BE_NULL);

        verifyNoInteractions(readNewsPersistencePort);
        verifyNoInteractions(newsMapper);
        verifyNoInteractions(updateNewsPersistencePort);
    }

}