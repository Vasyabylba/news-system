package ru.clevertec.newssystem.news.adapter.input.web.news;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Window;
import ru.clevertec.newssystem.news.adapter.input.web.news.dto.CommentResponse;
import ru.clevertec.newssystem.news.adapter.input.web.news.dto.NewsCreateRequest;
import ru.clevertec.newssystem.news.adapter.input.web.news.dto.NewsResponse;
import ru.clevertec.newssystem.news.adapter.input.web.news.dto.NewsUpdateRequest;
import ru.clevertec.newssystem.news.adapter.input.web.news.dto.NewsWithCommentsResponse;
import ru.clevertec.newssystem.news.port.input.comment.result.ReadCommentUseCaseResult;
import ru.clevertec.newssystem.news.port.input.news.command.CreateNewsUseCaseCommand;
import ru.clevertec.newssystem.news.port.input.news.command.UpdateNewsUseCaseCommand;
import ru.clevertec.newssystem.news.port.input.news.result.CreateNewsUseCaseResult;
import ru.clevertec.newssystem.news.port.input.news.result.ReadNewsUseCaseResult;
import ru.clevertec.newssystem.news.port.input.news.result.UpdateNewsUseCaseResult;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface WebNewsMapper {

    CreateNewsUseCaseCommand toCreateNewsUseCaseCommand(NewsCreateRequest newsCreateRequest);

    UpdateNewsUseCaseCommand toUpdateNewsUseCaseCommand(NewsUpdateRequest newsUpdateRequest);

    NewsResponse toNewsResponse(CreateNewsUseCaseResult createNewsUseCaseResult);

    NewsResponse toNewsResponse(UpdateNewsUseCaseResult updateNewsUseCaseResult);

    NewsResponse toNewsResponse(ReadNewsUseCaseResult readNewsUseCaseResult);

    @Mapping(target = "comments", source = "comments")
    NewsWithCommentsResponse toNewsWithCommentResponse(ReadNewsUseCaseResult readNewsUseCaseResult);

    default Window<CommentResponse> readCommentUseCaseResultToCommentResponse(
            Window<ReadCommentUseCaseResult> comments
    ) {
        return comments.map(this::readCommentUseCaseResultToCommentResponse);
    }

    CommentResponse readCommentUseCaseResultToCommentResponse(ReadCommentUseCaseResult comments);

}