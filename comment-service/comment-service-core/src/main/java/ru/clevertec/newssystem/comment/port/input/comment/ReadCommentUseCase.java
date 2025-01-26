package ru.clevertec.newssystem.comment.port.input.comment;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Window;
import ru.clevertec.newssystem.comment.filter.CommentFilter;
import ru.clevertec.newssystem.comment.port.input.comment.result.ReadCommentUseCaseResult;

import java.util.UUID;

public interface ReadCommentUseCase {

    Window<ReadCommentUseCaseResult> readAllComments(UUID newsId, CommentFilter commentFilter, Pageable pageable);

    ReadCommentUseCaseResult readComment(UUID newsId, UUID commentId);

}
