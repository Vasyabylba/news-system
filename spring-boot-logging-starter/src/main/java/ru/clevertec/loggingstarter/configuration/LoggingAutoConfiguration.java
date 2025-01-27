package ru.clevertec.loggingstarter.configuration;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.clevertec.loggingstarter.aop.ControllerLoggingAspect;

@Configuration
@EnableConfigurationProperties(ControllerLoggingProperties.class)
public class LoggingAutoConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "controller-logging", name = "enable", havingValue = "true")
    public ControllerLoggingAspect controllerLoggingAspect(ObjectFactory<HttpServletRequest> httpServletRequestFactory) {
        return new ControllerLoggingAspect(httpServletRequestFactory);
    }

}
