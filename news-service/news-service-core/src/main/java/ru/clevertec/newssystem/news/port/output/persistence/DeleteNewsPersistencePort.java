package ru.clevertec.newssystem.news.port.output.persistence;

import java.util.UUID;

public interface DeleteNewsPersistencePort {

    void deleteNewsById(UUID newsId);

}
