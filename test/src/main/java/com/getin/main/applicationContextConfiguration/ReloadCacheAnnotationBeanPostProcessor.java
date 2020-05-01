package com.getin.main.applicationContextConfiguration;

import com.wellcome.main.annotations.ReloadCache;
import com.wellcome.main.component.CacheReloader;
import com.wellcome.main.configuration.utils.ThreadCache;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReloadCacheAnnotationBeanPostProcessor implements BeanPostProcessor {

    private final ThreadCache threadCache;

    private final CacheReloader cacheReloader;

    private final Executor cacheReloaderExecutor;

    private final Map<String, ImmutablePair<Class, List<String>>> beanMap;

    public ReloadCacheAnnotationBeanPostProcessor(final ThreadCache threadCache,
                                                  final CacheReloader cacheReloader,
                                                  final Executor cacheReloaderExecutor) {
        this.threadCache = threadCache;
        this.cacheReloader = cacheReloader;
        this.cacheReloaderExecutor = cacheReloaderExecutor;
        this.beanMap = new HashMap<>();
    }

    @Override
    public Object postProcessBeforeInitialization(@NotNull Object bean, String beanName) throws BeansException {
        final Class<?> originalBeanClass = bean.getClass();
        final List<String> annotatedMethodList = Stream.of(originalBeanClass.getMethods())
                .filter(it -> it.isAnnotationPresent(ReloadCache.class))
                .map(Method::getName)
                .collect(Collectors.toList());

        if (!annotatedMethodList.isEmpty())
            beanMap.put(beanName, new ImmutablePair<>(originalBeanClass, annotatedMethodList));

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(@NotNull Object bean, String beanName) throws BeansException {
        final ImmutablePair<Class, List<String>> originalBeanData = beanMap.get(beanName);
        if (originalBeanData != null) {
            return Proxy.newProxyInstance(originalBeanData.left.getClassLoader(),
                    originalBeanData.left.getInterfaces(),
                    (object, method, args) -> {
                        if (originalBeanData.right.contains(method.getName())) {
                            final Object retObj = method.invoke(bean, args);
                            final Long cacheId = threadCache.getLocalityIdRequestThreadLocal().get();
                            cacheReloaderExecutor.execute(() ->
                                    cacheReloader.reloadInstitutionServiceCache(cacheId));
                            return retObj;
                        } else return method.invoke(bean, args);
                    });
        }
        return bean;
    }
}
