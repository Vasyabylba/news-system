package ru.clevertec.newssystem.news.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Window;
import org.springframework.data.jpa.domain.Specification;
import ru.clevertec.newssystem.news.adapter.input.web.news.dto.CommentResponse;
import ru.clevertec.newssystem.news.adapter.input.web.news.dto.NewsCreateRequest;
import ru.clevertec.newssystem.news.adapter.input.web.news.dto.NewsResponse;
import ru.clevertec.newssystem.news.adapter.input.web.news.dto.NewsUpdateRequest;
import ru.clevertec.newssystem.news.adapter.input.web.news.dto.NewsWithCommentsResponse;
import ru.clevertec.newssystem.news.adapter.output.client.comment.dto.ClientCommentWindowResponse;
import ru.clevertec.newssystem.news.adapter.output.persistence.jpa.entity.NewsEntity;
import ru.clevertec.newssystem.news.adapter.output.persistence.jpa.specification.NewsSpecification;
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

    public static Window<ReadNewsUseCaseResult> createWindowOfReadNewsUseCaseResult(int count) {
        List<ReadNewsUseCaseResult> results = createReadNewsUseCaseResultWithOutComments(count);
        return Window.from(results, ScrollPosition.offset().positionFunction());
    }

    public static List<ReadNewsUseCaseResult> createReadNewsUseCaseResultWithOutComments(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> createReadNewsUseCaseResultWithOutComments())
                .collect(Collectors.toList());
    }

    public static ReadNewsUseCaseResult createReadNewsUseCaseResultWithOutComments() {
        return ReadNewsUseCaseResult.builder()
                .id(UUID.randomUUID())
                .createdAt(LocalDateTime.now())
                .lastModifiedAt(LocalDateTime.now())
                .title("News 1 Title")
                .text("Content of news 1")
                .build();
    }

    public static ReadNewsUseCaseResult createReadNewsUseCaseResultWithComments(int count) {
        return ReadNewsUseCaseResult.builder()
                .id(UUID.randomUUID())
                .createdAt(LocalDateTime.now())
                .lastModifiedAt(LocalDateTime.now())
                .title("News 1 Title")
                .text("Content of news 1")
                .comments(createWindowOfReadCommentUseCaseResult(count))
                .build();
    }

    public static Window<ReadCommentUseCaseResult> createWindowOfReadCommentUseCaseResult(int count) {
        List<ReadCommentUseCaseResult> results = createReadCommentUseCaseResult(count);
        return Window.from(results, ScrollPosition.offset().positionFunction());
    }

    public static List<ReadCommentUseCaseResult> createReadCommentUseCaseResult(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> createReadCommentUseCaseResult())
                .collect(Collectors.toList());
    }

    public static ReadCommentUseCaseResult createReadCommentUseCaseResult() {
        return ReadCommentUseCaseResult.builder()
                .id(UUID.randomUUID())
                .createdAt(LocalDateTime.now())
                .lastModifiedAt(LocalDateTime.now())
                .text("Comment text")
                .username("test_user")
                .build();
    }

    public static Window<NewsResponse> createWindowOfNewsResponse(
            Window<ReadNewsUseCaseResult> results
    ) {
        return results.map(news -> NewsResponse.builder()
                .id(news.id())
                .createdAt(news.createdAt())
                .lastModifiedAt(news.lastModifiedAt())
                .title(news.title())
                .text(news.text())
                .build());
    }

    public static NewsWithCommentsResponse toNewsWithCommentsResponse(ReadNewsUseCaseResult result) {
        return NewsWithCommentsResponse.builder()
                .id(result.id())
                .createdAt(result.createdAt())
                .lastModifiedAt(result.lastModifiedAt())
                .title(result.title())
                .text(result.text())
                .comments(toCommentResponse(result.comments()))
                .build();
    }

    public static Window<CommentResponse> toCommentResponse(Window<ReadCommentUseCaseResult> comments) {
        return comments.map(comment -> CommentResponse.builder()
                .id(comment.id())
                .createdAt(comment.createdAt())
                .lastModifiedAt(comment.lastModifiedAt())
                .text(comment.text())
                .username(comment.username())
                .build()
        );
    }

    public static NewsCreateRequest createNewsCreateRequest() {
        return NewsCreateRequest.builder()
                .title("News 1 Title")
                .text("Content of news 1")
                .build();
    }

    public static CreateNewsUseCaseCommand toCreateNewsUseCaseCommand(NewsCreateRequest request) {
        return CreateNewsUseCaseCommand.builder()
                .title(request.title())
                .text(request.text())
                .build();
    }

    public static CreateNewsUseCaseResult createCreateNewsUseCaseResult(CreateNewsUseCaseCommand command) {
        return CreateNewsUseCaseResult.builder()
                .id(UUID.randomUUID())
                .createdAt(LocalDateTime.now())
                .lastModifiedAt(LocalDateTime.now())
                .title(command.title())
                .text(command.text())
                .build();
    }

    public static NewsUpdateRequest createNewsUpdateRequest() {
        return NewsUpdateRequest.builder()
                .title("Updated News 1 Title")
                .text("Updated content of news 1")
                .build();
    }

    public static UpdateNewsUseCaseCommand toUpdateNewsUseCaseCommand(NewsUpdateRequest request) {
        return UpdateNewsUseCaseCommand.builder()
                .title(request.title())
                .text(request.text())
                .build();
    }

    public static UpdateNewsUseCaseResult createUpdateNewsUseCaseResult(UpdateNewsUseCaseCommand command) {
        return UpdateNewsUseCaseResult.builder()
                .id(UUID.randomUUID())
                .createdAt(LocalDateTime.now())
                .lastModifiedAt(LocalDateTime.now())
                .title(command.title())
                .text(command.text())
                .build();
    }

    public static NewsResponse toNewsResponse(ReadNewsUseCaseResult result) {
        return NewsResponse.builder()
                .id(result.id())
                .createdAt(result.createdAt())
                .lastModifiedAt(result.lastModifiedAt())
                .title(result.title())
                .text(result.text())
                .build();
    }

    public static NewsResponse toNewsResponse(CreateNewsUseCaseResult result) {
        return NewsResponse.builder()
                .id(result.id())
                .createdAt(result.createdAt())
                .lastModifiedAt(result.lastModifiedAt())
                .title(result.title())
                .text(result.text())
                .build();
    }

    public static NewsResponse toNewsResponse(UpdateNewsUseCaseResult result) {
        return NewsResponse.builder()
                .id(result.id())
                .createdAt(result.createdAt())
                .lastModifiedAt(result.lastModifiedAt())
                .title(result.title())
                .text(result.text())
                .build();
    }

    public static ClientCommentWindowResponse createClientCommentWindowResponse(int pageSize) {
        return ClientCommentWindowResponse.builder()
                .empty(false)
                .content(generateCommentResponseList(pageSize))
                .last(true)
                .build();
    }

    private static List<ru.clevertec.newssystem.news.adapter.output.client.comment.dto.CommentResponse> generateCommentResponseList(
            int count
    ) {
        return IntStream.range(0, count)
                .mapToObj(i -> ru.clevertec.newssystem.news.adapter.output.client.comment.dto.CommentResponse.builder()
                        .id(UUID.randomUUID())
                        .createdAt(LocalDateTime.now().minusDays(count + i - 1))
                        .lastModifiedAt(LocalDateTime.now())
                        .text("This is a comment " + (i + 1))
                        .username("username" + (i + 1))
                        .build())
                .collect(Collectors.toList());
    }

    public static ReadCommentClientPortResult createReadCommentClientPortResult() {
        return ReadCommentClientPortResult.builder()
                .text("This is a comment 1")
                .username("username1")
                .build();
    }

    public static CreateNewsPersistencePortCommand createCreateNewsPersistencePortCommand() {
        return CreateNewsPersistencePortCommand.builder()
                .title("News Title")
                .text("Content of news")
                .build();
    }

    public static NewsEntity toNewsEntity(CreateNewsPersistencePortCommand command) {
        return NewsEntity.builder()
                .title(command.title())
                .text(command.text())
                .build();
    }

    public static NewsEntity createSavedNewsEntity(NewsEntity news) {
        return NewsEntity.builder()
                .id(news.getId())
                .createdAt(LocalDateTime.now())
                .lastModifiedAt(LocalDateTime.now())
                .title(news.getTitle())
                .text(news.getText())
                .build();
    }

    public static NewsPersistencePortResult toCreateNewsPersistencePortResult(NewsEntity news) {
        return NewsPersistencePortResult.builder()
                .id(news.getId())
                .createdAt(news.getCreatedAt())
                .lastModifiedAt(news.getLastModifiedAt())
                .title(news.getTitle())
                .text(news.getText())
                .build();
    }

    public static Pageable createUnsortedPageable(int pageNumber, int pageSize) {
        return PageRequest.of(pageNumber, pageSize);
    }

    public static Window<NewsEntity> createWindowOfNewsEntity(Pageable pageable) {
        return Window.from(
                generateNewsEntityList(pageable.getPageSize()),
                pageable.toScrollPosition().positionFunction()
        );
    }

    public static List<NewsEntity> generateNewsEntityList(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> NewsEntity.builder()
                        .id(UUID.randomUUID())
                        .createdAt(LocalDateTime.now().minusDays(count + i - 1))
                        .lastModifiedAt(LocalDateTime.now())
                        .title("News " + (i + 1) + " Title")
                        .text("Content of news " + (i + 1))
                        .build())
                .collect(Collectors.toList());
    }

    public static Window<NewsPersistencePortResult> createNewsPersistencePortResultWindow(
            List<NewsEntity> news,
            Pageable pageable
    ) {
        return Window.from(
                generateReadNewsPersistencePortResultList(news),
                pageable.toScrollPosition().positionFunction()
        );
    }

    private static List<NewsPersistencePortResult> generateReadNewsPersistencePortResultList(
            List<NewsEntity> news
    ) {
        return news.stream()
                .map(newsEntity -> NewsPersistencePortResult.builder()
                        .id(newsEntity.getId())
                        .createdAt(newsEntity.getCreatedAt())
                        .lastModifiedAt(newsEntity.getLastModifiedAt())
                        .title(newsEntity.getTitle())
                        .text(newsEntity.getText())
                        .build())
                .collect(Collectors.toList());
    }

    public static NewsFilter createNewsFilter() {
        return NewsFilter.builder()
                .createdAtGte(LocalDateTime.now().minusDays(365))
                .createdAtLte(LocalDateTime.now())
                .titleContains("title")
                .textContains("content")
                .build();
    }

    public static NewsEntity toNewsEntity(UUID newsId) {
        return NewsEntity.builder()
                .id(newsId)
                .createdAt(LocalDateTime.now().minusDays(10))
                .lastModifiedAt(LocalDateTime.now())
                .title("News Title")
                .text("Content of news")
                .build();
    }

    public static NewsPersistencePortResult createNewsPersistencePortResult(
            NewsEntity news
    ) {
        return NewsPersistencePortResult.builder()
                .id(news.getId())
                .createdAt(news.getCreatedAt())
                .lastModifiedAt(news.getLastModifiedAt())
                .title(news.getTitle())
                .text(news.getText())
                .build();
    }

    public static UpdateNewsPersistencePortCommand createUpdateNewsPersistencePortCommand() {
        return UpdateNewsPersistencePortCommand.builder()
                .id(UUID.randomUUID())
                .createdAt(LocalDateTime.now().minusDays(10))
                .title("Updated news title")
                .text("Updated content of news")
                .build();
    }

    public static NewsEntity toNewsEntity(UpdateNewsPersistencePortCommand command) {
        return NewsEntity.builder()
                .id(command.id())
                .createdAt(command.createdAt())
                .lastModifiedAt(LocalDateTime.now().minusDays(1))
                .title(command.title())
                .text(command.text())
                .build();
    }

    public static NewsEntity createUpdatedNewsEntity(NewsEntity news) {
        return NewsEntity.builder()
                .id(news.getId())
                .createdAt(news.getCreatedAt())
                .lastModifiedAt(LocalDateTime.now())
                .title(news.getTitle())
                .text(news.getText())
                .build();
    }

    public static NewsPersistencePortResult toNewsPersistencePortResult(NewsEntity news) {
        return NewsPersistencePortResult.builder()
                .id(news.getId())
                .createdAt(news.getCreatedAt())
                .lastModifiedAt(LocalDateTime.now())
                .title(news.getTitle())
                .text(news.getText())
                .build();
    }

    public static NewsEntity generateNewsEntityForCreate() {
        return NewsEntity.builder()
                .title("News Title")
                .text("Content of news")
                .build();
    }

    public static NewsEntity getNewsEntityForRead() {
        return NewsEntity.builder()
                .id(UUID.fromString("b14e5f84-9d14-4477-a2a0-0bc2618f0469"))
                .createdAt(LocalDateTime.of(1991, 2, 3, 4, 5, 6))
                .lastModifiedAt(LocalDateTime.of(1991, 1, 2, 3, 4, 5))
                .title("News 1 Title")
                .text("Content of news 1")
                .build();
    }

    public static NewsEntity getNewsEntityForUpdate() {
        return NewsEntity.builder()
                .id(UUID.fromString("b14e5f84-9d14-4477-a2a0-0bc2618f0469"))
                .createdAt(LocalDateTime.of(1991, 2, 3, 4, 5, 6))
                .lastModifiedAt(LocalDateTime.of(1991, 2, 3, 4, 5, 6))
                .title("Updated news 1 title")
                .text("Updated content of news 1")
                .build();
    }

    public static Specification<NewsEntity> createNewsSpecification() {
        return Specification
                .where(NewsSpecification.createdAtGteSpec(LocalDateTime.of(1991, 2, 3, 4, 5, 6)))
                .and(NewsSpecification.createdAtLteSpec(LocalDateTime.of(1991, 2, 3, 4, 5, 6)))
                .and(NewsSpecification.titleContainsSpec("news"))
                .and(NewsSpecification.textContainsSpec("content"));
    }

}
