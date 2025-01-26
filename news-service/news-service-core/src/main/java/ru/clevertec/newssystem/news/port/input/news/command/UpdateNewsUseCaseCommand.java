package ru.clevertec.newssystem.news.port.input.news.command;

import lombok.Builder;

@Builder
public record UpdateNewsUseCaseCommand(

        String title,

        String text

) {

}