package com.custom.config;


import io.sentry.Sentry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerExceptionResolver;

/**
 * Sentry 配置类
 * @author bllli
 */
@Configuration
public class SentryConfig {
    private static final Logger logger = LoggerFactory.getLogger(SentryConfig.class) ;

    @Value("${sentry.dsn}")
    private String sentryDSN;

    @Value("${sentry.need}")
    private boolean sentryNeed; // 客户的环境下，sentry地址被屏蔽了

    @Bean
    public HandlerExceptionResolver sentryExceptionResolver() {
        if (sentryNeed && sentryDSN != null && !sentryDSN.isEmpty()) {
            // 初始化sentry
            Sentry.init(sentryDSN);
            Sentry.capture("项目启动成功");
        } else {
            logger.error("sentry_dsn NOT CONFIG, errors will not send to your sentry server.");
        }
        return new io.sentry.spring.SentryExceptionResolver();
    }

    @Bean
    public ServletContextInitializer sentryServletContextInitializer() {
        return new io.sentry.spring.SentryServletContextInitializer();
    }
}

