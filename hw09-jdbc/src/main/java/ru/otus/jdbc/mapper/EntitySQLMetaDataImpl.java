package ru.otus.jdbc.mapper;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

public class EntitySQLMetaDataImpl implements EntitySQLMetaData {
    private final EntityClassMetaData<?> entityClassMetaData;

    public EntitySQLMetaDataImpl(EntityClassMetaData<?> entityClassMetaData) {
        this.entityClassMetaData = entityClassMetaData;
    }

    @Override
    public String getSelectAllSql() {
        return String.format("SELECT * FROM %s",
                entityClassMetaData.getName());
    }

    @Override
    public String getSelectByIdSql() {
        return String.format("SELECT * FROM %s WHERE %s = ?",
                entityClassMetaData.getName(),
                entityClassMetaData.getIdField().getName());
    }

    @Override
    public String getInsertSql() {
        List<Field> fieldsWithoutId = entityClassMetaData.getFieldsWithoutId();

        String columns = fieldsWithoutId
                .stream()
                .map(Field::getName)
                .collect(Collectors.joining(", "));

        String values = fieldsWithoutId
                .stream()
                .map(f -> "?")
                .collect(Collectors.joining(","));

        return String.format("INSERT INTO %s(%s) VALUES (%s)",
                entityClassMetaData.getName(),
                columns,
                values);
    }

    @Override
    public String getUpdateSql() {
        String values = entityClassMetaData
                .getFieldsWithoutId()
                .stream()
                .map(f -> f.getName() + " = ?")
                .collect(Collectors.joining(", "));

        return String.format("UPDATE %s SET %s WHERE %s = ?",
                entityClassMetaData.getName(),
                values,
                entityClassMetaData.getIdField().getName());
    }
}
