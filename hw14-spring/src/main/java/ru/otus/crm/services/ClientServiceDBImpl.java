package ru.otus.crm.services;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.otus.crm.models.Client;
import ru.otus.crm.repositories.ClientRepository;
import ru.otus.sessionmanager.TransactionManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ClientServiceDBImpl implements ClientServiceDB {
    private static final Logger log = LoggerFactory.getLogger(ClientServiceDBImpl.class);

    private final TransactionManager transactionManager;
    private final ClientRepository clientRepository;

    @Override
    public Client saveClient(Client client) {
        return transactionManager.doInTransaction(() -> {
            var savedClient = clientRepository.save(client);
            log.info("saved client: {}", savedClient);
            return savedClient;
        });
    }

    @Override
    public Optional<Client> getClient(long id) {
        var clientOptional = clientRepository.findById(id);
        log.info("client: {}", clientOptional);
        return clientOptional;
    }

    @Override
    public List<Client> findAll() {
        //var clientList = new ArrayList<Client>(clientRepository.findAll());
        var clientList = new ArrayList<Client>(clientRepository.findAllByCustomQuery());
        log.info("clientList:{}", clientList);
        return clientList;
    }

    @Override
    public void deleteClient(Long id) {
        transactionManager.doInTransaction(() -> {
            clientRepository.deleteById(id);
            return null;
        });
    }
}