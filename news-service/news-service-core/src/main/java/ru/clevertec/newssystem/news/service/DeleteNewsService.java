package ru.clevertec.newssystem.news.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.clevertec.newssystem.news.exception.NewsBadRequestException;
import ru.clevertec.newssystem.news.port.input.news.DeleteNewsUseCase;
import ru.clevertec.newssystem.news.port.output.persistence.DeleteNewsPersistencePort;

import java.util.UUID;

/**
 * A service for deleting news. Implements {@link DeleteNewsUseCase}.
 */
@Service
@RequiredArgsConstructor
public class DeleteNewsService implements DeleteNewsUseCase {

    private final DeleteNewsPersistencePort deleteNewsPersistencePort;

    /**
     * Method for deleting the news
     */
    @Override
    public void deleteNews(UUID newsId) {
        checkIfNewsIdIsNotNull(newsId);

        deleteNewsPersistencePort.deleteNewsById(newsId);
    }

    private void checkIfNewsIdIsNotNull(UUID newsId) {
        if (newsId == null) {
            throw NewsBadRequestException.byNewsIdIsNull();
        }
    }

}
