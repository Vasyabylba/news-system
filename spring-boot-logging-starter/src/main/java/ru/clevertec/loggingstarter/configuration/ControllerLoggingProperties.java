package ru.clevertec.loggingstarter.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("controller-logging")
public class ControllerLoggingProperties {

    private boolean enable = false;

}
