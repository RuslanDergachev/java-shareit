package ru.practicum.shareit.source;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories("ru.practicum.shareIt")
public class DbConfig {
}
