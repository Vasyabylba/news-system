package ru.clevertec.newssystem.comment.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class News {

    private UUID id;

    private LocalDateTime createdAt;

    private LocalDateTime lastModifiedAt;

    private String title;

    private String text;

    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

}
