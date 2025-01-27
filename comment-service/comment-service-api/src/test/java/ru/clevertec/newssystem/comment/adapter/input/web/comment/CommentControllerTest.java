package ru.clevertec.newssystem.comment.adapter.input.web.comment;

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
import ru.clevertec.newssystem.comment.adapter.input.web.comment.dto.CommentCreateRequest;
import ru.clevertec.newssystem.comment.adapter.input.web.comment.dto.CommentResponse;
import ru.clevertec.newssystem.comment.adapter.input.web.comment.dto.CommentUpdateRequest;
import ru.clevertec.newssystem.comment.exception.CommentNotFoundException;
import ru.clevertec.newssystem.comment.filter.CommentFilter;
import ru.clevertec.newssystem.comment.port.input.comment.CreateCommentUseCase;
import ru.clevertec.newssystem.comment.port.input.comment.DeleteCommentUseCase;
import ru.clevertec.newssystem.comment.port.input.comment.ReadCommentUseCase;
import ru.clevertec.newssystem.comment.port.input.comment.UpdateCommentUseCase;
import ru.clevertec.newssystem.comment.port.input.comment.command.CreateCommentUseCaseCommand;
import ru.clevertec.newssystem.comment.port.input.comment.command.UpdateCommentUseCaseCommand;
import ru.clevertec.newssystem.comment.port.input.comment.result.CreateCommentUseCaseResult;
import ru.clevertec.newssystem.comment.port.input.comment.result.ReadCommentUseCaseResult;
import ru.clevertec.newssystem.comment.port.input.comment.result.UpdateCommentUseCaseResult;
import ru.clevertec.newssystem.comment.util.TestData;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
 * Test class for the {@link CommentController}
 */
@WebMvcTest({CommentController.class})
@Import(RestExceptionHandler.class)
class CommentControllerTest {

    public static final String COMMENT_WITH_ID_NOT_FOUND = "Comment with id '%s' not found";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private WebCommentMapper commentMapper;

    @MockitoBean
    private ReadCommentUseCase readCommentUseCase;

    @MockitoBean
    private CreateCommentUseCase createCommentUseCase;

    @MockitoBean
    private UpdateCommentUseCase updateCommentUseCase;

    @MockitoBean
    private DeleteCommentUseCase deleteCommentUseCase;

    @Nested
    class GetComments {

        @Test
        void shouldGetAllCommentsAndReturnStatus200() throws Exception {
            // given
            UUID newsId = UUID.randomUUID();
            Pageable pageable = PageRequest.of(0, 10);
            CommentFilter commentFilter = TestData.createCommentFilter();
            Window<ReadCommentUseCaseResult> readCommentUseCaseResults =
                    TestData.createWindowOfReadCommentUseCaseResult(10);
            Window<CommentResponse> commentResponses =
                    TestData.createWindowOfCommentResponse(readCommentUseCaseResults);

            when(readCommentUseCase.readAllComments(newsId, commentFilter, pageable))
                    .thenReturn(readCommentUseCaseResults);
            when(commentMapper.toCommentResponse(any(ReadCommentUseCaseResult.class)))
                    .thenAnswer(invocation -> {
                        ReadCommentUseCaseResult input = invocation.getArgument(0);
                        return CommentResponse.builder()
                                .id(input.id())
                                .createdAt(input.createdAt())
                                .lastModifiedAt(input.lastModifiedAt())
                                .text(input.text())
                                .username(input.username())
                                .build();
                    });

            // when & then
            mockMvc.perform(get("/api/v1/news/{newsId}/comments", newsId)
                            .param("createdAtGte", String.valueOf(commentFilter.createdAtGte()))
                            .param("createdAtLte", String.valueOf(commentFilter.createdAtLte()))
                            .param("textContains", commentFilter.textContains())
                            .param("username", commentFilter.username())
                            .param("usernameStarts", commentFilter.usernameStarts())
                            .param("page", String.valueOf(pageable.getPageNumber()))
                            .param("size", String.valueOf(pageable.getPageSize())))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content.length()").value(commentResponses.getContent().size()))
                    .andExpect(jsonPath("$.content[0].id").isNotEmpty())
                    .andExpect(jsonPath("$.content[0].id")
                            .value(commentResponses.getContent().getFirst().id().toString()))
                    .andExpect(jsonPath("$.content[0].text")
                            .value(commentResponses.getContent().getFirst().text()))
                    .andExpect(jsonPath("$.content[0].username")
                            .value(commentResponses.getContent().getFirst().username()));

            verify(readCommentUseCase, times(1))
                    .readAllComments(newsId, commentFilter, pageable);
            verify(commentMapper, times(10))
                    .toCommentResponse(any(ReadCommentUseCaseResult.class));
        }

        @Test
        void shouldGetCommentAndReturnStatus200() throws Exception {
            // given
            UUID newsId = UUID.randomUUID();
            UUID commentId = UUID.randomUUID();
            ReadCommentUseCaseResult readResult = TestData.createReadCommentUseCaseResult();
            CommentResponse expectedResponse = TestData.toCommentResponse(readResult);

            when(readCommentUseCase.readComment(newsId, commentId))
                    .thenReturn(readResult);
            when(commentMapper.toCommentResponse(readResult))
                    .thenReturn(expectedResponse);

            // when & then
            mockMvc.perform(get("/api/v1/news/{newsId}/comments/{commentId}", newsId, commentId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));

            verify(readCommentUseCase, times(1))
                    .readComment(newsId, commentId);
            verify(commentMapper, times(1))
                    .toCommentResponse(readResult);
        }

        @Test
        void shouldReturnStatus422_whenCommentIsNotExists() throws Exception {
            UUID newsId = UUID.randomUUID();
            UUID commentId = UUID.randomUUID();

            when(readCommentUseCase.readComment(newsId, commentId))
                    .thenThrow(CommentNotFoundException.byCommentId(commentId));

            // when & then
            mockMvc.perform(get("/api/v1/news/{newsId}/comments/{commentId}", newsId, commentId))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message")
                            .value(String.format(COMMENT_WITH_ID_NOT_FOUND, commentId)));

            verify(readCommentUseCase, times(1))
                    .readComment(newsId, commentId);
            verifyNoMoreInteractions(commentMapper);
        }

    }

    @Nested
    class CreateComment {

        @Test
        void shouldCreateCommentAndReturnStatus201() throws Exception {
            // given
            UUID newsId = UUID.randomUUID();
            CommentCreateRequest createRequest = TestData.createCommentCreateRequest();
            CreateCommentUseCaseCommand createCommentUseCaseCommand =
                    TestData.toCreateCommentUseCaseCommand(createRequest);
            CreateCommentUseCaseResult createResult = TestData.createCreateCommentUseCaseResult(
                    createCommentUseCaseCommand);
            CommentResponse expectedResponse = TestData.toCommentResponse(createResult);

            when(commentMapper.toCreateCommentUseCaseCommand(createRequest))
                    .thenReturn(createCommentUseCaseCommand);
            when(createCommentUseCase.createComment(newsId, createCommentUseCaseCommand))
                    .thenReturn(createResult);
            when(commentMapper.toCommentResponse(createResult))
                    .thenReturn(expectedResponse);

            // when & then
            mockMvc.perform(post("/api/v1/news/{newsId}/comments", newsId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));

            verify(commentMapper, times(1))
                    .toCreateCommentUseCaseCommand(createRequest);
            verify(createCommentUseCase, times(1))
                    .createComment(newsId, createCommentUseCaseCommand);
            verify(commentMapper, times(1))
                    .toCommentResponse(createResult);
        }

    }

    @Nested
    class UpdateComment {

        @Test
        void shouldUpdateCommentAndReturnStatus200() throws Exception {
            // given
            UUID newsId = UUID.randomUUID();
            UUID commentId = UUID.randomUUID();
            CommentUpdateRequest updateRequest = TestData.createCommentUpdateRequest();
            UpdateCommentUseCaseCommand updateCommentUseCaseCommand =
                    TestData.toUpdateCommentUseCaseCommand(updateRequest);
            UpdateCommentUseCaseResult updateResult =
                    TestData.createUpdateCommentUseCaseResult(updateCommentUseCaseCommand);
            CommentResponse expectedResponse = TestData.toCommentResponse(updateResult);

            when(commentMapper.toUpdateCommentUseCaseCommand(updateRequest))
                    .thenReturn(updateCommentUseCaseCommand);
            when(updateCommentUseCase.updateComment(newsId, commentId, updateCommentUseCaseCommand))
                    .thenReturn(updateResult);
            when(commentMapper.toCommentResponse(updateResult))
                    .thenReturn(expectedResponse);

            // when & then
            mockMvc.perform(put("/api/v1/news/{newsId}/comments/{commentId}", newsId, commentId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));

            verify(commentMapper, times(1))
                    .toUpdateCommentUseCaseCommand(updateRequest);
            verify(updateCommentUseCase, times(1))
                    .updateComment(newsId, commentId, updateCommentUseCaseCommand);
            verify(commentMapper, times(1))
                    .toCommentResponse(updateResult);
        }

        @Test
        void shouldReturnStatus422_whenCommentIsNotExists() throws Exception {
            UUID newsId = UUID.randomUUID();
            UUID commentId = UUID.randomUUID();
            CommentUpdateRequest updateRequest = TestData.createCommentUpdateRequest();
            UpdateCommentUseCaseCommand updateCommentUseCaseCommand =
                    TestData.toUpdateCommentUseCaseCommand(updateRequest);

            when(commentMapper.toUpdateCommentUseCaseCommand(updateRequest))
                    .thenReturn(updateCommentUseCaseCommand);
            when(updateCommentUseCase.updateComment(newsId, commentId, updateCommentUseCaseCommand))
                    .thenThrow(CommentNotFoundException.byCommentId(commentId));

            // when & then
            mockMvc.perform(put("/api/v1/news/{newsId}/comments/{commentId}", newsId, commentId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message")
                            .value(String.format(COMMENT_WITH_ID_NOT_FOUND, commentId)));

            verify(commentMapper, times(1))
                    .toUpdateCommentUseCaseCommand(updateRequest);
            verify(updateCommentUseCase, times(1))
                    .updateComment(newsId, commentId, updateCommentUseCaseCommand);
            verifyNoMoreInteractions(commentMapper);
        }

    }

    @Nested
    class DeleteComment {

        @Test
        void shouldDeleteCommentAndReturnStatus204() throws Exception {
            // given
            UUID newsId = UUID.randomUUID();
            UUID commentId = UUID.randomUUID();

            doNothing()
                    .when(deleteCommentUseCase).deleteComment(newsId, commentId);

            // when & then
            mockMvc.perform(delete("/api/v1/news/{newsId}/comments/{commentId}", newsId, commentId))
                    .andExpect(status().isNoContent());

            verify(deleteCommentUseCase, times(1))
                    .deleteComment(newsId, commentId);
        }

    }

}

