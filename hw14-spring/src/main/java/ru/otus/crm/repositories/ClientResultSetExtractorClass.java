package ru.otus.crm.repositories;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import ru.otus.crm.models.Address;
import ru.otus.crm.models.Client;
import ru.otus.crm.models.Phone;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ClientResultSetExtractorClass implements ResultSetExtractor<List<Client>> {
    @Override
    public List<Client> extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<Client> clients = new ArrayList<>();
        Long prevClientId = null;
        Client prevClient = null;

        while (rs.next()) {
            Long clientId = rs.getLong("client_id");
            String name = rs.getString("name");
            Long phoneId = rs.getLong("phone_id");
            String phoneNumber = rs.getString("phone_number");
            String addressStreet = rs.getString("address_street");

            Client client = null;

            if (prevClientId == null || !prevClientId.equals(clientId)) {
                prevClientId = clientId;

                var address = Address.builder()
                        .clientId(clientId)
                        .street(addressStreet)
                        .build();

                prevClient = client = Client.builder()
                        .id(clientId)
                        .name(name)
                        .address(address)
                        .phones(new HashSet<>()).build();

                clients.add(client);
            }

            client = client != null ? client : prevClient;

            if (client != null) {
                Phone phone = Phone.builder()
                        .id(phoneId)
                        .clientId(clientId)
                        .number(phoneNumber)
                        .build();

                client.phones().add(phone);
            }
        }
        return clients;
    }
}
