package ru.clevertec.newssystem.comment.port.output.persistence;

import java.util.UUID;

public interface DeleteCommentPersistencePort {

    void deleteComment(UUID commentId, UUID newsId);

}
