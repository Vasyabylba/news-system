package ru.clevertec.newssystem.comment.port.input.comment;

import ru.clevertec.newssystem.comment.port.input.comment.command.CreateCommentUseCaseCommand;
import ru.clevertec.newssystem.comment.port.input.comment.result.CreateCommentUseCaseResult;

import java.util.UUID;

public interface CreateCommentUseCase {

    CreateCommentUseCaseResult createComment(UUID newsId, CreateCommentUseCaseCommand command);

}
