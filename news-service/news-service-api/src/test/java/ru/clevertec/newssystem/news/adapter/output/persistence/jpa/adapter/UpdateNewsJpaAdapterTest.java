package ru.clevertec.newssystem.news.adapter.output.persistence.jpa.adapter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.newssystem.news.adapter.output.persistence.jpa.PersistenceJpaNewsMapper;
import ru.clevertec.newssystem.news.adapter.output.persistence.jpa.entity.NewsEntity;
import ru.clevertec.newssystem.news.adapter.output.persistence.jpa.repository.NewsRepository;
import ru.clevertec.newssystem.news.port.output.persistence.command.UpdateNewsPersistencePortCommand;
import ru.clevertec.newssystem.news.port.output.persistence.result.NewsPersistencePortResult;
import ru.clevertec.newssystem.news.util.TestData;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateNewsJpaAdapterTest {

    @Mock
    private PersistenceJpaNewsMapper newsMapper;

    @Mock
    private NewsRepository newsRepository;

    @InjectMocks
    private UpdateNewsJpaAdapter updateNewsJpaAdapter;

    @Test
    void shouldUpdateNews() {
        //given
        UpdateNewsPersistencePortCommand updateNewsPersistencePortCommand =
                TestData.createUpdateNewsPersistencePortCommand();
        NewsEntity news = TestData.toNewsEntity(updateNewsPersistencePortCommand);
        NewsEntity updatedNews = TestData.createUpdatedNewsEntity(news);
        NewsPersistencePortResult expected
                = TestData.toNewsPersistencePortResult(updatedNews);

        when(newsMapper.toNewsEntity(updateNewsPersistencePortCommand))
                .thenReturn(news);
        when(newsRepository.save(news))
                .thenReturn(updatedNews);
        when(newsMapper.toNewsPersistencePortResult(updatedNews))
                .thenReturn(expected);

        //when
        NewsPersistencePortResult actual =
                updateNewsJpaAdapter.updateNews(updateNewsPersistencePortCommand);

        //then
        assertThat(actual).isNotNull();
        assertThat(actual.id()).isEqualTo(expected.id());
        assertThat(actual.createdAt()).isEqualTo(expected.createdAt());
        assertThat(actual.lastModifiedAt()).isEqualTo(expected.lastModifiedAt());
        assertThat(actual.title()).isEqualTo(expected.title());
        assertThat(actual.text()).isEqualTo(expected.text());

        verify(newsMapper, times(1))
                .toNewsEntity(updateNewsPersistencePortCommand);
        verify(newsRepository, times(1))
                .save(news);
        verify(newsMapper, times(1))
                .toNewsPersistencePortResult(updatedNews);
    }

}