package ru.clevertec.exceptionhandler.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.clevertec.exceptionhandler.handler.RestExceptionHandler;

@Configuration
@EnableConfigurationProperties(ExceptionHandlingProperties.class)
public class ExceptionHandlingAutoConfiguration {

    @Bean
    @ConditionalOnProperty(value = "exception-handling.enable", havingValue = "true", matchIfMissing = true)
    public RestExceptionHandler restExceptionHandler() {
        return new RestExceptionHandler();
    }

}
