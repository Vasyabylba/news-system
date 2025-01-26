package ru.clevertec.newssystem.comment.adapter.output.persistence.jpa.adapter;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Window;
import org.springframework.data.jpa.domain.Specification;
import ru.clevertec.newssystem.comment.adapter.output.persistence.jpa.PersistenceJpaCommentMapper;
import ru.clevertec.newssystem.comment.adapter.output.persistence.jpa.entity.CommentEntity;
import ru.clevertec.newssystem.comment.adapter.output.persistence.jpa.repository.CommentRepository;
import ru.clevertec.newssystem.comment.exception.CommentNotFoundException;
import ru.clevertec.newssystem.comment.filter.CommentFilter;
import ru.clevertec.newssystem.comment.port.output.persistence.result.CommentPersistencePortResult;
import ru.clevertec.newssystem.comment.util.TestData;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReadCommentJpaAdapterTest {

    public static final String COMMENT_WITH_ID_NOT_FOUND = "Comment with id '%s' not found";

    @Mock
    private PersistenceJpaCommentMapper commentMapper;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ReadCommentJpaAdapter readCommentJpaAdapter;

    @Test
    void shouldReadAllCommentsByNews() {
        //given
        UUID newsId = UUID.randomUUID();
        CommentFilter commentFilter = TestData.createCommentFilter();
        int pageNumber = 0;
        int pageSize = 10;
        Pageable pageable = TestData.createUnsortedPageable(pageNumber, pageSize);

        Window<CommentEntity> commentEntityWindow = TestData.createWindowOfCommentEntity(newsId, pageable);

        Window<CommentPersistencePortResult> expected
                = TestData.createCommentPersistencePortResultWindow(commentEntityWindow.getContent(), pageable);

        when(commentRepository.findBy(any(Specification.class), any()))
                .thenReturn(commentEntityWindow);
        when(commentMapper.toCommentPersistencePortResult(any(CommentEntity.class)))
                .thenAnswer(invocation -> {
                    CommentEntity input = invocation.getArgument(0);
                    return CommentPersistencePortResult.builder()
                            .id(input.getId())
                            .createdAt(input.getCreatedAt())
                            .lastModifiedAt(input.getLastModifiedAt())
                            .text(input.getText())
                            .username(input.getUsername())
                            .newsId(input.getNewsId())
                            .build();
                });

        //when
        Window<CommentPersistencePortResult> actual =
                readCommentJpaAdapter.readAllComments(newsId, commentFilter, pageable);

        //then
        assertThat(actual).isNotNull();
        assertThat(actual.getContent())
                .hasSize(expected.getContent().size())
                .containsExactlyElementsOf(expected.getContent());
        assertThat(actual.positionAt(expected.getContent().get(pageSize - 1)))
                .isEqualTo(expected.positionAt(pageSize - 1));

        verify(commentRepository, times(1)).findBy(any(Specification.class), any());
        verify(commentMapper, times(pageSize)).toCommentPersistencePortResult(any(CommentEntity.class));
    }

    @Nested
    class ReadComment {

        @Test
        void shouldReadComment() {
            //given
            UUID commentId = UUID.randomUUID();
            UUID newsId = UUID.randomUUID();

            CommentEntity comment = TestData.toCommentEntity(commentId, newsId);

            CommentPersistencePortResult expected
                    = TestData.createCommentPersistencePortResult(comment);

            when(commentRepository.findByIdAndNewsId(commentId, newsId))
                    .thenReturn(Optional.of(comment));
            when(commentMapper.toCommentPersistencePortResult(comment))
                    .thenReturn(expected);

            //when
            CommentPersistencePortResult actual = readCommentJpaAdapter.readComment(commentId, newsId);

            //then
            assertThat(actual).isNotNull();
            assertThat(actual.id()).isEqualTo(expected.id());
            assertThat(actual.createdAt()).isEqualTo(expected.createdAt());
            assertThat(actual.lastModifiedAt()).isEqualTo(expected.lastModifiedAt());
            assertThat(actual.text()).isEqualTo(expected.text());
            assertThat(actual.username()).isEqualTo(expected.username());
            assertThat(actual.newsId()).isEqualTo(expected.newsId());

            verify(commentRepository, times(1))
                    .findByIdAndNewsId(commentId, newsId);
            verify(commentMapper, times(1))
                    .toCommentPersistencePortResult(comment);
        }

        @Test
        void shouldThrowCommentNotFoundException_whenCommentNotExists() {
            //given
            UUID commentId = UUID.randomUUID();
            UUID newsId = UUID.randomUUID();

            when(commentRepository.findByIdAndNewsId(commentId, newsId))
                    .thenThrow(CommentNotFoundException.byCommentId(commentId));

            //when & then
            CommentNotFoundException actual = assertThrows(
                    CommentNotFoundException.class,
                    () -> readCommentJpaAdapter.readComment(commentId, newsId)
            );

            assertThat(actual)
                    .hasMessageContaining(String.format(COMMENT_WITH_ID_NOT_FOUND, commentId));

            verify(commentRepository, times(1))
                    .findByIdAndNewsId(commentId, newsId);
            verifyNoInteractions(commentMapper);
        }

    }

}