package ru.clevertec.newssystem.news.port.output.persistence.result;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class",
        defaultImpl = NewsPersistencePortResult.class
)
public record NewsPersistencePortResult(

        UUID id,

        LocalDateTime createdAt,

        LocalDateTime lastModifiedAt,

        String title,

        String text

) implements Serializable {

}
