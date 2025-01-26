package ru.clevertec.newssystem.comment.exception;

import ru.clevertec.exceptionhandler.exception.ResourceNotFoundException;

import java.util.UUID;

public class CommentNotFoundException extends ResourceNotFoundException {

    public static final String COMMENT_WITH_ID_S_NOT_FOUND = "Comment with id '%s' not found";

    public CommentNotFoundException(String message) {
        super(message);
    }

    public static CommentNotFoundException byCommentId(UUID commentId) {
        return new CommentNotFoundException(
                String.format(COMMENT_WITH_ID_S_NOT_FOUND, commentId)
        );
    }

}
