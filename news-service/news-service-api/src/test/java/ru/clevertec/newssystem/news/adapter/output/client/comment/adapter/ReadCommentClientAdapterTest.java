package ru.clevertec.newssystem.news.adapter.output.client.comment.adapter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Window;
import org.springframework.http.ResponseEntity;
import ru.clevertec.newssystem.news.adapter.output.client.comment.ClientCommentMapper;
import ru.clevertec.newssystem.news.adapter.output.client.comment.CommentClient;
import ru.clevertec.newssystem.news.adapter.output.client.comment.dto.ClientCommentWindowResponse;
import ru.clevertec.newssystem.news.adapter.output.client.comment.dto.CommentResponse;
import ru.clevertec.newssystem.news.port.output.client.result.ReadCommentClientPortResult;
import ru.clevertec.newssystem.news.util.TestData;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReadCommentClientAdapterTest {

    @Mock
    private CommentClient commentClient;

    @Mock
    private ClientCommentMapper clientCommentMapper;

    @InjectMocks
    private ReadCommentClientAdapter readCommentClientAdapter;

    @Test
    void shouldReadCommentsByNews() {
        // given
        UUID newsId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);
        ClientCommentWindowResponse clientCommentWindowResponse =
                TestData.createClientCommentWindowResponse(pageable.getPageSize());
        ReadCommentClientPortResult expected = TestData.createReadCommentClientPortResult();

        when(commentClient.getCommentsByNews(newsId, pageable))
                .thenReturn(ResponseEntity.ok(clientCommentWindowResponse));
        when(clientCommentMapper.toReadCommentClientPortResult(any(CommentResponse.class)))
                .thenAnswer(invocation -> {
                    CommentResponse input = invocation.getArgument(0);
                    return ReadCommentClientPortResult.builder()
                            .id(input.id())
                            .createdAt(input.createdAt())
                            .lastModifiedAt(input.lastModifiedAt())
                            .text(input.text())
                            .username(input.username())
                            .build();
                });

        // when
        Window<ReadCommentClientPortResult> actual =
                readCommentClientAdapter.readCommentsByNews(newsId, pageable);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.getContent()).hasSize(10);
        assertThat(actual.getContent().getFirst())
                .hasFieldOrPropertyWithValue("text", expected.text())
                .hasFieldOrPropertyWithValue("username", expected.username());

        verify(commentClient, times(1))
                .getCommentsByNews(newsId, pageable);
        verify(clientCommentMapper, times(10))
                .toReadCommentClientPortResult(any(CommentResponse.class));
    }

}