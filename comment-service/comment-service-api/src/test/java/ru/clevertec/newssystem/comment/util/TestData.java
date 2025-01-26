package ru.clevertec.newssystem.comment.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Window;
import org.springframework.data.jpa.domain.Specification;
import ru.clevertec.newssystem.comment.adapter.input.web.comment.dto.CommentCreateRequest;
import ru.clevertec.newssystem.comment.adapter.input.web.comment.dto.CommentResponse;
import ru.clevertec.newssystem.comment.adapter.input.web.comment.dto.CommentUpdateRequest;
import ru.clevertec.newssystem.comment.adapter.output.client.news.dto.NewsResponse;
import ru.clevertec.newssystem.comment.adapter.output.persistence.jpa.entity.CommentEntity;
import ru.clevertec.newssystem.comment.adapter.output.persistence.jpa.specification.CommentSpecification;
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

    public static Window<ReadCommentUseCaseResult> createWindowOfReadCommentUseCaseResult(int count) {
        List<ReadCommentUseCaseResult> results = createReadCommentUseCaseResult(count);
        return Window.from(results, ScrollPosition.offset().positionFunction());
    }

    public static CommentFilter createCommentFilter() {
        return CommentFilter.builder()
                .createdAtGte(LocalDateTime.now().minusDays(10))
                .createdAtLte(LocalDateTime.now())
                .textContains("comment")
                .username("test_user")
                .usernameStarts("test")
                .build();
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

    public static Window<CommentResponse> createWindowOfCommentResponse(
            Window<ReadCommentUseCaseResult> readCommentUseCaseResults
    ) {
        return readCommentUseCaseResults.map(comment -> CommentResponse.builder()
                .id(comment.id())
                .createdAt(comment.createdAt())
                .lastModifiedAt(comment.lastModifiedAt())
                .text(comment.text())
                .username(comment.username())
                .build());
    }

    public static CommentCreateRequest createCommentCreateRequest() {
        return CommentCreateRequest.builder()
                .text("New comment")
                .username("test_user")
                .build();
    }

    public static CreateCommentUseCaseCommand toCreateCommentUseCaseCommand(CommentCreateRequest request) {
        return CreateCommentUseCaseCommand.builder()
                .text(request.text())
                .username(request.username())
                .build();
    }

    public static CreateCommentUseCaseResult createCreateCommentUseCaseResult(CreateCommentUseCaseCommand command) {
        return CreateCommentUseCaseResult.builder()
                .id(UUID.randomUUID())
                .createdAt(LocalDateTime.now())
                .lastModifiedAt(LocalDateTime.now())
                .text(command.text())
                .username(command.username())
                .build();
    }

    public static CommentUpdateRequest createCommentUpdateRequest() {
        return CommentUpdateRequest.builder()
                .text("Updated comment")
                .username("test_user")
                .build();
    }

    public static UpdateCommentUseCaseCommand toUpdateCommentUseCaseCommand(CommentUpdateRequest request) {
        return UpdateCommentUseCaseCommand.builder()
                .text(request.text())
                .username(request.username())
                .build();
    }

    public static UpdateCommentUseCaseResult createUpdateCommentUseCaseResult(UpdateCommentUseCaseCommand command) {
        return UpdateCommentUseCaseResult.builder()
                .id(UUID.randomUUID())
                .createdAt(LocalDateTime.now())
                .lastModifiedAt(LocalDateTime.now())
                .text(command.text())
                .username(command.username())
                .build();
    }

    public static CommentResponse toCommentResponse(ReadCommentUseCaseResult result) {
        return CommentResponse.builder()
                .id(result.id())
                .createdAt(result.createdAt())
                .lastModifiedAt(result.lastModifiedAt())
                .text(result.text())
                .username(result.username())
                .build();
    }

    public static CommentResponse toCommentResponse(CreateCommentUseCaseResult result) {
        return CommentResponse.builder()
                .id(result.id())
                .createdAt(result.createdAt())
                .lastModifiedAt(result.lastModifiedAt())
                .text(result.text())
                .username(result.username())
                .build();
    }

    public static CommentResponse toCommentResponse(UpdateCommentUseCaseResult result) {
        return CommentResponse.builder()
                .id(result.id())
                .createdAt(result.createdAt())
                .lastModifiedAt(result.lastModifiedAt())
                .text(result.text())
                .username(result.username())
                .build();
    }

    public static NewsResponse createNewsResponse(UUID newsId) {
        return NewsResponse.builder()
                .id(newsId)
                .createdAt(LocalDateTime.now().minusDays(10))
                .lastModifiedAt(LocalDateTime.now())
                .title("News 1 title")
                .text("Content of news 1")
                .build();
    }

    public static ReadNewsClientPortResult toReadNewsClientPortResult(NewsResponse response) {
        return ReadNewsClientPortResult.builder()
                .id(response.id())
                .createdAt(response.createdAt())
                .lastModifiedAt(response.lastModifiedAt())
                .title(response.title())
                .text(response.text())
                .build();
    }

    public static ReadNewsClientPortResult createReadNewsClientPortResult() {
        return ReadNewsClientPortResult.builder()
                .id(UUID.fromString("ccf130f4-e7eb-449c-abb9-66531f50af26"))
                .createdAt(LocalDateTime.of(2025, 1, 2, 3, 4, 5))
                .lastModifiedAt(LocalDateTime.of(2025, 1, 2, 3, 4, 5))
                .title("News 1 Title")
                .text("Content of news 1")
                .build();
    }

    public static CreateCommentPersistencePortCommand createCreateCommentPersistencePortCommand() {
        return CreateCommentPersistencePortCommand.builder()
                .text("This is a comment")
                .username("username123")
                .newsId(UUID.fromString("b14e5f84-9d14-4477-a2a0-0bc2618f0469"))
                .build();
    }

    public static CommentEntity toCommentEntity(CreateCommentPersistencePortCommand command) {
        return CommentEntity.builder()
                .text(command.text())
                .username(command.username())
                .newsId(command.newsId())
                .build();
    }

    public static CommentEntity createSavedCommentEntity(CommentEntity comment) {
        return CommentEntity.builder()
                .id(comment.getId())
                .createdAt(LocalDateTime.now())
                .lastModifiedAt(LocalDateTime.now())
                .text(comment.getText())
                .username(comment.getUsername())
                .newsId(comment.getNewsId())
                .build();
    }

    public static CommentPersistencePortResult toCreateCommentPersistencePortResult(CommentEntity comment) {
        return CommentPersistencePortResult.builder()
                .id(comment.getId())
                .createdAt(comment.getCreatedAt())
                .lastModifiedAt(comment.getLastModifiedAt())
                .text(comment.getText())
                .username(comment.getUsername())
                .newsId(comment.getNewsId())
                .build();
    }

    public static Pageable createUnsortedPageable(int pageNumber, int pageSize) {
        return PageRequest.of(pageNumber, pageSize);
    }

    public static Window<CommentEntity> createWindowOfCommentEntity(UUID newsId, Pageable pageable) {
        return Window.from(
                generateCommentEntityList(newsId, pageable.getPageSize()),
                pageable.toScrollPosition().positionFunction()
        );
    }

    public static List<CommentEntity> generateCommentEntityList(UUID newsId, int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> CommentEntity.builder()
                        .id(UUID.randomUUID())
                        .createdAt(LocalDateTime.now().minusDays(count + i - 1))
                        .lastModifiedAt(LocalDateTime.now())
                        .text("Comment " + (i + 1))
                        .username("Username" + (i + 1))
                        .newsId(newsId)
                        .build())
                .collect(Collectors.toList());
    }

    public static Window<CommentPersistencePortResult> createCommentPersistencePortResultWindow(
            List<CommentEntity> comments,
            Pageable pageable
    ) {
        return Window.from(
                generateReadCommentPersistencePortResultList(comments),
                pageable.toScrollPosition().positionFunction()
        );
    }

    private static List<CommentPersistencePortResult> generateReadCommentPersistencePortResultList(
            List<CommentEntity> comments
    ) {
        return comments.stream()
                .map(comment -> CommentPersistencePortResult.builder()
                        .id(comment.getId())
                        .createdAt(comment.getCreatedAt())
                        .lastModifiedAt(comment.getLastModifiedAt())
                        .text(comment.getText())
                        .username(comment.getUsername())
                        .newsId(comment.getNewsId())
                        .build())
                .collect(Collectors.toList());
    }

    public static CommentEntity toCommentEntity(UUID commentId, UUID newsId) {
        return CommentEntity.builder()
                .id(commentId)
                .createdAt(LocalDateTime.now().minusDays(10))
                .lastModifiedAt(LocalDateTime.now())
                .text("This is a comment")
                .username("username123")
                .newsId(newsId)
                .build();
    }

    public static CommentPersistencePortResult createCommentPersistencePortResult(
            CommentEntity comment
    ) {
        return CommentPersistencePortResult.builder()
                .id(comment.getId())
                .createdAt(comment.getCreatedAt())
                .lastModifiedAt(comment.getLastModifiedAt())
                .text(comment.getText())
                .username(comment.getUsername())
                .newsId(comment.getNewsId())
                .build();
    }

    public static UpdateCommentPersistencePortCommand createUpdateCommentPersistencePortCommand() {
        return UpdateCommentPersistencePortCommand.builder()
                .id(UUID.randomUUID())
                .createdAt(LocalDateTime.now().minusDays(10))
                .text("This is an updated comment")
                .username("new_username")
                .newsId(UUID.randomUUID())
                .build();
    }

    public static CommentEntity toCommentEntity(UpdateCommentPersistencePortCommand command) {
        return CommentEntity.builder()
                .id(command.id())
                .createdAt(command.createdAt())
                .lastModifiedAt(LocalDateTime.now().minusDays(1))
                .text(command.text())
                .username(command.username())
                .newsId(command.newsId())
                .build();
    }

    public static CommentEntity createUpdatedCommentEntity(CommentEntity comment) {
        return CommentEntity.builder()
                .id(comment.getId())
                .createdAt(comment.getCreatedAt())
                .lastModifiedAt(LocalDateTime.now())
                .text(comment.getText())
                .username(comment.getUsername())
                .newsId(comment.getNewsId())
                .build();
    }

    public static CommentPersistencePortResult toCommentPersistencePortResult(CommentEntity comment) {
        return CommentPersistencePortResult.builder()
                .id(comment.getId())
                .createdAt(comment.getCreatedAt())
                .lastModifiedAt(LocalDateTime.now())
                .text(comment.getText())
                .username(comment.getUsername())
                .newsId(comment.getNewsId())
                .build();
    }

    public static CommentEntity generateCommentEntityForCreate() {
        return CommentEntity.builder()
                .text("This is comment")
                .username("username123")
                .newsId(UUID.fromString("b14e5f84-9d14-4477-a2a0-0bc2618f0469"))
                .build();
    }

    public static CommentEntity getCommentEntityForRead() {
        return CommentEntity.builder()
                .id(UUID.fromString("f6eb2a7a-9a3f-49a0-b895-6019407b6a21"))
                .createdAt(LocalDateTime.of(1999, 1, 8, 4, 5, 6))
                .lastModifiedAt(LocalDateTime.of(1999, 1, 8, 4, 5, 6))
                .text("Comment number 1")
                .username("AnnaGrace")
                .newsId(UUID.fromString("035cd8a9-9707-41c6-8810-b1566cc49c7b"))
                .build();
    }

    public static CommentEntity getCommentEntityForUpdate() {
        return CommentEntity.builder()
                .id(UUID.fromString("f6eb2a7a-9a3f-49a0-b895-6019407b6a21"))
                .createdAt(LocalDateTime.of(1999, 1, 8, 4, 5, 6))
                .lastModifiedAt(LocalDateTime.of(1999, 1, 8, 4, 5, 6))
                .text("New text of comment number 1")
                .username("new_user123")
                .newsId(UUID.fromString("035cd8a9-9707-41c6-8810-b1566cc49c7b"))
                .build();
    }

    public static CommentEntity getCommentEntityForDelete() {
        return CommentEntity.builder()
                .id(UUID.fromString("f6eb2a7a-9a3f-49a0-b895-6019407b6a21"))
                .createdAt(LocalDateTime.of(1999, 1, 8, 4, 5, 6))
                .lastModifiedAt(LocalDateTime.of(1999, 1, 8, 4, 5, 6))
                .text("New text of comment number 1")
                .username("new_user123")
                .newsId(UUID.fromString("035cd8a9-9707-41c6-8810-b1566cc49c7b"))
                .build();
    }

    public static Specification<CommentEntity> createCommentSpecification() {
        return Specification
                .where(CommentSpecification.newsIdSpec(UUID.fromString("035cd8a9-9707-41c6-8810-b1566cc49c7b")))
                .and(CommentSpecification.createdAtGteSpec(LocalDateTime.of(1999, 1, 8, 4, 5, 6)))
                .and(CommentSpecification.createdAtLteSpec(LocalDateTime.of(1999, 1, 8, 4, 5, 6)))
                .and(CommentSpecification.textContainsSpec("comment")
                        .and(CommentSpecification.usernameStartsSpec("User")));
    }

}
