package ru.clevertec.newssystem.comment.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Window;
import ru.clevertec.newssystem.comment.domain.Comment;
import ru.clevertec.newssystem.comment.domain.News;
import ru.clevertec.newssystem.comment.filter.CommentFilter;
import ru.clevertec.newssystem.comment.port.input.comment.command.CreateCommentUseCaseCommand;
import ru.clevertec.newssystem.comment.port.input.comment.command.UpdateCommentUseCaseCommand;
import ru.clevertec.newssystem.comment.port.input.comment.result.CreateCommentUseCaseResult;
import ru.clevertec.newssystem.comment.port.input.comment.result.ReadCommentUseCaseResult;
import ru.clevertec.newssystem.comment.port.input.comment.result.UpdateCommentUseCaseResult;
import ru.clevertec.newssystem.comment.port.output.client.result.ReadNewsClientPortResult;
import ru.clevertec.newssystem.comment.port.output.persistence.command.CreateCommentPersistencePortCommand;
import ru.clevertec.newssystem.comment.port.output.persistence.command.UpdateCommentPersistencePortCommand;
import ru.clevertec.newssystem.comment.port.output.persistence.result.CommentPersistencePortResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TestData {

    public static CreateCommentUseCaseCommand createCreateCommentUseCaseCommand() {
        return CreateCommentUseCaseCommand.builder()
                .text("This is a comment")
                .username("username123")
                .build();
    }

    public static ReadNewsClientPortResult createReadNewsClientPortResult(UUID newsId) {
        return ReadNewsClientPortResult.builder()
                .id(newsId)
                .createdAt(LocalDateTime.of(2025, 1, 1, 0, 0))
                .lastModifiedAt(LocalDateTime.of(2025, 1, 3, 0, 0))
                .title("This is a news")
                .text("This is the content of news")
                .build();
    }

    public static Comment createCreatingComment(UUID newsId, CreateCommentUseCaseCommand command) {
        return Comment.builder()
                .text(command.text())
                .username(command.username())
                .news(News.builder()
                        .id(newsId)
                        .build())
                .build();
    }

    public static CreateCommentPersistencePortCommand createCreateCommentPersistencePortCommand(Comment comment) {
        return CreateCommentPersistencePortCommand.builder()
                .text(comment.getText())
                .username(comment.getUsername())
                .newsId(comment.getNews().getId())
                .build();
    }

    public static CommentPersistencePortResult createCreateCommentPersistencePortResult(
            CreateCommentPersistencePortCommand command) {
        return CommentPersistencePortResult.builder()
                .id(UUID.randomUUID())
                .createdAt(LocalDateTime.now())
                .lastModifiedAt(LocalDateTime.now())
                .text(command.text())
                .username(command.username())
                .newsId(command.newsId())
                .build();
    }

    public static CreateCommentUseCaseResult createCreateCommentUseCaseResult(Comment comment) {
        return CreateCommentUseCaseResult.builder()
                .id(comment.getId())
                .createdAt(comment.getCreatedAt())
                .lastModifiedAt(comment.getLastModifiedAt())
                .text(comment.getText())
                .username(comment.getUsername())
                .build();
    }

    public static CommentFilter createCommentFilter() {
        return CommentFilter.builder()
                .createdAtGte(LocalDateTime.of(2025, 1, 1, 0, 0))
                .createdAtLte(LocalDateTime.now())
                .textContains("comment")
                .usernameStarts("username")
                .build();
    }

    public static Pageable createUnsortedPageable(int pageNumber, int pageSize) {
        return PageRequest.of(pageNumber, pageSize);
    }

    public static Window<CommentPersistencePortResult> createWindowCommentPersistencePortResult(
            UUID newsId,
            Pageable pageable
    ) {
        return Window.from(
                generateCommentPersistencePortResults(newsId, pageable.getPageSize()),
                pageable.toScrollPosition().positionFunction()
        );
    }

    public static List<CommentPersistencePortResult> generateCommentPersistencePortResults(
            UUID newsId,
            int count
    ) {
        return IntStream.range(0, count)
                .mapToObj(i -> CommentPersistencePortResult.builder()
                        .id(UUID.randomUUID())
                        .createdAt(LocalDateTime.now().minusDays(count + i - 1))
                        .lastModifiedAt(LocalDateTime.now())
                        .text("Comment " + (i + 1))
                        .username("Username" + (i + 1))
                        .newsId(newsId)
                        .build())
                .collect(Collectors.toList());
    }

    public static Window<ReadCommentUseCaseResult> createWindowReadCommentUseCaseResult(
            List<CommentPersistencePortResult> commentPersistencePortResults,
            Pageable pageable
    ) {
        return Window.from(
                generateReadCommentUseCaseResults(commentPersistencePortResults),
                pageable.toScrollPosition().positionFunction()
        );
    }

    public static List<ReadCommentUseCaseResult> generateReadCommentUseCaseResults(
            List<CommentPersistencePortResult> commentPersistencePortResults
    ) {
        return generateComments(commentPersistencePortResults).stream()
                .map(comment -> ReadCommentUseCaseResult.builder()
                        .id(comment.getId())
                        .text(comment.getText())
                        .username(comment.getUsername())
                        .createdAt(comment.getCreatedAt())
                        .lastModifiedAt(comment.getLastModifiedAt())
                        .build())
                .collect(Collectors.toList());
    }

    public static List<Comment> generateComments(List<CommentPersistencePortResult> persistenceResultList) {
        return persistenceResultList.stream()
                .map(persistenceResult -> Comment.builder()
                        .id(persistenceResult.id())
                        .createdAt(persistenceResult.createdAt())
                        .lastModifiedAt(persistenceResult.lastModifiedAt())
                        .text(persistenceResult.text())
                        .username(persistenceResult.username())
                        .news(News.builder().id(persistenceResult.newsId()).build())
                        .build())
                .collect(Collectors.toList());
    }

    public static CommentPersistencePortResult createCommentPersistencePortResult(UUID newsId, UUID commentId) {
        return CommentPersistencePortResult.builder()
                .id(commentId)
                .createdAt(LocalDateTime.of(2025, 1, 1, 0, 0))
                .lastModifiedAt(LocalDateTime.now())
                .text("This is a comment")
                .username("username123")
                .newsId(newsId)
                .build();
    }

    public static Comment createComment(CommentPersistencePortResult portResult) {
        return Comment.builder()
                .id(portResult.id())
                .createdAt(portResult.createdAt())
                .lastModifiedAt(portResult.lastModifiedAt())
                .text(portResult.text())
                .username(portResult.username())
                .news(News.builder()
                        .id(portResult.newsId())
                        .build())
                .build();
    }

    public static ReadCommentUseCaseResult createReadCommentUseCaseResult(Comment comment) {
        return ReadCommentUseCaseResult.builder()
                .id(comment.getId())
                .createdAt(comment.getCreatedAt())
                .lastModifiedAt(comment.getLastModifiedAt())
                .text(comment.getText())
                .username(comment.getUsername())
                .build();
    }

    public static UpdateCommentUseCaseCommand createUpdateCommentUseCaseCommand() {
        return UpdateCommentUseCaseCommand.builder()
                .text("This is an updated comment")
                .username("new_username")
                .build();
    }

    public static Comment toUpdatingComment(UpdateCommentUseCaseCommand command, Comment comment) {
        comment.setText(command.text());
        comment.setUsername(command.username());
        return comment;
    }

    public static UpdateCommentPersistencePortCommand createUpdateCommentPersistencePortCommand(Comment comment) {
        return UpdateCommentPersistencePortCommand.builder()
                .id(comment.getId())
                .createdAt(comment.getCreatedAt())
                .text(comment.getText())
                .username(comment.getUsername())
                .newsId(comment.getNews().getId())
                .build();
    }

    public static CommentPersistencePortResult createCommentPersistencePortResult(
            UpdateCommentPersistencePortCommand command
    ) {
        return CommentPersistencePortResult.builder()
                .id(command.id())
                .createdAt(command.createdAt())
                .lastModifiedAt(LocalDateTime.now())
                .text(command.text())
                .username(command.username())
                .newsId(command.newsId())
                .build();
    }

    public static Comment createUpdatedComment(CommentPersistencePortResult portResult) {
        return Comment.builder()
                .id(portResult.id())
                .createdAt(portResult.createdAt())
                .lastModifiedAt(portResult.lastModifiedAt())
                .text(portResult.text())
                .username(portResult.username())
                .news(News.builder()
                        .id(portResult.newsId())
                        .build())
                .build();
    }

    public static UpdateCommentUseCaseResult createUpdateCommentUseCaseResult(Comment comment) {
        return UpdateCommentUseCaseResult.builder()
                .id(comment.getId())
                .createdAt(comment.getCreatedAt())
                .lastModifiedAt(comment.getLastModifiedAt())
                .text(comment.getText())
                .username(comment.getUsername())
                .build();
    }

}
