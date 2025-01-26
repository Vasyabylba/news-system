package ru.clevertec.newssystem.comment.adapter.output.client.news.adapter;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;
import ru.clevertec.newssystem.comment.adapter.output.client.news.ClientNewsMapper;
import ru.clevertec.newssystem.comment.adapter.output.client.news.dto.NewsResponse;
import ru.clevertec.newssystem.comment.exception.NewsNotFoundException;
import ru.clevertec.newssystem.comment.port.output.client.result.ReadNewsClientPortResult;
import ru.clevertec.newssystem.comment.util.TestData;

import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@EnableWireMock({
        @ConfigureWireMock(
                name = "news-service"
        )
})
@RequiredArgsConstructor
class ReadNewsClientAdapterIntegrationTest {

    public static final String NEWS_WITH_ID_NOT_FOUND = "News with id '%s' not found";

    public static final String NEWS_RESPONSE_OK_JSON = "newsResponse#200.json";

    public static final String UNPROCESSABLE_ENTITY_JSON = "newsResponse#422.json";

    public static final String EMPTY_RESPONSE_200_JSON = "emptyResponse#200.json";

    public static final int OK_STATUS = 200;

    public static final String CONTENT_TYPE = "Content-Type";

    public static final String NEWS_URL = "/api/v1/news/";

    private static final int UNPROCESSABLE_ENTITY_STATUS = 422;

    @MockitoBean
    private ClientNewsMapper newsMapper;

    @Autowired
    private ReadNewsClientAdapter readNewsClientAdapter;

    @InjectWireMock("news-service")
    private WireMockServer mockNewsService;

    @Test
    void shouldReadNews_whenNewsIsExists() {
        //given
        UUID newsId = UUID.fromString("ccf130f4-e7eb-449c-abb9-66531f50af26");
        ReadNewsClientPortResult expected = TestData.createReadNewsClientPortResult();

        mockNewsService.stubFor(
                WireMock.get(urlPathEqualTo(NEWS_URL + newsId))
                        .willReturn(aResponse()
                                .withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withStatus(OK_STATUS)
                                .withBodyFile(NEWS_RESPONSE_OK_JSON)
                        )
        );

        when(newsMapper.toReadNewsClientPortResult(any(NewsResponse.class)))
                .thenAnswer(invocation -> {
                    NewsResponse input = invocation.getArgument(0);
                    return ReadNewsClientPortResult.builder()
                            .id(input.id())
                            .createdAt(input.createdAt())
                            .lastModifiedAt(input.lastModifiedAt())
                            .title(input.title())
                            .text(input.text())
                            .build();
                });

        //when
        ReadNewsClientPortResult actual = readNewsClientAdapter.readNews(newsId);

        //then
        assertThat(actual)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", expected.id())
                .hasFieldOrPropertyWithValue("createdAt", expected.createdAt())
                .hasFieldOrPropertyWithValue("lastModifiedAt", expected.lastModifiedAt())
                .hasFieldOrPropertyWithValue("title", expected.title())
                .hasFieldOrPropertyWithValue("text", expected.text());

        verify(newsMapper, times(1)).toReadNewsClientPortResult(any(NewsResponse.class));
    }

    @Test
    void shouldThrowNewsNotFoundException_whenNewsNotFound() {
        //given
        UUID newsId = UUID.fromString("ccf130f4-e7eb-449c-abb9-66531f50af22");

        mockNewsService.stubFor(
                WireMock.get(urlPathEqualTo(NEWS_URL + newsId))
                        .willReturn(aResponse()
                                .withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withStatus(UNPROCESSABLE_ENTITY_STATUS)
                                .withBodyFile(UNPROCESSABLE_ENTITY_JSON)
                        )
        );

        // when, then
        NewsNotFoundException expected = assertThrows(
                NewsNotFoundException.class,
                () -> readNewsClientAdapter.readNews(newsId)
        );

        Assertions.assertThat(expected)
                .hasMessageContaining(newsId.toString());

        verifyNoInteractions(newsMapper);
    }

    @Test
    void shouldReadNews_whenNewsClientResponseIsEmpty() {
        //given
        UUID newsId = UUID.fromString("ccf130f4-e7eb-449c-abb9-66531f50af26");

        mockNewsService.stubFor(
                WireMock.get(urlPathEqualTo(NEWS_URL + newsId))
                        .willReturn(aResponse()
                                .withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withStatus(OK_STATUS)
                                .withBodyFile(EMPTY_RESPONSE_200_JSON)
                        )
        );

        //when
        NewsNotFoundException actual = assertThrows(
                NewsNotFoundException.class,
                () -> readNewsClientAdapter.readNews(newsId)
        );

        //then
        assertThat(actual)
                .hasMessageContaining(String.format(NEWS_WITH_ID_NOT_FOUND, newsId));

        verifyNoInteractions(newsMapper);
    }

}
