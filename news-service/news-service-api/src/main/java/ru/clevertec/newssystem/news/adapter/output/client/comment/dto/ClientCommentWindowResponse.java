package ru.clevertec.newssystem.news.adapter.output.client.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientCommentWindowResponse {

    private boolean empty;

    private List<CommentResponse> content;

    private boolean last;

}
