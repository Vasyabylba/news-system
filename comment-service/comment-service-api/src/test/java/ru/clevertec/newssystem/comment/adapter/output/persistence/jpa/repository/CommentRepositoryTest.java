package ru.clevertec.newssystem.comment.adapter.output.persistence.jpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Window;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.clevertec.newssystem.comment.adapter.output.persistence.jpa.entity.CommentEntity;
import ru.clevertec.newssystem.comment.util.TestData;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EnableJpaAuditing
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Test
    @Sql(scripts = "classpath:db/insert-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:db/delete-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldCreateComment() {
        // given
        CommentEntity comment = TestData.generateCommentEntityForCreate();

        // when
        CommentEntity savedComment = commentRepository.save(comment);
        commentRepository.flush();

        // then
        assertThat(savedComment).isNotNull();
        assertThat(savedComment.getId()).isNotNull();
        assertThat(savedComment.getCreatedAt()).isNotNull();
        assertThat(savedComment.getLastModifiedAt()).isNotNull();
        assertThat(savedComment.getText()).isEqualTo(comment.getText());
        assertThat(savedComment.getUsername()).isEqualTo(comment.getUsername());
        assertThat(savedComment.getNewsId()).isEqualByComparingTo(comment.getNewsId());
    }

    @Test
    @Sql(scripts = "classpath:db/insert-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:db/delete-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldReadCommentByIdAndNewsId() {
        // given
        CommentEntity comment = TestData.getCommentEntityForRead();

        // when
        CommentEntity readComment = commentRepository.findByIdAndNewsId(comment.getId(), comment.getNewsId())
                .orElse(null);

        // then
        assertThat(readComment).isNotNull();
        assertThat(readComment.getId()).isNotNull();
        assertThat(readComment.getId()).isEqualByComparingTo(comment.getId());
        assertThat(readComment.getCreatedAt()).isEqualTo(comment.getCreatedAt());
        assertThat(readComment.getText()).isEqualTo(comment.getText());
        assertThat(readComment.getUsername()).isEqualTo(comment.getUsername());
        assertThat(readComment.getNewsId()).isEqualByComparingTo(comment.getNewsId());
    }

    @Test
    @Sql(scripts = "classpath:db/insert-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:db/delete-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldUpdateComment() {
        // given
        CommentEntity comment = TestData.getCommentEntityForUpdate();

        // when
        CommentEntity updatedComment = commentRepository.save(comment);

        // then
        assertThat(updatedComment).isNotNull();
        assertThat(updatedComment.getId()).isNotNull();
        assertThat(updatedComment.getId()).isEqualByComparingTo(comment.getId());
        assertThat(updatedComment.getCreatedAt()).isEqualTo(comment.getCreatedAt());
        assertThat(updatedComment.getLastModifiedAt()).isEqualTo(comment.getLastModifiedAt());
        assertThat(updatedComment.getText()).isEqualTo(comment.getText());
        assertThat(updatedComment.getUsername()).isEqualTo(comment.getUsername());
        assertThat(updatedComment.getNewsId()).isEqualByComparingTo(comment.getNewsId());
    }

    @Test
    @Sql(scripts = "classpath:db/insert-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:db/delete-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldDeleteComment() {
        // given
        CommentEntity comment = TestData.getCommentEntityForDelete();

        // when
        commentRepository.deleteByIdAndNewsId(comment.getId(), comment.getNewsId());
        CommentEntity deletedComment = commentRepository.findById(comment.getId())
                .orElse(null);

        // then
        assertThat(deletedComment).isNull();
    }

    @Test
    @Sql(scripts = "classpath:db/insert-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:db/delete-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldReadAllCommentsByNews() {
        //given
        Specification<CommentEntity> commentSpecification = TestData.createCommentSpecification();
        Pageable pageable = TestData.createUnsortedPageable(0, 3);

        //when
        Window<CommentEntity> commentWindow = commentRepository.findBy(
                commentSpecification, fluentQuery -> fluentQuery
                        .limit(pageable.getPageSize())
                        .scroll(pageable.toScrollPosition())
        );

        //then
        assertNotNull(commentWindow);
        assertThat(commentWindow).isNotNull();
        assertThat(commentWindow.getContent()).hasSize(2);
    }

}