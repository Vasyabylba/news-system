package ru.clevertec.newssystem.comment.adapter.output.client.news.configuration;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import ru.clevertec.newssystem.comment.adapter.output.client.news.CustomErrorDecoder;

public class NewsFeignClientConfiguration {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }

}
