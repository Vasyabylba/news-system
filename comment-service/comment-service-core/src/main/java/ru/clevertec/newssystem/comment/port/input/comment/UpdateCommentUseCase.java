package ru.clevertec.newssystem.comment.port.input.comment;

import ru.clevertec.newssystem.comment.port.input.comment.command.UpdateCommentUseCaseCommand;
import ru.clevertec.newssystem.comment.port.input.comment.result.UpdateCommentUseCaseResult;

import java.util.UUID;

public interface UpdateCommentUseCase {

    UpdateCommentUseCaseResult updateComment(UUID newsId, UUID commentId, UpdateCommentUseCaseCommand command);

}
