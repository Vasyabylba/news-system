package ru.clevertec.newssystem.news.port;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Window;
import ru.clevertec.newssystem.news.domain.Comment;
import ru.clevertec.newssystem.news.port.input.comment.result.ReadCommentUseCaseResult;
import ru.clevertec.newssystem.news.port.output.client.result.ReadCommentClientPortResult;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommentDomainMapper {

    @Mapping(target = "news", ignore = true)
    Comment toComment(ReadCommentClientPortResult readCommentClientPortResult);

    default Window<ReadCommentUseCaseResult> commentsToReadCommentUseCaseResult(Window<Comment> comments) {
        return comments.map(this::tReadCommentUseCaseResult);
    }

    ReadCommentUseCaseResult tReadCommentUseCaseResult(Comment comment);

}
