package ru.clevertec.newssystem.comment.adapter.output.client.news;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.clevertec.newssystem.comment.adapter.output.client.news.configuration.NewsFeignClientConfiguration;
import ru.clevertec.newssystem.comment.adapter.output.client.news.dto.NewsResponse;

import java.util.UUID;

@FeignClient(value = "news-service",
             url = "${external-api.news-service.url}",
             configuration = NewsFeignClientConfiguration.class)
public interface NewsClient {

    @GetMapping("/news/{newsId}")
    ResponseEntity<NewsResponse> getNews(@PathVariable UUID newsId);

}
