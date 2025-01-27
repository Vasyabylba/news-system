package ru.clevertec.newssystem.comment.adapter.output.persistence.jpa.adapter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.newssystem.comment.adapter.output.persistence.jpa.PersistenceJpaCommentMapper;
import ru.clevertec.newssystem.comment.adapter.output.persistence.jpa.entity.CommentEntity;
import ru.clevertec.newssystem.comment.adapter.output.persistence.jpa.repository.CommentRepository;
import ru.clevertec.newssystem.comment.port.output.persistence.command.UpdateCommentPersistencePortCommand;
import ru.clevertec.newssystem.comment.port.output.persistence.result.CommentPersistencePortResult;
import ru.clevertec.newssystem.comment.util.TestData;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateCommentJpaAdapterTest {

    @Mock
    private PersistenceJpaCommentMapper commentMapper;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private UpdateCommentJpaAdapter updateCommentJpaAdapter;

    @Test
    void shouldUpdateComment() {
        //given
        UpdateCommentPersistencePortCommand updateCommentPersistencePortCommand =
                TestData.createUpdateCommentPersistencePortCommand();
        CommentEntity comment = TestData.toCommentEntity(updateCommentPersistencePortCommand);
        CommentEntity updatedComment = TestData.createUpdatedCommentEntity(comment);
        CommentPersistencePortResult expected
                = TestData.toCommentPersistencePortResult(updatedComment);

        when(commentMapper.toCommentEntity(updateCommentPersistencePortCommand))
                .thenReturn(comment);
        when(commentRepository.save(comment))
                .thenReturn(updatedComment);
        when(commentMapper.toCommentPersistencePortResult(updatedComment))
                .thenReturn(expected);

        //when
        CommentPersistencePortResult actual =
                updateCommentJpaAdapter.updateComment(updateCommentPersistencePortCommand);

        //then
        assertThat(actual).isNotNull();
        assertThat(actual.id()).isEqualTo(expected.id());
        assertThat(actual.createdAt()).isEqualTo(expected.createdAt());
        assertThat(actual.lastModifiedAt()).isEqualTo(expected.lastModifiedAt());
        assertThat(actual.text()).isEqualTo(expected.text());
        assertThat(actual.username()).isEqualTo(expected.username());
        assertThat(actual.newsId()).isEqualTo(expected.newsId());

        verify(commentMapper, times(1))
                .toCommentEntity(updateCommentPersistencePortCommand);
        verify(commentRepository, times(1))
                .save(comment);
        verify(commentMapper, times(1))
                .toCommentPersistencePortResult(updatedComment);
    }

}