package ru.otus.core.repository;

import org.hibernate.Session;
import ru.otus.crm.model.User;

import java.util.Optional;

public interface UserDataTemplate {
    public Optional<User> findByLogin(Session session, String login);
}
