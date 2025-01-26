package ru.clevertec.newssystem.comment.adapter.output.client.news;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.clevertec.newssystem.comment.adapter.output.client.news.dto.NewsResponse;
import ru.clevertec.newssystem.comment.port.output.client.result.ReadNewsClientPortResult;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ClientNewsMapper {

    ReadNewsClientPortResult toReadNewsClientPortResult(NewsResponse newsResponse);

}
