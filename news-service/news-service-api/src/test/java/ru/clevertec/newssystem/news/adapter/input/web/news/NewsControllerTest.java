package ru.clevertec.newssystem.news.adapter.input.web.news;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Window;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.clevertec.exceptionhandler.handler.RestExceptionHandler;
import ru.clevertec.newssystem.news.adapter.input.web.news.dto.NewsCreateRequest;
import ru.clevertec.newssystem.news.adapter.input.web.news.dto.NewsResponse;
import ru.clevertec.newssystem.news.adapter.input.web.news.dto.NewsUpdateRequest;
import ru.clevertec.newssystem.news.adapter.input.web.news.dto.NewsWithCommentsResponse;
import ru.clevertec.newssystem.news.exception.NewsNotFoundException;
import ru.clevertec.newssystem.news.filter.NewsFilter;
import ru.clevertec.newssystem.news.port.input.news.CreateNewsUseCase;
import ru.clevertec.newssystem.news.port.input.news.DeleteNewsUseCase;
import ru.clevertec.newssystem.news.port.input.news.ReadNewsUseCase;
import ru.clevertec.newssystem.news.port.input.news.UpdateNewsUseCase;
import ru.clevertec.newssystem.news.port.input.news.command.CreateNewsUseCaseCommand;
import ru.clevertec.newssystem.news.port.input.news.command.UpdateNewsUseCaseCommand;
import ru.clevertec.newssystem.news.port.input.news.result.CreateNewsUseCaseResult;
import ru.clevertec.newssystem.news.port.input.news.result.ReadNewsUseCaseResult;
import ru.clevertec.newssystem.news.port.input.news.result.UpdateNewsUseCaseResult;
import ru.clevertec.newssystem.news.util.TestData;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for the {@link NewsController}
 */
@WebMvcTest
@Import(RestExceptionHandler.class)
class NewsControllerTest {

    public static final String NEWS_WITH_ID_NOT_FOUND = "News with id '%s' not found";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private WebNewsMapper newsMapper;

    @MockitoBean
    private ReadNewsUseCase readNewsUseCase;

    @MockitoBean
    private CreateNewsUseCase createNewsUseCase;

    @MockitoBean
    private UpdateNewsUseCase updateNewsUseCase;

    @MockitoBean
    private DeleteNewsUseCase deleteNewsUseCase;

    @Nested
    class GetNews {

        @Test
        void shouldGetAllNewsAndReturnStatus200() throws Exception {
            // given
            int pageNumber = 0;
            int pageSize = 10;
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Window<ReadNewsUseCaseResult> readNewsUseCaseResults =
                    TestData.createWindowOfReadNewsUseCaseResult(pageSize);
            Window<NewsResponse> newsResponses =
                    TestData.createWindowOfNewsResponse(readNewsUseCaseResults);

            when(readNewsUseCase.readAllNews(any(NewsFilter.class), eq(pageable)))
                    .thenReturn(readNewsUseCaseResults);
            when(newsMapper.toNewsResponse(any(ReadNewsUseCaseResult.class)))
                    .thenAnswer(invocation -> {
                        ReadNewsUseCaseResult input = invocation.getArgument(0);
                        return NewsResponse.builder()
                                .id(input.id())
                                .createdAt(input.createdAt())
                                .lastModifiedAt(input.lastModifiedAt())
                                .title(input.title())
                                .text(input.text())
                                .build();
                    });

            // when & then
            mockMvc.perform(get("/api/v1/news")
                            .param("page", String.valueOf(pageable.getPageNumber()))
                            .param("size", String.valueOf(pageable.getPageSize())))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content.length()").value(newsResponses.getContent().size()))
                    .andExpect(jsonPath("$.content[0].id").isNotEmpty())
                    .andExpect(jsonPath("$.content[0].id")
                            .value(newsResponses.getContent().getFirst().id().toString()))
                    .andExpect(jsonPath("$.content[0].title")
                            .value(newsResponses.getContent().getFirst().title()))
                    .andExpect(jsonPath("$.content[0].text")
                            .value(newsResponses.getContent().getFirst().text()));

            verify(readNewsUseCase, times(1))
                    .readAllNews(any(NewsFilter.class), eq(pageable));
            verify(newsMapper, times(pageSize))
                    .toNewsResponse(any(ReadNewsUseCaseResult.class));
        }

        @Test
        void shouldGetNewsAndReturnStatus200() throws Exception {
            // given
            UUID newsId = UUID.randomUUID();
            ReadNewsUseCaseResult readResult = TestData.createReadNewsUseCaseResultWithOutComments();
            NewsResponse expectedResponse = TestData.toNewsResponse(readResult);

            when(readNewsUseCase.readNews(newsId))
                    .thenReturn(readResult);
            when(newsMapper.toNewsResponse(readResult))
                    .thenReturn(expectedResponse);

            // when & then
            mockMvc.perform(get("/api/v1/news/{newsId}", newsId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));

            verify(readNewsUseCase, times(1)).readNews(newsId);
            verify(newsMapper, times(1)).toNewsResponse(readResult);
        }

        @Test
        void shouldReturnStatus422_whenNewsIsNotExists() throws Exception {
            // given
            UUID newsId = UUID.randomUUID();

            when(readNewsUseCase.readNews(newsId))
                    .thenThrow(NewsNotFoundException.byNewsId(newsId));

            // when & then
            mockMvc.perform(get("/api/v1/news/{newsId}", newsId))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message")
                            .value(String.format(NEWS_WITH_ID_NOT_FOUND, newsId)));

            verifyNoInteractions(newsMapper);
        }

        @Nested
        class ReadNewsWithComments {

            @Test
            void shouldGetNewsWithCommentsAndReturnStatus200() throws Exception {
                // given
                UUID newsId = UUID.randomUUID();
                int pageNumber = 0;
                int pageSize = 10;
                Pageable pageable = PageRequest.of(pageNumber, pageSize);
                ReadNewsUseCaseResult readResult = TestData.createReadNewsUseCaseResultWithComments(pageSize);
                NewsWithCommentsResponse expectedResponse = TestData.toNewsWithCommentsResponse(readResult);

                when(readNewsUseCase.readNewsWithComments(newsId, pageable))
                        .thenReturn(readResult);
                when(newsMapper.toNewsWithCommentResponse(readResult))
                        .thenReturn(expectedResponse);

                // when & then
                mockMvc.perform(get("/api/v1/news/{newsId}/with-comments", newsId)
                                .param("page", String.valueOf(pageable.getPageNumber()))
                                .param("size", String.valueOf(pageable.getPageSize())))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));

                verify(readNewsUseCase, times(1)).readNewsWithComments(newsId, pageable);
                verify(newsMapper, times(1)).toNewsWithCommentResponse(readResult);
            }

            @Test
            void shouldReturnStatus422_whenNewsIsNotExists() throws Exception {
                // given
                UUID newsId = UUID.randomUUID();
                int pageNumber = 0;
                int pageSize = 10;
                Pageable pageable = PageRequest.of(pageNumber, pageSize);

                when(readNewsUseCase.readNewsWithComments(newsId, pageable))
                        .thenThrow(NewsNotFoundException.byNewsId(newsId));

                // when & then
                mockMvc.perform(get("/api/v1/news/{newsId}/with-comments", newsId)
                                .param("page", String.valueOf(pageable.getPageNumber()))
                                .param("size", String.valueOf(pageable.getPageSize())))
                        .andExpect(status().isUnprocessableEntity())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.message")
                                .value(String.format(NEWS_WITH_ID_NOT_FOUND, newsId)));

                verifyNoInteractions(newsMapper);
            }

        }

    }

    @Nested
    class CreateNews {

        @Test
        void shouldCreateNewsAndReturnStatus201() throws Exception {
            // given
            NewsCreateRequest newsCreateRequest = TestData.createNewsCreateRequest();
            CreateNewsUseCaseCommand createNewsUseCaseCommand =
                    TestData.toCreateNewsUseCaseCommand(newsCreateRequest);
            CreateNewsUseCaseResult createResult = TestData.createCreateNewsUseCaseResult(createNewsUseCaseCommand);
            NewsResponse expectedResponse = TestData.toNewsResponse(createResult);

            when(newsMapper.toCreateNewsUseCaseCommand(newsCreateRequest))
                    .thenReturn(createNewsUseCaseCommand);
            when(createNewsUseCase.createNews(createNewsUseCaseCommand))
                    .thenReturn(createResult);
            when(newsMapper.toNewsResponse(createResult))
                    .thenReturn(expectedResponse);

            // when & then
            mockMvc.perform(post("/api/v1/news")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newsCreateRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));

            verify(newsMapper, times(1))
                    .toCreateNewsUseCaseCommand(newsCreateRequest);
            verify(createNewsUseCase, times(1))
                    .createNews(createNewsUseCaseCommand);
            verify(newsMapper, times(1))
                    .toNewsResponse(createResult);
        }

    }

    @Nested
    class UpdateNews {

        @Test
        void shouldUpdateNewsAndReturnStatus200() throws Exception {
            // given
            UUID newsId = UUID.randomUUID();
            NewsUpdateRequest updateRequest = TestData.createNewsUpdateRequest();
            UpdateNewsUseCaseCommand updateNewsUseCaseCommand =
                    TestData.toUpdateNewsUseCaseCommand(updateRequest);
            UpdateNewsUseCaseResult updateResult = TestData.createUpdateNewsUseCaseResult(updateNewsUseCaseCommand);
            NewsResponse expectedResponse = TestData.toNewsResponse(updateResult);

            when(newsMapper.toUpdateNewsUseCaseCommand(updateRequest))
                    .thenReturn(updateNewsUseCaseCommand);
            when(updateNewsUseCase.updateNews(newsId, updateNewsUseCaseCommand))
                    .thenReturn(updateResult);
            when(newsMapper.toNewsResponse(updateResult))
                    .thenReturn(expectedResponse);

            // when & then
            mockMvc.perform(put("/api/v1/news/{newsId}", newsId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));

            verify(newsMapper, times(1))
                    .toUpdateNewsUseCaseCommand(updateRequest);
            verify(updateNewsUseCase, times(1))
                    .updateNews(newsId, updateNewsUseCaseCommand);
            verify(newsMapper, times(1))
                    .toNewsResponse(updateResult);
        }

        @Test
        void shouldReturnStatus422_whenNewsIsNotExists() throws Exception {
            // given
            UUID newsId = UUID.randomUUID();
            NewsUpdateRequest updateRequest = TestData.createNewsUpdateRequest();
            UpdateNewsUseCaseCommand updateNewsUseCaseCommand =
                    TestData.toUpdateNewsUseCaseCommand(updateRequest);

            when(newsMapper.toUpdateNewsUseCaseCommand(updateRequest))
                    .thenReturn(updateNewsUseCaseCommand);
            when(updateNewsUseCase.updateNews(newsId, updateNewsUseCaseCommand))
                    .thenThrow(NewsNotFoundException.byNewsId(newsId));

            // when & then
            mockMvc.perform(put("/api/v1/news/{newsId}", newsId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message")
                            .value(String.format(NEWS_WITH_ID_NOT_FOUND, newsId)));

            verify(newsMapper, times(1))
                    .toUpdateNewsUseCaseCommand(updateRequest);
            verify(updateNewsUseCase, times(1))
                    .updateNews(newsId, updateNewsUseCaseCommand);
            verifyNoMoreInteractions(newsMapper);
        }

    }

    @Nested
    class DeleteNews {

        @Test
        void shouldDeleteNewsAndReturnStatus204() throws Exception {
            // given
            UUID newsId = UUID.randomUUID();

            doNothing()
                    .when(deleteNewsUseCase).deleteNews(newsId);

            // when & then
            mockMvc.perform(delete("/api/v1/news/{newsId}", newsId))
                    .andExpect(status().isNoContent());

            verify(deleteNewsUseCase, times(1)).deleteNews(newsId);
        }

    }

}