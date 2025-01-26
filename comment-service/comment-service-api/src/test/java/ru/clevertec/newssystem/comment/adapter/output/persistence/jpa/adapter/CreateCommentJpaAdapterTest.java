package ru.clevertec.newssystem.comment.adapter.output.persistence.jpa.adapter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.newssystem.comment.adapter.output.persistence.jpa.PersistenceJpaCommentMapper;
import ru.clevertec.newssystem.comment.adapter.output.persistence.jpa.entity.CommentEntity;
import ru.clevertec.newssystem.comment.adapter.output.persistence.jpa.repository.CommentRepository;
import ru.clevertec.newssystem.comment.port.output.persistence.command.CreateCommentPersistencePortCommand;
import ru.clevertec.newssystem.comment.port.output.persistence.result.CommentPersistencePortResult;
import ru.clevertec.newssystem.comment.util.TestData;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateCommentJpaAdapterTest {

    @Mock
    private PersistenceJpaCommentMapper commentMapper;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CreateCommentJpaAdapter createCommentJpaAdapter;

    @Test
    void shouldCreateComment() {
        //given
        CreateCommentPersistencePortCommand createCommentPersistencePortCommand =
                TestData.createCreateCommentPersistencePortCommand();
        CommentEntity comment = TestData.toCommentEntity(createCommentPersistencePortCommand);
        CommentEntity savedComment = TestData.createSavedCommentEntity(comment);
        CommentPersistencePortResult expected
                = TestData.toCreateCommentPersistencePortResult(savedComment);

        when(commentMapper.toCommentEntity(createCommentPersistencePortCommand))
                .thenReturn(comment);
        when(commentRepository.save(comment))
                .thenReturn(savedComment);
        when(commentMapper.toCommentPersistencePortResult(savedComment))
                .thenReturn(expected);

        //when
        CommentPersistencePortResult actual =
                createCommentJpaAdapter.createComment(createCommentPersistencePortCommand);

        //then
        assertThat(actual).isNotNull();
        assertThat(actual.id()).isEqualTo(expected.id());
        assertThat(actual.createdAt()).isEqualTo(expected.createdAt());
        assertThat(actual.lastModifiedAt()).isEqualTo(expected.lastModifiedAt());
        assertThat(actual.text()).isEqualTo(expected.text());
        assertThat(actual.username()).isEqualTo(expected.username());
        assertThat(actual.newsId()).isEqualTo(expected.newsId());

        verify(commentMapper, times(1))
                .toCommentEntity(createCommentPersistencePortCommand);
        verify(commentRepository, times(1))
                .save(comment);
        verify(commentMapper, times(1))
                .toCommentPersistencePortResult(savedComment);
    }

}