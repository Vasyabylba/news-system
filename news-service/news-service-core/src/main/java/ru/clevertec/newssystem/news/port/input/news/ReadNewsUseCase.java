package ru.clevertec.newssystem.news.port.input.news;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Window;
import ru.clevertec.newssystem.news.filter.NewsFilter;
import ru.clevertec.newssystem.news.port.input.news.result.ReadNewsUseCaseResult;

import java.util.UUID;

public interface ReadNewsUseCase {

    Window<ReadNewsUseCaseResult> readAllNews(NewsFilter newsFilter, Pageable pageable);

    ReadNewsUseCaseResult readNews(UUID newsId);

    ReadNewsUseCaseResult readNewsWithComments(UUID newsId, Pageable pageable);

}
