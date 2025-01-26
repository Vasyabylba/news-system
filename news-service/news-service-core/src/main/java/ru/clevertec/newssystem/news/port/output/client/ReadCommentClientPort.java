package ru.clevertec.newssystem.news.port.output.client;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Window;
import ru.clevertec.newssystem.news.port.output.client.result.ReadCommentClientPortResult;

import java.util.UUID;

public interface ReadCommentClientPort {

    Window<ReadCommentClientPortResult> readCommentsByNews(UUID newsId, Pageable pageable);

}
