package ru.clevertec.newssystem.news.adapter.output.persistence.jpa.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import ru.clevertec.newssystem.news.adapter.output.persistence.jpa.entity.NewsEntity;

import java.time.LocalDateTime;

public class NewsSpecification {

    private NewsSpecification() {
    }

    public static Specification<NewsEntity> createdAtGteSpec(LocalDateTime createdAtGte) {
        return ((root, query, cb) -> createdAtGte != null
                ? cb.greaterThanOrEqualTo(root.get("createdAt"), createdAtGte)
                : null);
    }

    public static Specification<NewsEntity> createdAtLteSpec(LocalDateTime createdAtLte) {
        return ((root, query, cb) -> createdAtLte != null
                ? cb.lessThanOrEqualTo(root.get("createdAt"), createdAtLte)
                : null);
    }

    public static Specification<NewsEntity> titleContainsSpec(String titleContains) {
        return ((root, query, cb) -> StringUtils.hasText(titleContains)
                ? cb.like(cb.lower(root.get("title")), "%" + titleContains.toLowerCase() + "%")
                : null);
    }

    public static Specification<NewsEntity> textContainsSpec(String textContains) {
        return ((root, query, cb) -> StringUtils.hasText(textContains)
                ? cb.like(cb.lower(root.get("text")), "%" + textContains.toLowerCase() + "%")
                : null);
    }

}
