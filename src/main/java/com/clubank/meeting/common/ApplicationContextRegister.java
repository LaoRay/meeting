package com.clubank.meeting.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class ApplicationContextRegister implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    /**
     * 设置spring上下文
     *
     * @param applicationContext spring上下文
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        log.info("ApplicationContext registed-->{}", applicationContext);
        this.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static <T> T popBean(Class<T> clazz) {
        return Optional.ofNullable(applicationContext)
                .map(context -> context.getBean(clazz)).orElse(null);
    }

    public static <T> T popBean(String name, Class<T> clazz) {
        return Optional.ofNullable(applicationContext)
                .map(context -> context.getBean(name, clazz)).orElse(null);
    }
}
