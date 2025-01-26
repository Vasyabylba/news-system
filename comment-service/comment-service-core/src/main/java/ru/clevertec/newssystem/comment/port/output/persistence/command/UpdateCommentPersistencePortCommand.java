package ru.clevertec.newssystem.comment.port.output.persistence.command;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record UpdateCommentPersistencePortCommand(

        UUID id,

        LocalDateTime createdAt,

        String text,

        String username,

        UUID newsId

) {

}
