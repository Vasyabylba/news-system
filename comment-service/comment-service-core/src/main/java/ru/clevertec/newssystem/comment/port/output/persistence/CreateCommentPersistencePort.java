package ru.clevertec.newssystem.comment.port.output.persistence;

import ru.clevertec.newssystem.comment.port.output.persistence.command.CreateCommentPersistencePortCommand;
import ru.clevertec.newssystem.comment.port.output.persistence.result.CommentPersistencePortResult;

public interface CreateCommentPersistencePort {

    CommentPersistencePortResult createComment(
            CreateCommentPersistencePortCommand command);

}
