package ru.clevertec.newssystem.news.adapter.output.client.comment.adapter;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Window;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;
import ru.clevertec.newssystem.news.adapter.output.client.comment.ClientCommentMapper;
import ru.clevertec.newssystem.news.adapter.output.client.comment.dto.CommentResponse;
import ru.clevertec.newssystem.news.port.output.client.result.ReadCommentClientPortResult;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@EnableWireMock({
        @ConfigureWireMock(
                name = "comment-service"
        )
})
@RequiredArgsConstructor
class ReadCommentClientAdapterIntegrationTest {

    public static final String COMMENTS_URL_TEMPLATE = "/api/v1/news/%s/comments";

    public static final String CONTENT_TYPE = "Content-Type";

    public static final int OK_STATUS = 200;

    public static final String COMMENTS_RESPONSE_OK_JSON = "commentsResponse#200.json";

    public static final String EMPTY_RESPONSE_200_JSON = "emptyResponse#200.json";

    @InjectWireMock("comment-service")
    private WireMockServer mockCommentService;

    @MockitoBean
    private ClientCommentMapper clientCommentMapper;

    @Autowired
    private ReadCommentClientAdapter readCommentClientAdapter;

    @Test
    void shouldReadCommentsByNews() {
        // given
        UUID newsId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);

        mockCommentService.stubFor(
                WireMock.get(urlPathEqualTo(String.format(COMMENTS_URL_TEMPLATE, newsId)))
                        .willReturn(aResponse()
                                .withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withStatus(OK_STATUS)
                                .withBodyFile(COMMENTS_RESPONSE_OK_JSON)
                        )
        );

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
                .hasFieldOrPropertyWithValue("id", UUID.fromString("6242f62e-4b2f-48ea-9479-a5ebb78104e4"))
                .hasFieldOrPropertyWithValue("createdAt", LocalDateTime.of(2025, 1, 2, 13, 29, 26))
                .hasFieldOrPropertyWithValue("lastModifiedAt", LocalDateTime.of(2025, 1, 2, 13, 29, 26))
                .hasFieldOrPropertyWithValue("text", "I really enjoyed this article. Thanks!")
                .hasFieldOrPropertyWithValue("username", "SophiaRain");
    }

    @Test
    void shouldReturnEmptyWindow_whenCommentClientResponseIsNull() {
        // given
        UUID newsId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);

        mockCommentService.stubFor(
                WireMock.get(urlPathEqualTo(String.format(COMMENTS_URL_TEMPLATE, newsId)))
                        .willReturn(aResponse()
                                .withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withStatus(OK_STATUS)
                                .withBodyFile(EMPTY_RESPONSE_200_JSON)
                        )
        );

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
        assertThat(actual.getContent()).isEmpty();
    }

}