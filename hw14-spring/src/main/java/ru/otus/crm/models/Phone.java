package ru.otus.crm.models;

import jakarta.annotation.Nonnull;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Builder
@Table("phones")
public record Phone(
        @Id Long id,
        @Nonnull String number,
        Long clientId) {

    public Phone(String number) {
        this(null, number, null);
    }
}
