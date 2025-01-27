package ru.clevertec.newssystem.comment.exception;

import ru.clevertec.exceptionhandler.exception.ResourceNotFoundException;

public class CommentBadRequestException extends ResourceNotFoundException {

    public static final String COMMENT_ID_MUST_NOT_BE_NULL = "Comment id must not be null";

    public CommentBadRequestException(String message) {
        super(message);
    }

    public static CommentBadRequestException byCommentIdIsNull() {
        return new CommentBadRequestException(COMMENT_ID_MUST_NOT_BE_NULL);
    }

}
