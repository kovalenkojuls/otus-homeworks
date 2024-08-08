package ru.otus.crm.models;

import jakarta.annotation.Nonnull;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;
import java.util.Set;

@Builder
@Table("clients")
public record Client(@Id Long id,
                     @Nonnull String name,
                     @MappedCollection(idColumn = "client_id") Address address,
                     @MappedCollection(idColumn = "client_id") Set<Phone> phones) {

    public Client() {
        this(null, null, null, null);
    }
}
