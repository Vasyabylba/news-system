package ru.clevertec.newssystem.news.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.newssystem.news.domain.News;
import ru.clevertec.newssystem.news.port.NewsDomainMapper;
import ru.clevertec.newssystem.news.port.input.news.command.CreateNewsUseCaseCommand;
import ru.clevertec.newssystem.news.port.input.news.result.CreateNewsUseCaseResult;
import ru.clevertec.newssystem.news.port.output.persistence.CreateNewsPersistencePort;
import ru.clevertec.newssystem.news.port.output.persistence.command.CreateNewsPersistencePortCommand;
import ru.clevertec.newssystem.news.port.output.persistence.result.NewsPersistencePortResult;
import ru.clevertec.newssystem.news.util.TestData;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateNewsServiceTest {

    @Mock
    private NewsDomainMapper newsMapper;

    @Mock
    private CreateNewsPersistencePort createNewsPersistencePort;

    @InjectMocks
    private CreateNewsService createNewsService;

    @Test
    void shouldCreateNews() {
        // given
        CreateNewsUseCaseCommand createNewsUseCaseCommand = TestData.createCreateNewsUseCaseCommand();

        News creatingNews = TestData.createCreatingNews(createNewsUseCaseCommand);
        CreateNewsPersistencePortCommand createNewsPersistencePortCommand =
                TestData.createCreateNewsPersistencePortCommand(creatingNews);
        NewsPersistencePortResult newsPersistencePortResult =
                TestData.createCreateNewsPersistencePortResult(createNewsPersistencePortCommand);
        News savedNews = TestData.createNews(newsPersistencePortResult);
        CreateNewsUseCaseResult expected = TestData.createCreateNewsUseCaseResult(savedNews);

        when(newsMapper.toNews(createNewsUseCaseCommand))
                .thenReturn(creatingNews);
        when(newsMapper.toCreateNewsPersistencePortCommand(creatingNews))
                .thenReturn(createNewsPersistencePortCommand);
        when(createNewsPersistencePort.createNews(createNewsPersistencePortCommand))
                .thenReturn(newsPersistencePortResult);
        when(newsMapper.toNews(newsPersistencePortResult))
                .thenReturn(savedNews);
        when(newsMapper.toCreateNewsUseCaseResult(savedNews))
                .thenReturn(expected);

        // when
        CreateNewsUseCaseResult actual = createNewsService.createNews(createNewsUseCaseCommand);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.id()).isEqualTo(expected.id());
        assertThat(actual.createdAt()).isEqualTo(expected.createdAt());
        assertThat(actual.lastModifiedAt()).isEqualTo(expected.lastModifiedAt());
        assertThat(actual.title()).isEqualTo(expected.title());
        assertThat(actual.text()).isEqualTo(expected.text());

        verify(newsMapper, times(1))
                .toNews(createNewsUseCaseCommand);
        verify(newsMapper, times(1))
                .toCreateNewsPersistencePortCommand(creatingNews);
        verify(createNewsPersistencePort, times(1))
                .createNews(createNewsPersistencePortCommand);
        verify(newsMapper, times(1))
                .toNews(newsPersistencePortResult);
        verify(newsMapper, times(1))
                .toCreateNewsUseCaseResult(savedNews);
    }

}