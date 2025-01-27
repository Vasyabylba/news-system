package ru.clevertec.newssystem.comment.port.input.comment.command;

import lombok.Builder;

@Builder
public record CreateCommentUseCaseCommand(

        String text,

        String username

) {

}