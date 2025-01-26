package ru.clevertec.newssystem.news.port.input.comment.result;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ReadCommentUseCaseResult(
        UUID id,

        LocalDateTime createdAt,

        LocalDateTime lastModifiedAt,

        String text,

        String username
) {

}
