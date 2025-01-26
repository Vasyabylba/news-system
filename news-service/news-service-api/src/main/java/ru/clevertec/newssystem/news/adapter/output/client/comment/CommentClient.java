package ru.clevertec.newssystem.news.adapter.output.client.comment;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.clevertec.newssystem.news.adapter.output.client.comment.dto.ClientCommentWindowResponse;

import java.util.UUID;

@FeignClient(value = "comment-service", url = "${external-api.comment-service.url}")
public interface CommentClient {

    @GetMapping("/news/{newsId}/comments")
    ResponseEntity<ClientCommentWindowResponse> getCommentsByNews(@PathVariable UUID newsId, Pageable pageable);

}
