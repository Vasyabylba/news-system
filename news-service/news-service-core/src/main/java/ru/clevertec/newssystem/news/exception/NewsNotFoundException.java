package ru.clevertec.newssystem.news.exception;

import ru.clevertec.exceptionhandler.exception.ResourceNotFoundException;

import java.util.UUID;

public class NewsNotFoundException extends ResourceNotFoundException {

    public static final String NEWS_WITH_ID_NOT_FOUND = "News with id '%s' not found";

    public NewsNotFoundException(String message) {
        super(message);
    }

    public static NewsNotFoundException byNewsId(UUID newsId) {
        return new NewsNotFoundException(
                String.format(NEWS_WITH_ID_NOT_FOUND, newsId)
        );
    }

}
