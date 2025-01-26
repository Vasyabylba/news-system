package ru.clevertec.newssystem.news.port.output.persistence;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Window;
import ru.clevertec.newssystem.news.filter.NewsFilter;
import ru.clevertec.newssystem.news.port.output.persistence.result.NewsPersistencePortResult;

import java.util.UUID;


public interface ReadNewsPersistencePort {

    Window<NewsPersistencePortResult> readAllNews(NewsFilter newsFilter, Pageable pageable);

    NewsPersistencePortResult readNewsById(UUID newsId);

}
