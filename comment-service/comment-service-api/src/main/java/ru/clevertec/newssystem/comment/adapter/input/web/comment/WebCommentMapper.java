package ru.clevertec.newssystem.comment.adapter.input.web.comment;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.clevertec.newssystem.comment.adapter.input.web.comment.dto.CommentCreateRequest;
import ru.clevertec.newssystem.comment.adapter.input.web.comment.dto.CommentResponse;
import ru.clevertec.newssystem.comment.adapter.input.web.comment.dto.CommentUpdateRequest;
import ru.clevertec.newssystem.comment.port.input.comment.command.CreateCommentUseCaseCommand;
import ru.clevertec.newssystem.comment.port.input.comment.command.UpdateCommentUseCaseCommand;
import ru.clevertec.newssystem.comment.port.input.comment.result.CreateCommentUseCaseResult;
import ru.clevertec.newssystem.comment.port.input.comment.result.ReadCommentUseCaseResult;
import ru.clevertec.newssystem.comment.port.input.comment.result.UpdateCommentUseCaseResult;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface WebCommentMapper {

    CreateCommentUseCaseCommand toCreateCommentUseCaseCommand(CommentCreateRequest commentCreateRequest);

    UpdateCommentUseCaseCommand toUpdateCommentUseCaseCommand(CommentUpdateRequest commentUpdateRequest);

    CommentResponse toCommentResponse(CreateCommentUseCaseResult createCommentUseCaseResult);

    CommentResponse toCommentResponse(UpdateCommentUseCaseResult updateCommentUseCaseResult);

    CommentResponse toCommentResponse(ReadCommentUseCaseResult readCommentUseCaseResult);

}