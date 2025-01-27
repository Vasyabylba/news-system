package ru.clevertec.newssystem.news.adapter.output.persistence.jpa.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Component;
import ru.clevertec.newssystem.news.adapter.output.persistence.jpa.PersistenceJpaNewsMapper;
import ru.clevertec.newssystem.news.adapter.output.persistence.jpa.entity.NewsEntity;
import ru.clevertec.newssystem.news.adapter.output.persistence.jpa.repository.NewsRepository;
import ru.clevertec.newssystem.news.port.output.persistence.UpdateNewsPersistencePort;
import ru.clevertec.newssystem.news.port.output.persistence.command.UpdateNewsPersistencePortCommand;
import ru.clevertec.newssystem.news.port.output.persistence.result.NewsPersistencePortResult;

@Component
@RequiredArgsConstructor
public class UpdateNewsJpaAdapter implements UpdateNewsPersistencePort {

    private final PersistenceJpaNewsMapper newsMapper;

    private final NewsRepository newsRepository;

    @CachePut(cacheManager = "redisCacheManager", value = "news", key = "#result.id.toString()")
    @Override
    public NewsPersistencePortResult updateNews(UpdateNewsPersistencePortCommand command) {
        NewsEntity news = newsMapper.toNewsEntity(command);

        NewsEntity updatedNews = newsRepository.save(news);

        return newsMapper.toNewsPersistencePortResult(updatedNews);
    }

}
