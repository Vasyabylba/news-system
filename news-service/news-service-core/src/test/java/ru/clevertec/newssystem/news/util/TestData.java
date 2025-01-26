package ru.clevertec.newssystem.news.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Window;
import ru.clevertec.newssystem.news.domain.Comment;
import ru.clevertec.newssystem.news.domain.News;
import ru.clevertec.newssystem.news.filter.NewsFilter;
import ru.clevertec.newssystem.news.port.input.comment.result.ReadCommentUseCaseResult;
import ru.clevertec.newssystem.news.port.input.news.command.CreateNewsUseCaseCommand;
import ru.clevertec.newssystem.news.port.input.news.command.UpdateNewsUseCaseCommand;
import ru.clevertec.newssystem.news.port.input.news.result.CreateNewsUseCaseResult;
import ru.clevertec.newssystem.news.port.input.news.result.ReadNewsUseCaseResult;
import ru.clevertec.newssystem.news.port.input.news.result.UpdateNewsUseCaseResult;
import ru.clevertec.newssystem.news.port.output.client.result.ReadCommentClientPortResult;
import ru.clevertec.newssystem.news.port.output.persistence.command.CreateNewsPersistencePortCommand;
import ru.clevertec.newssystem.news.port.output.persistence.command.UpdateNewsPersistencePortCommand;
import ru.clevertec.newssystem.news.port.output.persistence.result.NewsPersistencePortResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TestData {

    public static CreateNewsUseCaseCommand createCreateNewsUseCaseCommand() {
        return CreateNewsUseCaseCommand.builder()
                .title("News Title")
                .text("Content of news")
                .build();
    }

    public static News createCreatingNews(CreateNewsUseCaseCommand command) {
        return News.builder()
                .title(command.title())
                .text(command.text())
                .build();
    }

    public static CreateNewsPersistencePortCommand createCreateNewsPersistencePortCommand(News news) {
        return CreateNewsPersistencePortCommand.builder()
                .title(news.getTitle())
                .text(news.getText())
                .build();
    }

    public static NewsPersistencePortResult createCreateNewsPersistencePortResult(
            CreateNewsPersistencePortCommand command) {
        return NewsPersistencePortResult.builder()
                .id(UUID.randomUUID())
                .createdAt(LocalDateTime.now())
                .lastModifiedAt(LocalDateTime.now())
                .title(command.title())
                .text(command.text())
                .build();
    }

    public static News createNews(NewsPersistencePortResult portResult) {
        return News.builder()
                .id(portResult.id())
                .createdAt(portResult.createdAt())
                .lastModifiedAt(portResult.lastModifiedAt())
                .title(portResult.title())
                .text(portResult.text())
                .build();
    }

    public static CreateNewsUseCaseResult createCreateNewsUseCaseResult(News news) {
        return CreateNewsUseCaseResult.builder()
                .id(news.getId())
                .createdAt(news.getCreatedAt())
                .lastModifiedAt(news.getLastModifiedAt())
                .title(news.getTitle())
                .text(news.getText())
                .build();
    }

    public static NewsFilter createNewsFilter() {
        return NewsFilter.builder()
                .createdAtGte(LocalDateTime.of(2025, 1, 1, 0, 0))
                .createdAtLte(LocalDateTime.now())
                .titleContains("news")
                .textContains("content")
                .build();
    }

    public static Pageable createUnsortedPageable(int pageNumber, int pageSize) {
        return PageRequest.of(pageNumber, pageSize);
    }

    public static Window<NewsPersistencePortResult> createWindowNewsPersistencePortResult(Pageable pageable) {
        return Window.from(
                generateNewsPersistencePortResults(pageable.getPageSize()),
                pageable.toScrollPosition().positionFunction()
        );
    }

    public static List<NewsPersistencePortResult> generateNewsPersistencePortResults(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> NewsPersistencePortResult.builder()
                        .id(UUID.randomUUID())
                        .createdAt(LocalDateTime.now().minusDays(count + i - 1))
                        .lastModifiedAt(LocalDateTime.now())
                        .title("News Title " + (i + 1))
                        .text("Content of news " + (i + 1))
                        .build())
                .collect(Collectors.toList());
    }

    public static Window<ReadNewsUseCaseResult> createWindowReadNewsUseCaseResult(
            List<NewsPersistencePortResult> readNewsPersistencePortResult,
            Pageable pageable
    ) {
        return Window.from(
                generateReadNewsUseCaseResults(readNewsPersistencePortResult),
                pageable.toScrollPosition().positionFunction()
        );
    }

    public static List<ReadNewsUseCaseResult> generateReadNewsUseCaseResults(
            List<NewsPersistencePortResult> readNewsPersistencePortResult
    ) {
        return generateNews(readNewsPersistencePortResult).stream()
                .map(news -> ReadNewsUseCaseResult.builder()
                        .id(news.getId())
                        .title(news.getTitle())
                        .text(news.getText())
                        .createdAt(news.getCreatedAt())
                        .lastModifiedAt(news.getLastModifiedAt())
                        .build())
                .collect(Collectors.toList());
    }

    public static List<News> generateNews(List<NewsPersistencePortResult> persistenceResultList) {
        return persistenceResultList.stream()
                .map(persistenceResult -> News.builder()
                        .id(persistenceResult.id())
                        .createdAt(persistenceResult.createdAt())
                        .lastModifiedAt(persistenceResult.lastModifiedAt())
                        .title(persistenceResult.title())
                        .text(persistenceResult.text())
                        .build())
                .collect(Collectors.toList());
    }

    public static NewsPersistencePortResult createNewsPersistencePortResult(UUID newsId) {
        return NewsPersistencePortResult.builder()
                .id(newsId)
                .createdAt(LocalDateTime.of(2025, 1, 1, 0, 0))
                .lastModifiedAt(LocalDateTime.now())
                .title("News Title")
                .text("Content of news")
                .build();
    }

    public static ReadNewsUseCaseResult createReadNewsUseCaseResult(News news) {
        return ReadNewsUseCaseResult.builder()
                .id(news.getId())
                .createdAt(news.getCreatedAt())
                .lastModifiedAt(news.getLastModifiedAt())
                .title(news.getTitle())
                .text(news.getText())
                .build();
    }

    public static UpdateNewsUseCaseCommand createUpdateNewsUseCaseCommand() {
        return UpdateNewsUseCaseCommand.builder()
                .title("Updated news title")
                .text("Updated content of news")
                .build();
    }

    public static News toUpdatingNews(UpdateNewsUseCaseCommand command, News news) {
        news.setTitle(command.title());
        news.setText(command.text());
        return news;
    }

    public static UpdateNewsPersistencePortCommand createUpdateNewsPersistencePortCommand(News news) {
        return UpdateNewsPersistencePortCommand.builder()
                .id(news.getId())
                .createdAt(news.getCreatedAt())
                .title(news.getTitle())
                .text(news.getText())
                .build();
    }

    public static NewsPersistencePortResult createNewsPersistencePortResult(
            UpdateNewsPersistencePortCommand command
    ) {
        return NewsPersistencePortResult.builder()
                .id(command.id())
                .createdAt(command.createdAt())
                .lastModifiedAt(LocalDateTime.now())
                .text(command.title())
                .text(command.text())
                .build();
    }

    public static News createUpdatedNews(NewsPersistencePortResult portResult) {
        return News.builder()
                .id(portResult.id())
                .createdAt(portResult.createdAt())
                .lastModifiedAt(portResult.lastModifiedAt())
                .title(portResult.title())
                .text(portResult.text())
                .build();
    }

    public static UpdateNewsUseCaseResult createUpdateNewsUseCaseResult(News news) {
        return UpdateNewsUseCaseResult.builder()
                .id(news.getId())
                .createdAt(news.getCreatedAt())
                .lastModifiedAt(news.getLastModifiedAt())
                .title(news.getTitle())
                .text(news.getText())
                .build();
    }

    public static Window<ReadCommentClientPortResult> createWindowReadCommentClientPortResult(Pageable pageable) {
        return Window.from(
                createReadCommentClientPortResultList(pageable.getPageSize()),
                pageable.toScrollPosition().positionFunction()
        );
    }

    private static List<ReadCommentClientPortResult> createReadCommentClientPortResultList(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> ReadCommentClientPortResult.builder()
                        .id(UUID.randomUUID())
                        .createdAt(LocalDateTime.now().minusDays(count + i - 1))
                        .lastModifiedAt(LocalDateTime.now())
                        .text("This is a comment " + (i + 1))
                        .username("username" + (i + 1))
                        .build())
                .collect(Collectors.toList());
    }

    public static Window<Comment> toWindowComments(Window<ReadCommentClientPortResult> clientResults) {
        return clientResults.map(input ->
                Comment.builder()
                        .id(input.id())
                        .createdAt(input.createdAt())
                        .lastModifiedAt(input.lastModifiedAt())
                        .text(input.text())
                        .username(input.username())
                        .build()
        );
    }

    public static ReadNewsUseCaseResult toReadNewsUseCaseResult(News news, Window<Comment> comments) {
        return ReadNewsUseCaseResult.builder()
                .id(news.getId())
                .createdAt(news.getCreatedAt())
                .lastModifiedAt(news.getLastModifiedAt())
                .title(news.getTitle())
                .text(news.getText())
                .comments(commentWindowToReadCommentUseCaseResultWindow(comments))
                .build();
    }

    private static Window<ReadCommentUseCaseResult> commentWindowToReadCommentUseCaseResultWindow(
            Window<Comment> comments
    ) {
        return comments.map(comment ->
                ReadCommentUseCaseResult.builder()
                        .id(comment.getId())
                        .createdAt(comment.getCreatedAt())
                        .lastModifiedAt(comment.getLastModifiedAt())
                        .text(comment.getText())
                        .username(comment.getUsername())
                        .build());
    }

}
