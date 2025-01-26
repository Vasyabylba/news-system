package ru.clevertec.newssystem.comment.port.input.comment;

import java.util.UUID;

public interface DeleteCommentUseCase {

    void deleteComment(UUID newsId, UUID commentId);

}
