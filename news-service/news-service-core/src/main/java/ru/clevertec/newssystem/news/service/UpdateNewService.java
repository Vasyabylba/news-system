package ru.clevertec.newssystem.news.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.clevertec.newssystem.news.domain.News;
import ru.clevertec.newssystem.news.exception.NewsBadRequestException;
import ru.clevertec.newssystem.news.port.NewsDomainMapper;
import ru.clevertec.newssystem.news.port.input.news.UpdateNewsUseCase;
import ru.clevertec.newssystem.news.port.input.news.command.UpdateNewsUseCaseCommand;
import ru.clevertec.newssystem.news.port.input.news.result.UpdateNewsUseCaseResult;
import ru.clevertec.newssystem.news.port.output.persistence.ReadNewsPersistencePort;
import ru.clevertec.newssystem.news.port.output.persistence.UpdateNewsPersistencePort;
import ru.clevertec.newssystem.news.port.output.persistence.command.UpdateNewsPersistencePortCommand;
import ru.clevertec.newssystem.news.port.output.persistence.result.NewsPersistencePortResult;

import java.util.UUID;

/**
 * A service for updating news. Implements {@link UpdateNewsUseCase}.
 */
@Service
@RequiredArgsConstructor
public class UpdateNewService implements UpdateNewsUseCase {

    private final NewsDomainMapper newsMapper;

    private final UpdateNewsPersistencePort updateNewsPersistencePort;

    private final ReadNewsPersistencePort readNewsPersistencePort;

    /**
     * Method for updating the news
     */
    @Override
    public UpdateNewsUseCaseResult updateNews(UUID newsId, UpdateNewsUseCaseCommand command) {
        News news = getNewsById(newsId);

        newsMapper.updateWithNull(command, news);
        UpdateNewsPersistencePortCommand updateNewsPersistencePortCommand =
                newsMapper.toUpdateNewsPersistencePortCommand(news);
        NewsPersistencePortResult newsPersistencePortResult =
                updateNewsPersistencePort.updateNews(updateNewsPersistencePortCommand);
        News updatedNews = newsMapper.toNews(newsPersistencePortResult);

        return newsMapper.toUpdateNewsUseCaseResult(updatedNews);
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
