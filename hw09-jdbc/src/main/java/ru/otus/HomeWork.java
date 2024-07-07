package ru.otus;

import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.core.repository.executor.DbExecutor;
import ru.otus.core.repository.executor.DbExecutorImpl;
import ru.otus.core.sessionmanager.TransactionRunner;
import ru.otus.core.sessionmanager.TransactionRunnerJdbc;
import ru.otus.crm.datasource.DriverManagerDataSource;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Manager;
import ru.otus.crm.service.DbServiceClientImpl;
import ru.otus.crm.service.DbServiceManagerImpl;
import ru.otus.jdbc.mapper.*;

import java.util.List;

public class HomeWork {
    private static final String URL = "jdbc:postgresql://localhost:5430/demoDB";
    private static final String USER = "usr";
    private static final String PASSWORD = "pwd";

    private static final Logger log = LoggerFactory.getLogger(HomeWork.class);

    public static void main(String[] args) {
        var dataSource = new DriverManagerDataSource(URL, USER, PASSWORD);
        flywayMigrations(dataSource);

        var transactionRunner = new TransactionRunnerJdbc(dataSource);
        var dbExecutor = new DbExecutorImpl();

        log.info("\n\n---- DEMO Client:");
        demoClient(dbExecutor, transactionRunner);
        log.info("\n\n---- DEMO Manager:");
        demoManager(dbExecutor, transactionRunner);
    }

    private static void demoClient(DbExecutor dbExecutor, TransactionRunner transactionRunner) {
        var entityClassMetaDataClient = new EntityClassMetaDataImpl<Client>(Client.class);
        var entitySQLMetaDataClient  = new EntitySQLMetaDataImpl(entityClassMetaDataClient);
        var dataTemplateClient = new DataTemplateJdbc<Client>(dbExecutor,
                entitySQLMetaDataClient,
                entityClassMetaDataClient);

        var dbServiceClient = new DbServiceClientImpl(transactionRunner, dataTemplateClient);
        dbServiceClient.saveClient(new Client("dbServiceFirst"));

        var clientSecond = dbServiceClient.saveClient(new Client("dbServiceSecond"));
        var clientSecondSelected = dbServiceClient
                .getClient(clientSecond.getId())
                .orElseThrow(() -> new RuntimeException("Client not found, id:" + clientSecond.getId()));
        log.info("clientSecondSelected:{}", clientSecondSelected);

        Client clientThird = new Client("ClientThird");
        clientThird = dbServiceClient.saveClient(clientThird);
        clientThird.setName("UpdatedName");
        dbServiceClient.saveClient(clientThird);

        List<Client> allClients = dbServiceClient.findAll();
    }

    private static void demoManager(DbExecutor dbExecutor, TransactionRunner transactionRunner) {
        var entityClassMetaDataManager = new EntityClassMetaDataImpl<Manager>(Manager.class);
        var entitySQLMetaDataManager = new EntitySQLMetaDataImpl(entityClassMetaDataManager);
        var dataTemplateManager = new DataTemplateJdbc<Manager>(dbExecutor,
                entitySQLMetaDataManager,
                entityClassMetaDataManager);

        var dbServiceManager = new DbServiceManagerImpl(transactionRunner, dataTemplateManager);
        dbServiceManager.saveManager(new Manager("ManagerFirst"));

        var managerSecond = dbServiceManager.saveManager(new Manager("ManagerSecond"));
        var managerSecondSelected = dbServiceManager
                .getManager(managerSecond.getNo())
                .orElseThrow(() -> new RuntimeException("Manager not found, id:" + managerSecond.getNo()));
        log.info("managerSecondSelected:{}", managerSecondSelected);

        Manager managerThird = new Manager("ManagerThird");
        managerThird = dbServiceManager.saveManager(managerThird);
        managerThird.setLabel("UpdatedLabel");
        managerThird.setParam1("newParam1");
        dbServiceManager.saveManager(managerThird);

        List<Manager> allClients = dbServiceManager.findAll();
    }

    private static void flywayMigrations(DataSource dataSource) {
        log.info("db migration started...");
        var flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:/db/migration")
                .load();
        flyway.migrate();
        log.info("db migration finished.");
        log.info("***");
    }
}
