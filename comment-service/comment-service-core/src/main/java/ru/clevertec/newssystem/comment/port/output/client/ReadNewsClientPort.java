package ru.clevertec.newssystem.comment.port.output.client;

import ru.clevertec.newssystem.comment.port.output.client.result.ReadNewsClientPortResult;

import java.util.UUID;

public interface ReadNewsClientPort {

    ReadNewsClientPortResult readNews(UUID newsId);

}
