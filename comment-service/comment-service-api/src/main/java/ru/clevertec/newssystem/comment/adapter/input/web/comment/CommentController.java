package ru.clevertec.newssystem.comment.adapter.input.web.comment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Window;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.clevertec.newssystem.comment.adapter.input.web.comment.dto.CommentCreateRequest;
import ru.clevertec.newssystem.comment.adapter.input.web.comment.dto.CommentResponse;
import ru.clevertec.newssystem.comment.adapter.input.web.comment.dto.CommentUpdateRequest;
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

import java.util.UUID;

@RestController
@RequestMapping("api/v1/news/{newsId}/comments")
@RequiredArgsConstructor
public class CommentController {

    public static final int DEFAULT_PAGE_SIZE = 20;

    private final WebCommentMapper commentMapper;

    private final ReadCommentUseCase readCommentUseCase;

    private final CreateCommentUseCase createCommentUseCase;

    private final UpdateCommentUseCase updateCommentUseCase;

    private final DeleteCommentUseCase deleteCommentUseCase;

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Window<CommentResponse>> getAllComments(
            @PathVariable UUID newsId,
            @ModelAttribute CommentFilter commentFilter,
            @PageableDefault(size = DEFAULT_PAGE_SIZE) Pageable pageable
    ) {
        Window<ReadCommentUseCaseResult> readCommentUseCaseResults =
                readCommentUseCase.readAllComments(newsId, commentFilter, pageable);

        Window<CommentResponse> commentResponses = readCommentUseCaseResults.map(commentMapper::toCommentResponse);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(commentResponses);
    }

    @GetMapping(value = "/{commentId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommentResponse> getComment(@PathVariable UUID newsId, @PathVariable UUID commentId) {
        ReadCommentUseCaseResult readCommentUseCaseResult = readCommentUseCase.readComment(newsId, commentId);

        CommentResponse commentResponse = commentMapper.toCommentResponse(readCommentUseCaseResult);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(commentResponse);
    }

    @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable UUID newsId,
            @RequestBody @Valid CommentCreateRequest commentCreateRequest
    ) {
        CreateCommentUseCaseCommand createCommentUseCaseCommand =
                commentMapper.toCreateCommentUseCaseCommand(commentCreateRequest);

        CreateCommentUseCaseResult comment = createCommentUseCase.createComment(newsId, createCommentUseCaseCommand);

        CommentResponse commentResponse = commentMapper.toCommentResponse(comment);

        return ResponseEntity.status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(commentResponse);
    }

    @PutMapping(value = "/{commentId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable UUID newsId,
            @PathVariable UUID commentId,
            @RequestBody @Valid CommentUpdateRequest commentUpdateRequest
    ) {
        UpdateCommentUseCaseCommand updateCommentUseCaseCommand =
                commentMapper.toUpdateCommentUseCaseCommand(commentUpdateRequest);

        UpdateCommentUseCaseResult comment =
                updateCommentUseCase.updateComment(newsId, commentId, updateCommentUseCaseCommand);

        CommentResponse commentResponse = commentMapper.toCommentResponse(comment);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(commentResponse);
    }

    @DeleteMapping(value = "/{commentId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteComment(@PathVariable UUID newsId, @PathVariable UUID commentId) {
        deleteCommentUseCase.deleteComment(newsId, commentId);

        return ResponseEntity.noContent().build();
    }

}
