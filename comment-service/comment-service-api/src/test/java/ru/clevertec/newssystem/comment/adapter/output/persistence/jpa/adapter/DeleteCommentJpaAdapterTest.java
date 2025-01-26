package ru.clevertec.newssystem.comment.adapter.output.persistence.jpa.adapter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.newssystem.comment.adapter.output.persistence.jpa.repository.CommentRepository;

import java.util.UUID;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class DeleteCommentJpaAdapterTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private DeleteCommentJpaAdapter deleteCommentJpaAdapter;

    @Test
    void shouldDeleteComment() {
        //given
        UUID commentId = UUID.randomUUID();
        UUID newsId = UUID.randomUUID();

        doNothing()
                .when(commentRepository).deleteByIdAndNewsId(commentId, newsId);

        //when
        deleteCommentJpaAdapter.deleteComment(commentId, newsId);

        //then
        verifyNoMoreInteractions(commentRepository);
    }

}

