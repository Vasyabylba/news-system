package ru.clevertec.newssystem.comment.filter;

import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Builder
public record CommentFilter(

        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss", iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime createdAtGte,

        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss", iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime createdAtLte,

        String textContains,

        String username,

        String usernameStarts

) {

}