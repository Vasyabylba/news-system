package ru.clevertec.newssystem.news.adapter.input.web.news.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record NewsCreateRequest(

        @NotBlank
        String title,

        String text

) {

}