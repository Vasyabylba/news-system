package ru.clevertec.newssystem.news.port.input.news.command;

import lombok.Builder;

@Builder
public record CreateNewsUseCaseCommand(

        String title,

        String text

) {

}