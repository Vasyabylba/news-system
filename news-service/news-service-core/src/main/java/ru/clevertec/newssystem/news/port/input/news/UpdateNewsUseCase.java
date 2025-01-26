package ru.clevertec.newssystem.news.port.input.news;

import ru.clevertec.newssystem.news.port.input.news.command.UpdateNewsUseCaseCommand;
import ru.clevertec.newssystem.news.port.input.news.result.UpdateNewsUseCaseResult;

import java.util.UUID;

public interface UpdateNewsUseCase {

    UpdateNewsUseCaseResult updateNews(UUID newsId, UpdateNewsUseCaseCommand command);

}
