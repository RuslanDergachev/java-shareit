package ru.practicum.shareIt.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareIt.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
