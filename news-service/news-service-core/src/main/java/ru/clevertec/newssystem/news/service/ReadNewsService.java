package ru.clevertec.newssystem.news.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Window;
import org.springframework.stereotype.Service;
import ru.clevertec.newssystem.news.domain.Comment;
import ru.clevertec.newssystem.news.domain.News;
import ru.clevertec.newssystem.news.exception.NewsBadRequestException;
import ru.clevertec.newssystem.news.filter.NewsFilter;
import ru.clevertec.newssystem.news.port.CommentDomainMapper;
import ru.clevertec.newssystem.news.port.NewsDomainMapper;
import ru.clevertec.newssystem.news.port.input.news.ReadNewsUseCase;
import ru.clevertec.newssystem.news.port.input.news.result.ReadNewsUseCaseResult;
import ru.clevertec.newssystem.news.port.output.client.ReadCommentClientPort;
import ru.clevertec.newssystem.news.port.output.persistence.ReadNewsPersistencePort;
import ru.clevertec.newssystem.news.port.output.persistence.result.NewsPersistencePortResult;

import java.util.UUID;

/**
 * A service for reading news. Implements {@link ReadNewsUseCase}.
 */
@Service
@RequiredArgsConstructor
public class ReadNewsService implements ReadNewsUseCase {

    private final NewsDomainMapper newsMapper;

    private final ReadNewsPersistencePort readNewsPersistencePort;

    private final ReadCommentClientPort readCommentClientPort;

    private final CommentDomainMapper commentMapper;

    /**
     * Method for reading all news
     */
    @Override
    public Window<ReadNewsUseCaseResult> readAllNews(NewsFilter newsFilter, Pageable pageable) {
        Window<NewsPersistencePortResult> readNewsPersistencePortResults =
                readNewsPersistencePort.readAllNews(newsFilter, pageable);

        Window<News> news = readNewsPersistencePortResults.map(newsMapper::toNews);

        return news.map(newsMapper::toReadNewsUseCaseResult);
    }

    /**
     * A method for reading the news
     */
    @Override
    public ReadNewsUseCaseResult readNews(UUID newsId) {
        News news = getNewsById(newsId);

        return newsMapper.toReadNewsUseCaseResult(news);
    }

    /**
     * Method for reading the news with comments
     */
    @Override
    public ReadNewsUseCaseResult readNewsWithComments(UUID newsId, Pageable pageable) {
        News news = getNewsById(newsId);

        Window<Comment> comments = readCommentClientPort.readCommentsByNews(newsId, pageable)
                .map(commentMapper::toComment);

        return newsMapper.toReadNewsUseCaseResult(news, comments);
    }

    private News getNewsById(UUID newsId) {
        checkIfNewsIdIsNotNull(newsId);

        return newsMapper.toNews(readNewsPersistencePort.readNewsById(newsId));
    }

    private void checkIfNewsIdIsNotNull(UUID newsId) {
        if (newsId == null) {
            throw NewsBadRequestException.byNewsIdIsNull();
        }
    }

}
