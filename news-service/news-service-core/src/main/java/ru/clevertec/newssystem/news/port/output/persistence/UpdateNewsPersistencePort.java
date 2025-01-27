package ru.clevertec.newssystem.news.port.output.persistence;

import ru.clevertec.newssystem.news.port.output.persistence.command.UpdateNewsPersistencePortCommand;
import ru.clevertec.newssystem.news.port.output.persistence.result.NewsPersistencePortResult;

public interface UpdateNewsPersistencePort {

    NewsPersistencePortResult updateNews(UpdateNewsPersistencePortCommand command);

}
