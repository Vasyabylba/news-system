package ru.clevertec.newssystem.news.port.input.news.result;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record CreateNewsUseCaseResult(

        UUID id,

        LocalDateTime createdAt,

        LocalDateTime lastModifiedAt,

        String title,

        String text

) {

}