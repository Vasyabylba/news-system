package ru.clevertec.newssystem.comment.adapter.output.persistence.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.newssystem.comment.adapter.output.persistence.jpa.entity.CommentEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, UUID>, JpaSpecificationExecutor<CommentEntity> {

    Optional<CommentEntity> findByIdAndNewsId(UUID id, UUID newsId);

    @Transactional
    @Modifying
    @Query("delete from CommentEntity c where c.id = :commentId and c.newsId = :newsId")
    void deleteByIdAndNewsId(@Param("commentId") UUID commentId, @Param("newsId") UUID newsId);

}