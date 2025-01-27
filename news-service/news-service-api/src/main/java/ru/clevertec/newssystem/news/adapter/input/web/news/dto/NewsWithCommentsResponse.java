package ru.clevertec.newssystem.news.adapter.input.web.news.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import org.springframework.data.domain.Window;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record NewsWithCommentsResponse(

        UUID id,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime lastModifiedAt,

        String title,

        String text,

        Window<CommentResponse> comments

) {

}