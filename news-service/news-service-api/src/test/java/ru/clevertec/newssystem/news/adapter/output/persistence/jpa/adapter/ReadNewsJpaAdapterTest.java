package ru.clevertec.newssystem.news.adapter.output.persistence.jpa.adapter;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Window;
import org.springframework.data.jpa.domain.Specification;
import ru.clevertec.newssystem.news.adapter.output.persistence.jpa.PersistenceJpaNewsMapper;
import ru.clevertec.newssystem.news.adapter.output.persistence.jpa.entity.NewsEntity;
import ru.clevertec.newssystem.news.adapter.output.persistence.jpa.repository.NewsRepository;
import ru.clevertec.newssystem.news.exception.NewsNotFoundException;
import ru.clevertec.newssystem.news.filter.NewsFilter;
import ru.clevertec.newssystem.news.port.output.persistence.result.NewsPersistencePortResult;
import ru.clevertec.newssystem.news.util.TestData;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReadNewsJpaAdapterTest {

    public static final String NEWS_WITH_ID_NOT_FOUND = "News with id '%s' not found";

    @Mock
    private PersistenceJpaNewsMapper newsMapper;

    @Mock
    private NewsRepository newsRepository;

    @InjectMocks
    private ReadNewsJpaAdapter readNewsJpaAdapter;

    @Test
    void shouldReadAllNewsByNews() {
        //given
        NewsFilter newsFilter = TestData.createNewsFilter();
        int pageNumber = 0;
        int pageSize = 10;
        Pageable pageable = TestData.createUnsortedPageable(pageNumber, pageSize);

        Window<NewsEntity> newsEntityWindow = TestData.createWindowOfNewsEntity(pageable);

        Window<NewsPersistencePortResult> expected
                = TestData.createNewsPersistencePortResultWindow(newsEntityWindow.getContent(), pageable);

        when(newsRepository.findBy(any(Specification.class), any()))
                .thenReturn(newsEntityWindow);
        when(newsMapper.toNewsPersistencePortResult(any(NewsEntity.class)))
                .thenAnswer(invocation -> {
                    NewsEntity input = invocation.getArgument(0);
                    return NewsPersistencePortResult.builder()
                            .id(input.getId())
                            .createdAt(input.getCreatedAt())
                            .lastModifiedAt(input.getLastModifiedAt())
                            .title(input.getTitle())
                            .text(input.getText())
                            .build();
                });

        //when
        Window<NewsPersistencePortResult> actual = readNewsJpaAdapter.readAllNews(newsFilter, pageable);

        //then
        assertThat(actual).isNotNull();
        assertThat(actual.getContent())
                .hasSize(expected.getContent().size())
                .containsExactlyElementsOf(expected.getContent());
        assertThat(actual.positionAt(expected.getContent().get(pageSize - 1)))
                .isEqualTo(expected.positionAt(pageSize - 1));

        verify(newsRepository, times(1)).findBy(any(Specification.class), any());
        verify(newsMapper, times(pageSize)).toNewsPersistencePortResult(any(NewsEntity.class));
    }

    @Nested
    class ReadNews {

        @Test
        void shouldReadNews() {
            //given
            UUID newsId = UUID.randomUUID();

            NewsEntity news = TestData.toNewsEntity(newsId);

            NewsPersistencePortResult expected
                    = TestData.createNewsPersistencePortResult(news);

            when(newsRepository.findById(newsId))
                    .thenReturn(Optional.of(news));
            when(newsMapper.toNewsPersistencePortResult(news))
                    .thenReturn(expected);

            //when
            NewsPersistencePortResult actual = readNewsJpaAdapter.readNewsById(newsId);

            //then
            assertThat(actual).isNotNull();
            assertThat(actual.id()).isEqualTo(expected.id());
            assertThat(actual.createdAt()).isEqualTo(expected.createdAt());
            assertThat(actual.lastModifiedAt()).isEqualTo(expected.lastModifiedAt());
            assertThat(actual.title()).isEqualTo(expected.title());
            assertThat(actual.text()).isEqualTo(expected.text());

            verify(newsRepository, times(1)).findById(newsId);
            verify(newsMapper, times(1)).toNewsPersistencePortResult(news);
        }

        @Test
        void shouldThrowNewsNotFoundException_whenNewsNotExists() {
            //given
            UUID newsId = UUID.randomUUID();

            when(newsRepository.findById(newsId))
                    .thenThrow(NewsNotFoundException.byNewsId(newsId));

            //when & then
            NewsNotFoundException actual = assertThrows(
                    NewsNotFoundException.class,
                    () -> readNewsJpaAdapter.readNewsById(newsId)
            );

            assertThat(actual)
                    .hasMessageContaining(String.format(NEWS_WITH_ID_NOT_FOUND, newsId));

            verify(newsRepository, times(1)).findById(newsId);
            verifyNoInteractions(newsMapper);
        }

    }

}