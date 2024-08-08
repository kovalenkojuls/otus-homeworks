package ru.otus.crm.services;

import java.util.List;
import java.util.Optional;
import ru.otus.crm.models.Client;

public interface ClientServiceDB {
    Client saveClient(Client client);

    void deleteClient(Long id);

    Optional<Client> getClient(long id);

    List<Client> findAll();
}