package ru.clevertec.newssystem.comment.port.output.persistence;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Window;
import ru.clevertec.newssystem.comment.filter.CommentFilter;
import ru.clevertec.newssystem.comment.port.output.persistence.result.CommentPersistencePortResult;

import java.util.UUID;


public interface ReadCommentPersistencePort {

    Window<CommentPersistencePortResult> readAllComments(UUID newsId,
                                                         CommentFilter commentFilter,
                                                         Pageable pageable);

    CommentPersistencePortResult readComment(UUID commentId, UUID newsId);

}
