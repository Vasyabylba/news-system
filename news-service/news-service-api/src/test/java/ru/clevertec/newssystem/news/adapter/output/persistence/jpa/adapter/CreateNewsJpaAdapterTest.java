package ru.clevertec.newssystem.news.adapter.output.persistence.jpa.adapter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.newssystem.news.adapter.output.persistence.jpa.PersistenceJpaNewsMapper;
import ru.clevertec.newssystem.news.adapter.output.persistence.jpa.entity.NewsEntity;
import ru.clevertec.newssystem.news.adapter.output.persistence.jpa.repository.NewsRepository;
import ru.clevertec.newssystem.news.port.output.persistence.command.CreateNewsPersistencePortCommand;
import ru.clevertec.newssystem.news.port.output.persistence.result.NewsPersistencePortResult;
import ru.clevertec.newssystem.news.util.TestData;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateNewsJpaAdapterTest {

    @Mock
    private PersistenceJpaNewsMapper newsMapper;

    @Mock
    private NewsRepository newsRepository;

    @InjectMocks
    private CreateNewsJpaAdapter createNewsJpaAdapter;

    @Test
    void shouldCreateNews() {
        //given
        CreateNewsPersistencePortCommand createNewsPersistencePortCommand =
                TestData.createCreateNewsPersistencePortCommand();
        NewsEntity news = TestData.toNewsEntity(createNewsPersistencePortCommand);
        NewsEntity savedNews = TestData.createSavedNewsEntity(news);
        NewsPersistencePortResult expected
                = TestData.toCreateNewsPersistencePortResult(savedNews);

        when(newsMapper.toNewsEntity(createNewsPersistencePortCommand))
                .thenReturn(news);
        when(newsRepository.save(news))
                .thenReturn(savedNews);
        when(newsMapper.toNewsPersistencePortResult(savedNews))
                .thenReturn(expected);

        //when
        NewsPersistencePortResult actual =
                createNewsJpaAdapter.createNews(createNewsPersistencePortCommand);

        //then
        assertThat(actual).isNotNull();
        assertThat(actual.id()).isEqualTo(expected.id());
        assertThat(actual.createdAt()).isEqualTo(expected.createdAt());
        assertThat(actual.lastModifiedAt()).isEqualTo(expected.lastModifiedAt());
        assertThat(actual.title()).isEqualTo(expected.title());
        assertThat(actual.text()).isEqualTo(expected.text());

        verify(newsMapper, times(1)).toNewsEntity(createNewsPersistencePortCommand);
        verify(newsRepository, times(1)).save(news);
        verify(newsMapper, times(1)).toNewsPersistencePortResult(savedNews);
    }

}