package ru.clevertec.newssystem.comment.port.output.persistence.command;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CreateCommentPersistencePortCommand(

        String text,

        String username,

        UUID newsId

) {

}

