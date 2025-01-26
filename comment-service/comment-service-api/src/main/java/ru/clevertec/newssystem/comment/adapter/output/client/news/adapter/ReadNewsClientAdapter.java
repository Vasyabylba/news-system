package ru.clevertec.newssystem.comment.adapter.output.client.news.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.clevertec.newssystem.comment.adapter.output.client.news.ClientNewsMapper;
import ru.clevertec.newssystem.comment.adapter.output.client.news.NewsClient;
import ru.clevertec.newssystem.comment.adapter.output.client.news.dto.NewsResponse;
import ru.clevertec.newssystem.comment.exception.NewsNotFoundException;
import ru.clevertec.newssystem.comment.port.output.client.ReadNewsClientPort;
import ru.clevertec.newssystem.comment.port.output.client.result.ReadNewsClientPortResult;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ReadNewsClientAdapter implements ReadNewsClientPort {

    private final NewsClient newsClient;

    private final ClientNewsMapper newsMapper;

    @Override
    public ReadNewsClientPortResult readNews(UUID newsId) {
        NewsResponse newsResponse = newsClient.getNews(newsId).getBody();

        if (newsResponse == null) {
            throw NewsNotFoundException.byNewsId(newsId);
        }

        return newsMapper.toReadNewsClientPortResult(newsResponse);
    }

}
