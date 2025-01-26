package ru.clevertec.exceptionhandler.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("exception-handling")
public class ExceptionHandlingProperties {

    private boolean enable = true;

}
