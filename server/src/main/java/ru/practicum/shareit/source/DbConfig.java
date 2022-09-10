package ru.practicum.shareit.source;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = {"ru.practicum.shareit.booking.repository"
        ,"ru.practicum.shareit.comments.repository", "ru.practicum.shareit.item.repository"
        ,"ru.practicum.shareit.request.repository", "ru.practicum.shareit.user.repository"})
public class DbConfig {
}
