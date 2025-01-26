package ru.clevertec.newssystem.news.port.output.persistence.command;

import lombok.Builder;

@Builder
public record CreateNewsPersistencePortCommand(

        String title,

        String text

) {

}

