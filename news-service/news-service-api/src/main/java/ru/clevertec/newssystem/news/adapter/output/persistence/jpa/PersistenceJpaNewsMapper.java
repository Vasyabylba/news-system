package ru.clevertec.newssystem.news.adapter.output.persistence.jpa;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.clevertec.newssystem.news.adapter.output.persistence.jpa.entity.NewsEntity;
import ru.clevertec.newssystem.news.port.output.persistence.command.CreateNewsPersistencePortCommand;
import ru.clevertec.newssystem.news.port.output.persistence.command.UpdateNewsPersistencePortCommand;
import ru.clevertec.newssystem.news.port.output.persistence.result.NewsPersistencePortResult;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface PersistenceJpaNewsMapper {

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastModifiedAt", ignore = true)
    NewsEntity toNewsEntity(CreateNewsPersistencePortCommand createNewsPersistencePortCommand);

    NewsEntity toNewsEntity(UpdateNewsPersistencePortCommand updateNewsPersistencePortCommand);

    NewsPersistencePortResult toNewsPersistencePortResult(NewsEntity news);

}