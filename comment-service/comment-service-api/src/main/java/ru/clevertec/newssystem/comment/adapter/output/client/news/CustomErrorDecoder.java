package ru.clevertec.newssystem.comment.adapter.output.client.news;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import ru.clevertec.exceptionhandler.handler.RestExceptionHandler;
import ru.clevertec.newssystem.comment.exception.NewsBadRequestException;
import ru.clevertec.newssystem.comment.exception.NewsNotFoundException;

import java.io.IOException;
import java.io.InputStream;

public class CustomErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder errorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        RestExceptionHandler.GenericErrorResponse message = null;
        try (InputStream bodyIs = response.body().asInputStream()) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.findAndRegisterModules();
            message = mapper.readValue(bodyIs, RestExceptionHandler.GenericErrorResponse.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        HttpStatus responseStatus = HttpStatus.valueOf(response.status());
        switch (responseStatus) {
            case HttpStatus.BAD_REQUEST -> throw new NewsBadRequestException(
                    message.message() != null ? message.message() : "NewsId Bad Request"
            );
            case HttpStatus.UNPROCESSABLE_ENTITY -> throw new NewsNotFoundException(
                    message.message() != null ? message.message() : "News not found"
            );
            default -> {
                return errorDecoder.decode(methodKey, response);
            }
        }
    }

}