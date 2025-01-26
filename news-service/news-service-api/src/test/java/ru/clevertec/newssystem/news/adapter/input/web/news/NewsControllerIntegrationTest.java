package ru.clevertec.newssystem.news.adapter.input.web.news;

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
import ru.clevertec.newssystem.news.adapter.input.web.news.dto.NewsCreateRequest;
import ru.clevertec.newssystem.news.adapter.input.web.news.dto.NewsUpdateRequest;
import ru.clevertec.newssystem.news.util.TestData;

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
                name = "comment-service"
        )
})
@AutoConfigureMockMvc
public class NewsControllerIntegrationTest {

    public static final String COMMENTS_URL_TEMPLATE = "/api/v1/news/%s/comments";

    public static final String CONTENT_TYPE = "Content-Type";

    public static final int OK_STATUS = 200;

    public static final String COMMENTS_RESPONSE_OK_JSON = "commentsResponse#200.json";

    @InjectWireMock("comment-service")
    private WireMockServer mockCommentService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    class GetNews {

        @Test
        void shouldGetAllNewsAndReturnStatus200() throws Exception {
            // given
            Pageable pageable = PageRequest.of(0, 10);

            // when & then
            mockMvc.perform(get("/api/v1/news")
                            .param("page", String.valueOf(pageable.getPageNumber()))
                            .param("size", String.valueOf(pageable.getPageSize())))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content.length()").value(10));
        }

        @Test
        void shouldGetNewsAndReturnStatus200() throws Exception {
            // given
            UUID newsId = UUID.fromString("7affdbc7-c0c6-4f38-87c8-24d65d9ed374");

            // when & then
            mockMvc.perform(get("/api/v1/news/{newsId}", newsId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(newsId.toString()));
        }

        @Test
        void shouldReturnStatus422_whenNewsIsNotExists() throws Exception {
            // given
            UUID newsId = UUID.fromString("ccf130f4-e7eb-449c-abb9-66531f50af22");

            // when & then
            mockMvc.perform(get("/api/v1/news/{newsId}", newsId))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message")
                            .value("News with id 'ccf130f4-e7eb-449c-abb9-66531f50af22' not found"));
        }

        @Nested
        class ReadNewsWithComments {

            @Test
            void shouldGetNewsWithCommentsAndReturnStatus200() throws Exception {
                // given
                UUID newsId = UUID.fromString("7affdbc7-c0c6-4f38-87c8-24d65d9ed374");
                Pageable pageable = PageRequest.of(0, 10);

                mockCommentService.stubFor(
                        WireMock.get(urlPathEqualTo(String.format(COMMENTS_URL_TEMPLATE, newsId)))
                                .willReturn(aResponse()
                                        .withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                        .withStatus(OK_STATUS)
                                        .withBodyFile(COMMENTS_RESPONSE_OK_JSON)
                                )
                );

                // when & then
                mockMvc.perform(get("/api/v1/news/{newsId}/with-comments", newsId)
                                .param("page", String.valueOf(pageable.getPageNumber()))
                                .param("size", String.valueOf(pageable.getPageSize())))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.comments.content.length()").value(10));
            }

            @Test
            void shouldReturnStatus422_whenNewsIsNotExists() throws Exception {
                // given
                UUID newsId = UUID.fromString("ccf130f4-e7eb-449c-abb9-66531f50af22");
                Pageable pageable = PageRequest.of(0, 10);

                mockCommentService.stubFor(
                        WireMock.get(urlPathEqualTo(String.format(COMMENTS_URL_TEMPLATE, newsId)))
                                .willReturn(aResponse()
                                        .withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                        .withStatus(OK_STATUS)
                                        .withBodyFile(COMMENTS_RESPONSE_OK_JSON)
                                )
                );

                // when & then
                mockMvc.perform(get("/api/v1/news/{newsId}/with-comments", newsId)
                                .param("page", String.valueOf(pageable.getPageNumber()))
                                .param("size", String.valueOf(pageable.getPageSize())))
                        .andExpect(status().isUnprocessableEntity())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.message")
                                .value("News with id 'ccf130f4-e7eb-449c-abb9-66531f50af22' not found"));
            }

        }

    }

    @Nested
    class CreateNews {

        @Test
        void shouldCreateCommentAndReturnStatus201() throws Exception {
            // given
            NewsCreateRequest newsCreateRequest = TestData.createNewsCreateRequest();

            // when & then
            mockMvc.perform(post("/api/v1/news")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newsCreateRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.title").value(newsCreateRequest.title()))
                    .andExpect(jsonPath("$.text").value(newsCreateRequest.text()));
        }

    }

    @Nested
    class UpdateNews {

        @Test
        void shouldUpdateNewsAndReturnStatus200() throws Exception {
            // given
            UUID newsId = UUID.fromString("915ff41c-4c8d-4d98-a927-b3ddf077366b");
            NewsUpdateRequest updateRequest = TestData.createNewsUpdateRequest();

            // when & then
            mockMvc.perform(put("/api/v1/news/{newsId}", newsId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.title").value(updateRequest.title()))
                    .andExpect(jsonPath("$.text").value(updateRequest.text()));
        }

        @Test
        void shouldReturnStatus422_whenNewsIsNotExists() throws Exception {
            // given
            UUID newsId = UUID.fromString("ccf130f4-e7eb-449c-abb9-66531f50af22");
            NewsUpdateRequest updateRequest = TestData.createNewsUpdateRequest();

            // when & then
            mockMvc.perform(put("/api/v1/news/{newsId}", newsId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message")
                            .value("News with id 'ccf130f4-e7eb-449c-abb9-66531f50af22' not found"));
        }

    }

    @Nested
    class DeleteNews {

        @Test
        void shouldDeleteNewsAndReturnStatus204() throws Exception {
            // given
            UUID newsId = UUID.fromString("87735d1c-7650-4308-ba7b-2218f2d2860e");

            // when & then
            mockMvc.perform(delete("/api/v1/news/{newsId}", newsId))
                    .andExpect(status().isNoContent());
        }

        @Test
        void shouldReturnStatus204_whenNewsIsNotExists() throws Exception {
            // given
            UUID newsId = UUID.fromString("ccf130f4-e7eb-449c-abb9-66531f50af22");

            // when & then
            mockMvc.perform(delete("/api/v1/news/{newsId}", newsId))
                    .andExpect(status().isNoContent());
        }

    }

}
