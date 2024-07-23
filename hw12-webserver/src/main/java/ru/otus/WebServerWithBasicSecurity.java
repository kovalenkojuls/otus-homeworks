package ru.otus;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.core.repository.DataTemplateHibernate;
import ru.otus.core.repository.HibernateUtils;
import ru.otus.core.repository.UserDataTemplateImpl;
import ru.otus.core.sessionmanager.TransactionManagerHibernate;
import ru.otus.crm.dbmigrations.MigrationsExecutorFlyway;
import ru.otus.crm.model.Address;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;
import ru.otus.crm.model.User;
import ru.otus.crm.service.DbServiceClientImpl;
import ru.otus.server.UserWebServerImpl;
import ru.otus.server.UsersWebServer;
import ru.otus.services.DBLoginServiceImpl;
import ru.otus.services.TemplateProcessorImpl;

public class WebServerWithBasicSecurity {
    private static final Logger log = LoggerFactory.getLogger(WebServerWithBasicSecurity.class);

    private static final int WEB_SERVER_PORT = 8080;
    public static final String HIBERNATE_CFG_FILE = "hibernate.cfg.xml";
    private static final String TEMPLATES_DIR = "/templates/";

    public static void main(String[] args) throws Exception {
        var configuration = new Configuration().configure(HIBERNATE_CFG_FILE);

        var dbUrl = configuration.getProperty("hibernate.connection.url");
        var dbUserName = configuration.getProperty("hibernate.connection.username");
        var dbPassword = configuration.getProperty("hibernate.connection.password");

        new MigrationsExecutorFlyway(dbUrl, dbUserName, dbPassword).executeMigrations();

        var sessionFactory = HibernateUtils.buildSessionFactory(configuration,
                Client.class,
                Address.class,
                Phone.class,
                User.class);

        UsersWebServer usersWebServer = getUsersWebServer(sessionFactory);
        usersWebServer.join();
    }

    private static UsersWebServer getUsersWebServer(SessionFactory sessionFactory) throws Exception {
        var transactionManager = new TransactionManagerHibernate(sessionFactory);
        var clientTemplate = new DataTemplateHibernate<>(Client.class);
        var dbServiceClient = new DbServiceClientImpl(transactionManager, clientTemplate);
        var userDataTemplate = new UserDataTemplateImpl();
        var dbLoginService = new DBLoginServiceImpl(transactionManager, userDataTemplate);
        var templateProcessor = new TemplateProcessorImpl(TEMPLATES_DIR);

        UsersWebServer usersWebServer = new UserWebServerImpl(WEB_SERVER_PORT,
                templateProcessor,
                dbLoginService,
                dbServiceClient);

        usersWebServer.start();
        return usersWebServer;
    }
}
