package ru.clevertec.newssystem.news.exception;

import ru.clevertec.exceptionhandler.exception.ResourceBadRequestException;

public class NewsBadRequestException extends ResourceBadRequestException {

    public static final String NEWS_ID_MUST_NOT_BE_NULL = "News id must not be null";

    public NewsBadRequestException(String message) {
        super(message);
    }

    public static NewsBadRequestException byNewsIdIsNull() {
        return new NewsBadRequestException(NEWS_ID_MUST_NOT_BE_NULL);
    }

}
