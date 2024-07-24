package ru.otus.core.repository;

import org.hibernate.Session;
import ru.otus.crm.model.User;

import java.util.Optional;

public class UserDataTemplateImpl implements UserDataTemplate {
    @Override
    public Optional<User> findByLogin(Session session, String login) {
        var query = session.createQuery(
                "select u " +
                "from User u " +
                "where u.login = :login", User.class);

        query.setParameter("login", login);
        return Optional.ofNullable(query.getResultList().getFirst());
    }
}
