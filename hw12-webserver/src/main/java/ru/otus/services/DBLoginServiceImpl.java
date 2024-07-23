package ru.otus.services;

import org.eclipse.jetty.security.AbstractLoginService;
import org.eclipse.jetty.security.RolePrincipal;
import org.eclipse.jetty.security.UserPrincipal;
import org.eclipse.jetty.util.security.Password;
import ru.otus.core.repository.UserDataTemplate;
import ru.otus.core.sessionmanager.TransactionManager;
import ru.otus.crm.model.User;

import java.util.List;
import java.util.Optional;

public class DBLoginServiceImpl extends AbstractLoginService {
    private final UserDataTemplate userDataTemplate;
    private final TransactionManager transactionManager;

    public DBLoginServiceImpl(TransactionManager transactionManager, UserDataTemplate userDataTemplate) {
        this.transactionManager = transactionManager;
        this.userDataTemplate = userDataTemplate;
    }

    @Override
    protected List<RolePrincipal> loadRoleInfo(UserPrincipal userPrincipal) {
        return List.of(new RolePrincipal("admin"));
    }

    @Override
    protected UserPrincipal loadUserInfo(String login) {
        return transactionManager.doInReadOnlyTransaction(session -> {
            Optional<User> user = userDataTemplate.findByLogin(session, login);
            return user.map(u -> new UserPrincipal(u.getLogin(), new Password(u.getPassword())))
                    .orElse(null);
        });
    }
}