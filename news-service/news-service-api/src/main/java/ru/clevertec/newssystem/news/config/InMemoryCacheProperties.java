package ru.clevertec.newssystem.news.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("in-memory-cache")
public class InMemoryCacheProperties {

    private boolean enable = false;

    private String algorithm;

    private Integer capacity;

}
