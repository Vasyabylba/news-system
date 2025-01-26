package ru.clevertec.newssystem.news.adapter.input.web.news;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Window;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.clevertec.newssystem.news.adapter.input.web.news.dto.NewsCreateRequest;
import ru.clevertec.newssystem.news.adapter.input.web.news.dto.NewsResponse;
import ru.clevertec.newssystem.news.adapter.input.web.news.dto.NewsUpdateRequest;
import ru.clevertec.newssystem.news.adapter.input.web.news.dto.NewsWithCommentsResponse;
import ru.clevertec.newssystem.news.filter.NewsFilter;
import ru.clevertec.newssystem.news.port.input.news.CreateNewsUseCase;
import ru.clevertec.newssystem.news.port.input.news.DeleteNewsUseCase;
import ru.clevertec.newssystem.news.port.input.news.ReadNewsUseCase;
import ru.clevertec.newssystem.news.port.input.news.UpdateNewsUseCase;
import ru.clevertec.newssystem.news.port.input.news.command.CreateNewsUseCaseCommand;
import ru.clevertec.newssystem.news.port.input.news.command.UpdateNewsUseCaseCommand;
import ru.clevertec.newssystem.news.port.input.news.result.CreateNewsUseCaseResult;
import ru.clevertec.newssystem.news.port.input.news.result.ReadNewsUseCaseResult;
import ru.clevertec.newssystem.news.port.input.news.result.UpdateNewsUseCaseResult;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/news")
@RequiredArgsConstructor
public class NewsController {

    public static final int DEFAULT_PAGE_SIZE = 20;

    private final WebNewsMapper newsMapper;

    private final ReadNewsUseCase readNewsUseCase;

    private final CreateNewsUseCase createNewsUseCase;

    private final UpdateNewsUseCase updateNewsUseCase;

    private final DeleteNewsUseCase deleteNewsUseCase;

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Window<NewsResponse>> getAllNews(
            @ModelAttribute NewsFilter newsFilter,
            @PageableDefault(size = DEFAULT_PAGE_SIZE) Pageable pageable
    ) {
        Window<ReadNewsUseCaseResult> readNewsUseCaseResults = readNewsUseCase.readAllNews(newsFilter, pageable);

        Window<NewsResponse> newsResponses = readNewsUseCaseResults.map(newsMapper::toNewsResponse);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(newsResponses);
    }

    @GetMapping(value = "/{newsId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<NewsResponse> getNews(@PathVariable UUID newsId) {
        ReadNewsUseCaseResult readNewsUseCaseResult = readNewsUseCase.readNews(newsId);

        NewsResponse newsResponse = newsMapper.toNewsResponse(readNewsUseCaseResult);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(newsResponse);
    }

    @GetMapping(value = "/{newsId}/with-comments", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<NewsWithCommentsResponse> getNewsWithComments(
            @PathVariable UUID newsId,
            @PageableDefault(size = DEFAULT_PAGE_SIZE) Pageable pageable
    ) {
        ReadNewsUseCaseResult readNewsUseCaseResult = readNewsUseCase.readNewsWithComments(newsId, pageable);

        NewsWithCommentsResponse newsWithCommentsResponse =
                newsMapper.toNewsWithCommentResponse(readNewsUseCaseResult);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(newsWithCommentsResponse);
    }

    @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<NewsResponse> createNews(@RequestBody @Valid NewsCreateRequest newsCreateRequest) {
        CreateNewsUseCaseCommand createNewsUseCaseCommand = newsMapper.toCreateNewsUseCaseCommand(newsCreateRequest);

        CreateNewsUseCaseResult news = createNewsUseCase.createNews(createNewsUseCaseCommand);

        NewsResponse newsResponse = newsMapper.toNewsResponse(news);

        return ResponseEntity.status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(newsResponse);
    }

    @PutMapping(value = "/{newsId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<NewsResponse> updateNews(@PathVariable UUID newsId,
                                                   @RequestBody @Valid NewsUpdateRequest newsUpdateRequest) {
        UpdateNewsUseCaseCommand updateNewsUseCaseCommand = newsMapper.toUpdateNewsUseCaseCommand(newsUpdateRequest);

        UpdateNewsUseCaseResult news = updateNewsUseCase.updateNews(newsId, updateNewsUseCaseCommand);

        NewsResponse newsResponse = newsMapper.toNewsResponse(news);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(newsResponse);
    }

    @DeleteMapping(value = "/{newsId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteNews(@PathVariable UUID newsId) {
        deleteNewsUseCase.deleteNews(newsId);

        return ResponseEntity.noContent().build();
    }

}
