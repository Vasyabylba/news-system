package ru.clevertec.newssystem.news.adapter.output.persistence.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.clevertec.newssystem.news.adapter.output.persistence.jpa.entity.NewsEntity;

import java.util.UUID;

@Repository
public interface NewsRepository extends JpaRepository<NewsEntity, UUID>, JpaSpecificationExecutor<NewsEntity> {

}