package ru.clevertec.newssystem.comment.adapter.input.web.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;
import ru.clevertec.newssystem.comment.adapter.input.web.comment.dto.CommentCreateRequest;
import ru.clevertec.newssystem.comment.adapter.input.web.comment.dto.CommentUpdateRequest;
import ru.clevertec.newssystem.comment.util.TestData;

import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@EnableWireMock({
        @ConfigureWireMock(
                name = "news-service"
        )
})
@AutoConfigureMockMvc
class CommentControllerIntegrationTest {

    public static final String NEWS_RESPONSE_OK_JSON = "newsResponse#200.json";

    public static final String UNPROCESSABLE_ENTITY_JSON = "newsResponse#422.json";

    public static final int OK_STATUS = 200;

    public static final String CONTENT_TYPE = "Content-Type";

    public static final String NEWS_URL_TEMPLATE = "/api/v1/news/%s";

    private static final int UNPROCESSABLE_ENTITY_STATUS = 422;

    @InjectWireMock("news-service")
    private WireMockServer mockNewsService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    class GetComments {

        @Test
        void shouldGetAllCommentsAndReturnStatus200() throws Exception {
            // given
            UUID newsId = UUID.fromString("ccf130f4-e7eb-449c-abb9-66531f50af26");
            Pageable pageable = PageRequest.of(0, 10);

            mockNewsService.stubFor(
                    WireMock.get(urlPathEqualTo(String.format(NEWS_URL_TEMPLATE, newsId)))
                            .willReturn(aResponse()
                                    .withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                    .withStatus(OK_STATUS)
                                    .withBodyFile(NEWS_RESPONSE_OK_JSON)
                            )
            );

            // when & then
            mockMvc.perform(get("/api/v1/news/{newsId}/comments", newsId)
                            .param("page", String.valueOf(pageable.getPageNumber()))
                            .param("size", String.valueOf(pageable.getPageSize())))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content.length()").value(10));
        }

        @Test
        void shouldReturnStatus422_whenNewsIsNotExists() throws Exception {
            // given
            UUID newsId = UUID.fromString("ccf130f4-e7eb-449c-abb9-66531f50af22");
            Pageable pageable = PageRequest.of(0, 10);

            mockNewsService.stubFor(
                    WireMock.get(urlPathEqualTo(String.format(NEWS_URL_TEMPLATE, newsId)))
                            .willReturn(aResponse()
                                    .withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                    .withStatus(UNPROCESSABLE_ENTITY_STATUS)
                                    .withBodyFile(UNPROCESSABLE_ENTITY_JSON)
                            )
            );

            // when & then
            mockMvc.perform(get("/api/v1/news/{newsId}/comments", newsId)
                            .param("page", String.valueOf(pageable.getPageNumber()))
                            .param("size", String.valueOf(pageable.getPageSize())))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }

    }

    @Nested
    class GetComment {

        @Test
        void shouldGetCommentAndReturnStatus200() throws Exception {
            // given
            UUID newsId = UUID.fromString("ccf130f4-e7eb-449c-abb9-66531f50af26");
            UUID commentId = UUID.fromString("190d4868-df59-4bc1-84b0-7a7d8fc8cd62");

            mockNewsService.stubFor(
                    WireMock.get(urlPathEqualTo(String.format(NEWS_URL_TEMPLATE, newsId)))
                            .willReturn(aResponse()
                                    .withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                    .withStatus(OK_STATUS)
                                    .withBodyFile(NEWS_RESPONSE_OK_JSON)
                            )
            );

            // when & then
            mockMvc.perform(get("/api/v1/news/{newsId}/comments/{commentId}", newsId, commentId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(commentId.toString()));
        }

        @Test
        void shouldReturnStatus422_whenCommentNotExists() throws Exception {
            // given
            UUID newsId = UUID.fromString("ccf130f4-e7eb-449c-abb9-66531f50af26");
            UUID commentId = UUID.fromString("190d4868-df59-4bc1-84b0-7a7d8fc8cd60");

            mockNewsService.stubFor(
                    WireMock.get(urlPathEqualTo(String.format(NEWS_URL_TEMPLATE, newsId)))
                            .willReturn(aResponse()
                                    .withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                    .withStatus(OK_STATUS)
                                    .withBodyFile(NEWS_RESPONSE_OK_JSON)
                            )
            );

            // when & then
            mockMvc.perform(get("/api/v1/news/{newsId}/comments/{commentId}", newsId, commentId))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message")
                            .value("Comment with id '190d4868-df59-4bc1-84b0-7a7d8fc8cd60' not found"));
        }

        @Test
        void shouldReturnStatus422_whenNewsIsNotExists() throws Exception {
            // given
            UUID newsId = UUID.fromString("ccf130f4-e7eb-449c-abb9-66531f50af22");
            UUID commentId = UUID.fromString("190d4868-df59-4bc1-84b0-7a7d8fc8cd62");

            mockNewsService.stubFor(
                    WireMock.get(urlPathEqualTo(String.format(NEWS_URL_TEMPLATE, newsId)))
                            .willReturn(aResponse()
                                    .withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                    .withStatus(UNPROCESSABLE_ENTITY_STATUS)
                                    .withBodyFile(UNPROCESSABLE_ENTITY_JSON)
                            )
            );

            // when & then
            mockMvc.perform(get("/api/v1/news/{newsId}/comments/{commentId}", newsId, commentId))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message")
                            .value("News with id 'ccf130f4-e7eb-449c-abb9-66531f50af22' not found"));
        }

    }

    @Nested
    class CreateComment {

        @Test
        void shouldCreateCommentAndReturnStatus201() throws Exception {
            // given
            UUID newsId = UUID.fromString("ccf130f4-e7eb-449c-abb9-66531f50af26");
            CommentCreateRequest createRequest = TestData.createCommentCreateRequest();

            mockNewsService.stubFor(
                    WireMock.get(urlPathEqualTo(String.format(NEWS_URL_TEMPLATE, newsId)))
                            .willReturn(aResponse()
                                    .withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                    .withStatus(OK_STATUS)
                                    .withBodyFile(NEWS_RESPONSE_OK_JSON)
                            )
            );

            // when & then
            mockMvc.perform(post("/api/v1/news/{newsId}/comments", newsId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.text").value(createRequest.text()))
                    .andExpect(jsonPath("$.username").value(createRequest.username()));
        }

        @Test
        void shouldReturnStatus422_whenNewsIsNotExists() throws Exception {
            // given
            UUID newsId = UUID.fromString("ccf130f4-e7eb-449c-abb9-66531f50af22");
            CommentCreateRequest createRequest = TestData.createCommentCreateRequest();

            mockNewsService.stubFor(
                    WireMock.get(urlPathEqualTo(String.format(NEWS_URL_TEMPLATE, newsId)))
                            .willReturn(aResponse()
                                    .withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                    .withStatus(UNPROCESSABLE_ENTITY_STATUS)
                                    .withBodyFile(UNPROCESSABLE_ENTITY_JSON)
                            )
            );

            // when & then
            mockMvc.perform(post("/api/v1/news/{newsId}/comments", newsId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message")
                            .value("News with id 'ccf130f4-e7eb-449c-abb9-66531f50af22' not found"));
        }

    }

    @Nested
    class UpdateComment {

        @Test
        void shouldUpdateCommentAndReturnStatus201() throws Exception {
            // given
            UUID newsId = UUID.fromString("ccf130f4-e7eb-449c-abb9-66531f50af26");
            UUID commentId = UUID.fromString("190d4868-df59-4bc1-84b0-7a7d8fc8cd62");
            CommentUpdateRequest updateRequest = TestData.createCommentUpdateRequest();

            mockNewsService.stubFor(
                    WireMock.get(urlPathEqualTo(String.format(NEWS_URL_TEMPLATE, newsId)))
                            .willReturn(aResponse()
                                    .withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                    .withStatus(OK_STATUS)
                                    .withBodyFile(NEWS_RESPONSE_OK_JSON)
                            )
            );

            // when & then
            mockMvc.perform(put("/api/v1/news/{newsId}/comments/{commentId}", newsId, commentId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.text").value(updateRequest.text()))
                    .andExpect(jsonPath("$.username").value(updateRequest.username()));
        }

        @Test
        void shouldReturnStatus422_whenNewsIsNotExists() throws Exception {
            // given
            UUID newsId = UUID.fromString("ccf130f4-e7eb-449c-abb9-66531f50af22");
            UUID commentId = UUID.fromString("190d4868-df59-4bc1-84b0-7a7d8fc8cd62");
            CommentUpdateRequest updateRequest = TestData.createCommentUpdateRequest();

            mockNewsService.stubFor(
                    WireMock.get(urlPathEqualTo(String.format(NEWS_URL_TEMPLATE, newsId)))
                            .willReturn(aResponse()
                                    .withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                    .withStatus(UNPROCESSABLE_ENTITY_STATUS)
                                    .withBodyFile(UNPROCESSABLE_ENTITY_JSON)
                            )
            );

            // when & then
            mockMvc.perform(put("/api/v1/news/{newsId}/comments/{commentId}", newsId, commentId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message")
                            .value("News with id 'ccf130f4-e7eb-449c-abb9-66531f50af22' not found"));
        }

    }

    @Nested
    class DeleteComment {

        @Test
        void shouldDeleteCommentAndReturnStatus204() throws Exception {
            // given
            UUID newsId = UUID.fromString("ccf130f4-e7eb-449c-abb9-66531f50af26");
            UUID commentId = UUID.fromString("c02d2088-7116-4b37-b3f8-10e15e8e083a");

            mockNewsService.stubFor(
                    WireMock.get(urlPathEqualTo(String.format(NEWS_URL_TEMPLATE, newsId)))
                            .willReturn(aResponse()
                                    .withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                    .withStatus(OK_STATUS)
                                    .withBodyFile(NEWS_RESPONSE_OK_JSON)
                            )
            );

            // when & then
            mockMvc.perform(delete("/api/v1/news/{newsId}/comments/{commentId}", newsId, commentId))
                    .andExpect(status().isNoContent());
        }

        @Test
        void shouldReturnStatus422_whenNewsIsNotExists() throws Exception {
            // given
            UUID newsId = UUID.fromString("ccf130f4-e7eb-449c-abb9-66531f50af22");
            UUID commentId = UUID.fromString("c02d2088-7116-4b37-b3f8-10e15e8e083a");

            mockNewsService.stubFor(
                    WireMock.get(urlPathEqualTo(String.format(NEWS_URL_TEMPLATE, newsId)))
                            .willReturn(aResponse()
                                    .withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                    .withStatus(UNPROCESSABLE_ENTITY_STATUS)
                                    .withBodyFile(UNPROCESSABLE_ENTITY_JSON)
                            )
            );

            // when & then
            mockMvc.perform(delete("/api/v1/news/{newsId}/comments/{commentId}", newsId, commentId))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message")
                            .value("News with id 'ccf130f4-e7eb-449c-abb9-66531f50af22' not found"));
        }

    }

}

