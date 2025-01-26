package ru.clevertec.newssystem.comment.port.output.persistence;

import ru.clevertec.newssystem.comment.port.output.persistence.command.UpdateCommentPersistencePortCommand;
import ru.clevertec.newssystem.comment.port.output.persistence.result.CommentPersistencePortResult;

public interface UpdateCommentPersistencePort {

    CommentPersistencePortResult updateComment(UpdateCommentPersistencePortCommand command);

}
