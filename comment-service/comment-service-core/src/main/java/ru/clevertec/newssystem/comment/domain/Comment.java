package ru.clevertec.newssystem.comment.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {

    private UUID id;

    private LocalDateTime createdAt;

    private LocalDateTime lastModifiedAt;

    private String text;

    private String username;

    private News news;

}
