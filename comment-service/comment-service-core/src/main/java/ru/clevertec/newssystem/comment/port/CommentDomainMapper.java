package ru.clevertec.newssystem.comment.port;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import ru.clevertec.newssystem.comment.domain.Comment;
import ru.clevertec.newssystem.comment.port.input.comment.command.CreateCommentUseCaseCommand;
import ru.clevertec.newssystem.comment.port.input.comment.command.UpdateCommentUseCaseCommand;
import ru.clevertec.newssystem.comment.port.input.comment.result.CreateCommentUseCaseResult;
import ru.clevertec.newssystem.comment.port.input.comment.result.ReadCommentUseCaseResult;
import ru.clevertec.newssystem.comment.port.input.comment.result.UpdateCommentUseCaseResult;
import ru.clevertec.newssystem.comment.port.output.persistence.command.CreateCommentPersistencePortCommand;
import ru.clevertec.newssystem.comment.port.output.persistence.command.UpdateCommentPersistencePortCommand;
import ru.clevertec.newssystem.comment.port.output.persistence.result.CommentPersistencePortResult;

import java.util.UUID;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommentDomainMapper {

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastModifiedAt", ignore = true)
    @Mapping(target = "news.id", source = "newsId")
    Comment toComment(UUID newsId, CreateCommentUseCaseCommand command);

    @Mapping(target = "newsId", source = "news.id")
    CreateCommentPersistencePortCommand toCreateCommentPersistencePortCommand(Comment comment);

    @Mapping(target = "newsId", source = "news.id")
    UpdateCommentPersistencePortCommand toUpdateCommentPersistencePortCommand(Comment comment);

    ReadCommentUseCaseResult toReadCommentUseCaseResult(Comment comment);

    CreateCommentUseCaseResult toCreateCommentUseCaseResult(Comment comment);

    UpdateCommentUseCaseResult toUpdateCommentUseCaseResult(Comment comment);

    @Mapping(target = "news.id", source = "newsId")
    Comment toComment(CommentPersistencePortResult commentPersistencePortResult);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastModifiedAt", ignore = true)
    @Mapping(target = "news", ignore = true)
    Comment updateWithNull(UpdateCommentUseCaseCommand command, @MappingTarget Comment comment);

}
