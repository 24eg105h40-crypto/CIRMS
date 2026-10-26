package com.cirms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Enables Spring's @Async support so that MatchingServiceImpl.triggerMatchingAndNotify()
 * runs in a separate thread pool and does not block item creation responses.
 */
@Configuration
@EnableAsync
public class AsyncConfig {
}
