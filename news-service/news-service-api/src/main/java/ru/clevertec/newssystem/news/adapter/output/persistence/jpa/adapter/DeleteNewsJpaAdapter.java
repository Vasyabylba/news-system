package ru.clevertec.newssystem.news.adapter.output.persistence.jpa.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;
import ru.clevertec.newssystem.news.adapter.output.persistence.jpa.repository.NewsRepository;
import ru.clevertec.newssystem.news.port.output.persistence.DeleteNewsPersistencePort;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DeleteNewsJpaAdapter implements DeleteNewsPersistencePort {

    private final NewsRepository newsRepository;

    @CacheEvict(cacheManager = "redisCacheManager",value = "news", key = "#newsId.toString()")
    @Override
    public void deleteNewsById(UUID newsId) {
        newsRepository.deleteById(newsId);
    }

}
