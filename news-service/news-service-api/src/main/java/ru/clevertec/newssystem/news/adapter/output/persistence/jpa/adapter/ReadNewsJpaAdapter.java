package ru.clevertec.newssystem.news.adapter.output.persistence.jpa.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Window;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.clevertec.exceptionhandler.exception.ResourceNotFoundException;
import ru.clevertec.newssystem.news.adapter.output.persistence.jpa.PersistenceJpaNewsMapper;
import ru.clevertec.newssystem.news.adapter.output.persistence.jpa.entity.NewsEntity;
import ru.clevertec.newssystem.news.adapter.output.persistence.jpa.repository.NewsRepository;
import ru.clevertec.newssystem.news.adapter.output.persistence.jpa.specification.NewsSpecification;
import ru.clevertec.newssystem.news.filter.NewsFilter;
import ru.clevertec.newssystem.news.port.output.persistence.ReadNewsPersistencePort;
import ru.clevertec.newssystem.news.port.output.persistence.result.NewsPersistencePortResult;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ReadNewsJpaAdapter implements ReadNewsPersistencePort {

    public static final String NEWS_WITH_ID_NOT_FOUND = "News with id '%s' not found";

    private static final String MANDATORY_SORT_NEWS_FIELD = "createdAt";

    private final NewsRepository newsRepository;

    private final PersistenceJpaNewsMapper newsMapper;

    @Override
    public Window<NewsPersistencePortResult> readAllNews(NewsFilter newsFilter, Pageable pageable) {
        Specification<NewsEntity> newsSpecification = toSpecification(newsFilter);
        Window<NewsEntity> newsWindow = newsRepository.findBy(
                newsSpecification, fluentQuery -> fluentQuery
                        .sortBy(pageable.getSort().and(Sort.by(Sort.Order.desc(MANDATORY_SORT_NEWS_FIELD))))
                        .limit(pageable.getPageSize())
                        .scroll(pageable.toScrollPosition())
        );

        return newsWindow.map(newsMapper::toNewsPersistencePortResult);
    }

    @Cacheable(cacheManager = "redisCacheManager", value = "news", key = "#newsId.toString()")
    @Override
    public NewsPersistencePortResult readNewsById(UUID newsId) {
        NewsEntity newsFromDB = newsRepository.findById(newsId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(NEWS_WITH_ID_NOT_FOUND, newsId)));

        return newsMapper.toNewsPersistencePortResult(newsFromDB);
    }

    private Specification<NewsEntity> toSpecification(NewsFilter newsFilter) {
        return Specification.where(NewsSpecification.createdAtGteSpec(newsFilter.createdAtGte()))
                .and(NewsSpecification.createdAtLteSpec(newsFilter.createdAtLte()))
                .and(NewsSpecification.titleContainsSpec(newsFilter.titleContains()))
                .and(NewsSpecification.textContainsSpec(newsFilter.textContains()));
    }

}
