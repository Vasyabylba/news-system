package ru.clevertec.newssystem.comment.port.input.comment.command;

import lombok.Builder;

@Builder
public record UpdateCommentUseCaseCommand(

        String text,

        String username

) {

}