package ru.clevertec.newssystem.news.port.output.client.result;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder

public record ReadCommentClientPortResult(
        UUID id,

        LocalDateTime createdAt,

        LocalDateTime lastModifiedAt,

        String text,

        String username
) {

}
