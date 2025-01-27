package ru.clevertec.newssystem.news.port.output.persistence.command;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record UpdateNewsPersistencePortCommand(

        UUID id,

        LocalDateTime createdAt,

        LocalDateTime lastModifiedAt,

        String title,

        String text

) {

}
