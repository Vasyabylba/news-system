package ru.clevertec.newssystem.news.port.output.persistence;

import ru.clevertec.newssystem.news.port.output.persistence.command.CreateNewsPersistencePortCommand;
import ru.clevertec.newssystem.news.port.output.persistence.result.NewsPersistencePortResult;

public interface CreateNewsPersistencePort {

    NewsPersistencePortResult createNews(CreateNewsPersistencePortCommand command);

}
