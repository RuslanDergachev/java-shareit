package ru.practicum.shareit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = {"ru.practicum.shareit.booking.repository"
		,"ru.practicum.shareit.comments.repository", "ru.practicum.shareit.item.repository"
		,"ru.practicum.shareit.request.repository", "ru.practicum.shareit.user.repository"})

public class ShareItServer {

	public static void main(String[] args) {
		SpringApplication.run(ShareItServer.class, args);
	}
}
