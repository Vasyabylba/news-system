package ru.clevertec.newssystem.comment.port.output.persistence.result;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class",
        defaultImpl = CommentPersistencePortResult.class
)
public record CommentPersistencePortResult(

        UUID id,

        LocalDateTime createdAt,

        LocalDateTime lastModifiedAt,

        String text,

        String username,

        UUID newsId

) {

}
