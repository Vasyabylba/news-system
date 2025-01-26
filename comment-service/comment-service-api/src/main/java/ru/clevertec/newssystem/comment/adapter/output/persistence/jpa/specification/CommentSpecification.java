package ru.clevertec.newssystem.comment.adapter.output.persistence.jpa.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import ru.clevertec.newssystem.comment.adapter.output.persistence.jpa.entity.CommentEntity;

import java.time.LocalDateTime;
import java.util.UUID;

public class CommentSpecification {

    private CommentSpecification() {
    }

    public static Specification<CommentEntity> createdAtGteSpec(LocalDateTime createdAtGte) {
        return ((root, query, cb) -> createdAtGte != null
                ? cb.greaterThanOrEqualTo(root.get("createdAt"), createdAtGte)
                : null);
    }

    public static Specification<CommentEntity> createdAtLteSpec(LocalDateTime createdAtLte) {
        return ((root, query, cb) -> createdAtLte != null
                ? cb.lessThanOrEqualTo(root.get("createdAt"), createdAtLte)
                : null);
    }

    public static Specification<CommentEntity> textContainsSpec(String textContains) {
        return ((root, query, cb) -> StringUtils.hasText(textContains)
                ? cb.like(cb.lower(root.get("text")), "%" + textContains.toLowerCase() + "%")
                : null);
    }

    public static Specification<CommentEntity> usernameSpec(String username) {
        return ((root, query, cb) -> StringUtils.hasText(username)
                ? cb.equal(cb.lower(root.get("username")), username.toLowerCase())
                : null);
    }

    public static Specification<CommentEntity> usernameStartsSpec(String usernameStarts) {
        return ((root, query, cb) -> StringUtils.hasText(usernameStarts)
                ? cb.like(cb.lower(root.get("username")), usernameStarts.toLowerCase() + "%")
                : null);
    }

    public static Specification<CommentEntity> newsIdSpec(UUID newsId) {
        return ((root, query, cb) -> newsId != null
                ? cb.equal(root.get("newsId"), newsId)
                : null);
    }

}
