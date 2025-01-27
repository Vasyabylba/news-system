package ru.clevertec.newssystem.news.adapter.output.client.comment.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Window;
import org.springframework.stereotype.Component;
import ru.clevertec.newssystem.news.adapter.output.client.comment.ClientCommentMapper;
import ru.clevertec.newssystem.news.adapter.output.client.comment.CommentClient;
import ru.clevertec.newssystem.news.adapter.output.client.comment.dto.ClientCommentWindowResponse;
import ru.clevertec.newssystem.news.adapter.output.client.comment.dto.CommentResponse;
import ru.clevertec.newssystem.news.port.output.client.ReadCommentClientPort;
import ru.clevertec.newssystem.news.port.output.client.result.ReadCommentClientPortResult;

import java.util.Collections;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ReadCommentClientAdapter implements ReadCommentClientPort {

    private final CommentClient commentClient;

    private final ClientCommentMapper clientCommentMapper;

    @Override
    public Window<ReadCommentClientPortResult> readCommentsByNews(UUID newsId, Pageable pageable) {
        ClientCommentWindowResponse clientCommentWindowResponse =
                commentClient.getCommentsByNews(newsId, pageable).getBody();

        Window<CommentResponse> commentsByNews = createWindow(pageable, clientCommentWindowResponse);

        return commentsByNews.map(clientCommentMapper::toReadCommentClientPortResult);
    }

    private Window<CommentResponse> createWindow(Pageable pageable,
                                                 ClientCommentWindowResponse response) {
        if (response == null) {
            return Window.from(Collections.emptyList(), pageable.toScrollPosition().positionFunction());
        }

        return Window.from(
                response.getContent(),
                pageable.toScrollPosition().positionFunction(),
                !response.isLast()
        );
    }

}
