package ru.clevertec.newssystem.comment.port.input.comment.result;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record UpdateCommentUseCaseResult(

        UUID id,

        LocalDateTime createdAt,

        LocalDateTime lastModifiedAt,

        String text,

        String username

) {

}
