package ru.clevertec.newssystem.news.filter;

import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Builder
public record NewsFilter(

        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss", iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime createdAtGte,

        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss", iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime createdAtLte,

        String titleContains,

        String textContains

) {

}