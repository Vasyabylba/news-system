package ru.clevertec.newssystem.news.port.input.news;

import java.util.UUID;

public interface DeleteNewsUseCase {

    void deleteNews(UUID newsId);

}
