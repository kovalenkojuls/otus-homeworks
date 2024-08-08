package ru.otus.crm.models;

import jakarta.annotation.Nonnull;
import lombok.Builder;
import org.springframework.data.relational.core.mapping.Table;

@Table("addresses")
@Builder
public record Address(@Nonnull String street,
                      Long clientId) {}
