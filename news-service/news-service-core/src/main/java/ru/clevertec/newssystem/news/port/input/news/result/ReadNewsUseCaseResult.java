package ru.clevertec.newssystem.news.port.input.news.result;

import lombok.Builder;
import org.springframework.data.domain.Window;
import ru.clevertec.newssystem.news.port.input.comment.result.ReadCommentUseCaseResult;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ReadNewsUseCaseResult(

        UUID id,

        LocalDateTime createdAt,

        LocalDateTime lastModifiedAt,

        String title,

        String text,

        Window<ReadCommentUseCaseResult> comments

) {


}
