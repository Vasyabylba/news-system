package ru.clevertec.newssystem.comment.adapter.input.web.comment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record CommentUpdateRequest(

        @NotBlank
        String text,

        @NotBlank
        String username

) {

}
