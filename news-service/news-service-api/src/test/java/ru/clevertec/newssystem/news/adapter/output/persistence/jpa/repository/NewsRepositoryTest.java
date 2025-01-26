package ru.clevertec.newssystem.news.adapter.output.persistence.jpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Window;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.clevertec.newssystem.news.adapter.output.persistence.jpa.entity.NewsEntity;
import ru.clevertec.newssystem.news.util.TestData;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EnableJpaAuditing
class NewsRepositoryTest {

    @Autowired
    private NewsRepository newsRepository;

    @Test
    @Sql(scripts = "classpath:db/insert-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:db/delete-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldCreateNews() {
        // given
        NewsEntity news = TestData.generateNewsEntityForCreate();

        // when
        NewsEntity savedNews = newsRepository.save(news);
        newsRepository.flush();

        // then
        assertThat(savedNews).isNotNull();
        assertThat(savedNews.getId()).isNotNull();
        assertThat(savedNews.getCreatedAt()).isNotNull();
        assertThat(savedNews.getLastModifiedAt()).isNotNull();
        assertThat(savedNews.getTitle()).isEqualTo(news.getTitle());
        assertThat(savedNews.getText()).isEqualTo(news.getText());
    }

    @Test
    @Sql(scripts = "classpath:db/insert-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:db/delete-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldReadNews() {
        // given
        NewsEntity news = TestData.getNewsEntityForRead();

        // when
        NewsEntity readNews = newsRepository.findById(news.getId()).orElse(null);

        // then
        assertThat(readNews).isNotNull();
        assertThat(readNews.getId()).isNotNull();
        assertThat(readNews.getId()).isEqualByComparingTo(news.getId());
        assertThat(readNews.getCreatedAt()).isEqualTo(news.getCreatedAt());
        assertThat(readNews.getTitle()).isEqualTo(news.getTitle());
        assertThat(readNews.getText()).isEqualTo(news.getText());
    }

    @Test
    @Sql(scripts = "classpath:db/insert-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:db/delete-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldUpdateNews() {
        // given
        NewsEntity news = TestData.getNewsEntityForUpdate();

        // when
        NewsEntity updatedNews = newsRepository.save(news);

        // then
        assertThat(updatedNews).isNotNull();
        assertThat(updatedNews.getId()).isNotNull();
        assertThat(updatedNews.getId()).isEqualByComparingTo(news.getId());
        assertThat(updatedNews.getCreatedAt()).isEqualTo(news.getCreatedAt());
        assertThat(updatedNews.getLastModifiedAt()).isEqualTo(news.getLastModifiedAt());
        assertThat(updatedNews.getTitle()).isEqualTo(news.getTitle());
        assertThat(updatedNews.getText()).isEqualTo(news.getText());
    }

    @Test
    @Sql(scripts = "classpath:db/insert-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:db/delete-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldDeleteNews() {
        // given
        UUID newsId = UUID.fromString("f6eb2a7a-9a3f-49a0-b895-6019407b6a21");

        // when
        newsRepository.deleteById(newsId);
        NewsEntity deletedNews = newsRepository.findById(newsId).orElse(null);

        // then
        assertThat(deletedNews).isNull();
    }

    @Test
    @Sql(scripts = "classpath:db/insert-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:db/delete-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldReadAllNewsByNews() {
        //given
        Specification<NewsEntity> newsSpecification = TestData.createNewsSpecification();
        Pageable pageable = TestData.createUnsortedPageable(0, 3);

        //when
        Window<NewsEntity> newsWindow = newsRepository.findBy(
                newsSpecification, fluentQuery -> fluentQuery
                        .limit(pageable.getPageSize())
                        .scroll(pageable.toScrollPosition())
        );

        //then
        assertNotNull(newsWindow);
        assertThat(newsWindow).isNotNull();
        assertThat(newsWindow.getContent()).hasSize(3);
    }

}