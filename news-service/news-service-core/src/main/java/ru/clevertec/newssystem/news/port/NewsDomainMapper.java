package ru.clevertec.newssystem.news.port;

import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Window;
import ru.clevertec.newssystem.news.domain.Comment;
import ru.clevertec.newssystem.news.domain.News;
import ru.clevertec.newssystem.news.port.input.news.command.CreateNewsUseCaseCommand;
import ru.clevertec.newssystem.news.port.input.news.command.UpdateNewsUseCaseCommand;
import ru.clevertec.newssystem.news.port.input.news.result.CreateNewsUseCaseResult;
import ru.clevertec.newssystem.news.port.input.news.result.ReadNewsUseCaseResult;
import ru.clevertec.newssystem.news.port.input.news.result.UpdateNewsUseCaseResult;
import ru.clevertec.newssystem.news.port.output.persistence.command.CreateNewsPersistencePortCommand;
import ru.clevertec.newssystem.news.port.output.persistence.command.UpdateNewsPersistencePortCommand;
import ru.clevertec.newssystem.news.port.output.persistence.result.NewsPersistencePortResult;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = CommentDomainMapper.class)
public interface NewsDomainMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastModifiedAt", ignore = true)
    @Mapping(target = "comments", ignore = true)
    News toNews(CreateNewsUseCaseCommand createNewsUseCaseCommand);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    News updateWithNull(UpdateNewsUseCaseCommand updateNewsUseCaseCommand, @MappingTarget News news);

    @Mapping(target = "comments", ignore = true)
    ReadNewsUseCaseResult toReadNewsUseCaseResult(News news);

    CreateNewsUseCaseResult toCreateNewsUseCaseResult(News news);

    UpdateNewsUseCaseResult toUpdateNewsUseCaseResult(News news);

    UpdateNewsPersistencePortCommand toUpdateNewsPersistencePortCommand(News news);

    CreateNewsPersistencePortCommand toCreateNewsPersistencePortCommand(News news);

    @Mapping(target = "comments", ignore = true)
    News toNews(NewsPersistencePortResult newsPersistencePortResult);

    @AfterMapping
    default void linkComments(@MappingTarget News news) {
        news.getComments().forEach(comment -> comment.setNews(news));
    }

    @Mapping(target = "comments", source = "comments")
    ReadNewsUseCaseResult toReadNewsUseCaseResult(News news, Window<Comment> comments);

}