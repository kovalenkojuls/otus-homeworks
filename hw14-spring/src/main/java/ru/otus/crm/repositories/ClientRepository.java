package ru.otus.crm.repositories;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import ru.otus.crm.models.Client;

import java.util.List;
import java.util.Optional;

public interface ClientRepository extends ListCrudRepository<Client, Long> {
    @Override
    List<Client> findAll();

    @Override
    void deleteById(Long id);

    @Override
    Optional<Client> findById(Long id);

    @Query(value = """
                select  c.id as client_id,
                        c.name as name,
                        p.id as phone_id,
                        p.number as phone_number,
                        a.street as address_street
                        from clients c
                            left join phones p on c.id = p.client_id
                            left join addresses a on c.id = a.client_id
                        order by c.id
            """, resultSetExtractorClass = ClientResultSetExtractorClass.class)
    List<Client> findAllByCustomQuery();
}
