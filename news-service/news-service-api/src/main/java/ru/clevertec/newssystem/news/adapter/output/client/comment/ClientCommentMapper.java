package ru.clevertec.newssystem.news.adapter.output.client.comment;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.clevertec.newssystem.news.adapter.output.client.comment.dto.CommentResponse;
import ru.clevertec.newssystem.news.port.output.client.result.ReadCommentClientPortResult;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ClientCommentMapper {

    ReadCommentClientPortResult toReadCommentClientPortResult(CommentResponse commentResponse);

}
