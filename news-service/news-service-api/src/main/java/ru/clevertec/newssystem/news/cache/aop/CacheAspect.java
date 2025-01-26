package ru.clevertec.newssystem.news.cache.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import ru.clevertec.newssystem.news.adapter.output.persistence.jpa.entity.NewsEntity;
import ru.clevertec.newssystem.news.cache.Cache;
import ru.clevertec.newssystem.news.config.InMemoryCacheConfiguration;

import java.util.Optional;
import java.util.UUID;

@ConditionalOnBean(InMemoryCacheConfiguration.class)
@Component
@Aspect
@RequiredArgsConstructor
@Slf4j
public class CacheAspect {

    public static final String OBJECT_WITH_KEY_ADDED_TO_CACHE =
            "Object with key = '{}' added to cache:  {{}}";

    public static final String OBJECT_WITH_KEY_RETRIEVED_FROM_CACHE =
            "Object with key '{}' retrieved from cache: {{}}";

    public static final String OBJECT_WITH_KEY_REMOVED_FROM_CACHE =
            "Object with key = '{}' removed from cache";

    private final Cache<String, NewsEntity> cache;

    /**
     * Pointcut for the save method in CrudRepository.
     */
    @Pointcut("execution(* org.springframework.data.repository.CrudRepository+.save(..))")
    public void anySaveMethods() {
    }

    /**
     * Pointcut for the findById method in CrudRepository.
     */
    @Pointcut("execution(* org.springframework.data.repository.CrudRepository+.findById(..))")
    public void anyFindByIdMethods() {
    }

    /**
     * Pointcut for the deleteById method in CrudRepository.
     */
    @Pointcut("execution(* org.springframework.data.repository.CrudRepository+.deleteById(..))")
    public void anyDeleteByIdMethods() {
    }

    /**
     * Intercepting a called save methods.
     */
    @Around("anySaveMethods()")
    public Object aroundSaveMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();

        if (result instanceof NewsEntity cacheValue) {
            String cacheKey = cacheValue.getId().toString();
            cache.put(cacheKey, cacheValue);
            log.info(OBJECT_WITH_KEY_ADDED_TO_CACHE, cacheKey, cacheValue);
        }

        return result;
    }

    /**
     * Intercepting a called read methods.
     */
    @Around("anyFindByIdMethods()")
    public Object aroundFindByIdMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        if (args.length != 1 || !(args[0] instanceof UUID)) {
            return joinPoint.proceed();
        }

        String cacheKey = args[0].toString();

        if (cache.contains(cacheKey)) {
            NewsEntity cacheValue = cache.get(cacheKey);
            log.info(OBJECT_WITH_KEY_RETRIEVED_FROM_CACHE, cacheKey, cacheValue);
            return Optional.ofNullable(cacheValue);
        }

        Optional<?> result = (Optional<?>) joinPoint.proceed();
        result.ifPresent(entity -> {
            if (entity instanceof NewsEntity cacheValue) {
                cache.put(cacheKey, cacheValue);
                log.info(OBJECT_WITH_KEY_ADDED_TO_CACHE, cacheKey, cacheValue);
            }
        });

        return result;
    }

    /**
     * Intercepting a called deletion methods.
     */
    @Around("anyDeleteByIdMethods()")
    public Object aroundDeleteMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        if (args.length != 1 || !(args[0] instanceof UUID)) {
            return joinPoint.proceed();
        }

        String cacheKey = args[0].toString();

        Object result = joinPoint.proceed();
        cache.delete(cacheKey);
        log.info(OBJECT_WITH_KEY_REMOVED_FROM_CACHE, cacheKey);

        return result;
    }

}
