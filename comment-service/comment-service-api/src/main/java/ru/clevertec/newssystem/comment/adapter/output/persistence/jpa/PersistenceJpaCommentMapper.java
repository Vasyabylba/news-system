package ru.clevertec.newssystem.comment.adapter.output.persistence.jpa;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.clevertec.newssystem.comment.adapter.output.persistence.jpa.entity.CommentEntity;
import ru.clevertec.newssystem.comment.port.output.persistence.command.CreateCommentPersistencePortCommand;
import ru.clevertec.newssystem.comment.port.output.persistence.command.UpdateCommentPersistencePortCommand;
import ru.clevertec.newssystem.comment.port.output.persistence.result.CommentPersistencePortResult;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface PersistenceJpaCommentMapper {

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastModifiedAt", ignore = true)
    CommentEntity toCommentEntity(CreateCommentPersistencePortCommand command);

    CommentEntity toCommentEntity(UpdateCommentPersistencePortCommand command);

    CommentPersistencePortResult toCommentPersistencePortResult(CommentEntity commentEntity);

}