package ru.clevertec.newssystem.news.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.clevertec.newssystem.news.domain.News;
import ru.clevertec.newssystem.news.port.NewsDomainMapper;
import ru.clevertec.newssystem.news.port.input.news.CreateNewsUseCase;
import ru.clevertec.newssystem.news.port.input.news.command.CreateNewsUseCaseCommand;
import ru.clevertec.newssystem.news.port.input.news.result.CreateNewsUseCaseResult;
import ru.clevertec.newssystem.news.port.output.persistence.CreateNewsPersistencePort;
import ru.clevertec.newssystem.news.port.output.persistence.command.CreateNewsPersistencePortCommand;
import ru.clevertec.newssystem.news.port.output.persistence.result.NewsPersistencePortResult;

/**
 * A service for creating news. Implements {@link CreateNewsUseCase}.
 */
@Service
@RequiredArgsConstructor
public class CreateNewsService implements CreateNewsUseCase {

    private final NewsDomainMapper newsMapper;

    private final CreateNewsPersistencePort createNewsPersistencePort;

    /**
     * Method for creating the news
     */
    @Override
    public CreateNewsUseCaseResult createNews(CreateNewsUseCaseCommand command) {
        News news = newsMapper.toNews(command);

        CreateNewsPersistencePortCommand createNewsPersistencePortCommand =
                newsMapper.toCreateNewsPersistencePortCommand(news);
        NewsPersistencePortResult newsPersistencePortResult =
                createNewsPersistencePort.createNews(createNewsPersistencePortCommand);

        News savedNews = newsMapper.toNews(newsPersistencePortResult);

        return newsMapper.toCreateNewsUseCaseResult(savedNews);
    }

}
