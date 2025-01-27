package ru.clevertec.newssystem.news.port.input.news;

import ru.clevertec.newssystem.news.port.input.news.command.CreateNewsUseCaseCommand;
import ru.clevertec.newssystem.news.port.input.news.result.CreateNewsUseCaseResult;

public interface CreateNewsUseCase {

    CreateNewsUseCaseResult createNews(CreateNewsUseCaseCommand command);

}
