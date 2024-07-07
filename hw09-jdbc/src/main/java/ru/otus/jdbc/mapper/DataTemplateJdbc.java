package ru.otus.jdbc.mapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import ru.otus.core.repository.DataTemplate;
import ru.otus.core.repository.DataTemplateException;
import ru.otus.core.repository.executor.DbExecutor;

public class DataTemplateJdbc<T> implements DataTemplate<T> {
    private final DbExecutor dbExecutor;
    private final EntitySQLMetaData entitySQLMetaData;
    private final EntityClassMetaData<T> entityClassMetaData;

    public DataTemplateJdbc(DbExecutor dbExecutor,
                            EntitySQLMetaData entitySQLMetaData,
                            EntityClassMetaData<T> entityClassMetaData) {
        this.dbExecutor = dbExecutor;
        this.entitySQLMetaData = entitySQLMetaData;
        this.entityClassMetaData = entityClassMetaData;
    }

    @Override
    public Optional<T> findById(Connection connection, long id) {
        return dbExecutor.executeSelect(
                connection,
                entitySQLMetaData.getSelectByIdSql(),
                List.of(id),
                rs -> {
                    try {
                        if (!rs.next()) {
                            return null;
                        }
                        return fillNewInstance(rs);
                    } catch (SQLException e) {
                        throw new DataTemplateException(e);
                    }
                });
    }

    @Override
    public List<T> findAll(Connection connection) {
        Function<ResultSet, List<T>> rsHandler = rs -> {
            try {
                List<T> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(fillNewInstance(rs));
                }
                return result;
            } catch (SQLException e) {
                throw new DataTemplateException(e);
            }
        };

        return dbExecutor.executeSelect(
                connection,
                entitySQLMetaData.getSelectAllSql(),
                List.of(),
                rsHandler).orElse(List.of());
    }

    @Override
    public long insert(Connection connection, T client) {
        var params = new ArrayList<Object>();
        for (Field field: entityClassMetaData.getFieldsWithoutId()) {
            field.setAccessible(true);
            try {
                params.add(field.get(client));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        return dbExecutor.executeStatement(connection,
                entitySQLMetaData.getInsertSql(),
                params);
    }

    @Override
    public void update(Connection connection, T client) {
        List<Object> params = new ArrayList<>();

        try {
            for (Field field: entityClassMetaData.getFieldsWithoutId()) {
                field.setAccessible(true);
                params.add(field.get(client));
            }

            Field fieldId = entityClassMetaData.getIdField();
            fieldId.setAccessible(true);
            params.add(fieldId.get(client));
        } catch (Exception e) {
            throw new DataTemplateException(e);
        }

        dbExecutor.executeStatement(
                connection,
                entitySQLMetaData.getUpdateSql(),
                params);
    }

    private T fillNewInstance(ResultSet rs) {
        try {
            Constructor<T> constructor = entityClassMetaData.getConstructor();
            constructor.setAccessible(true);
            T instance = constructor.newInstance();

            for (Field field: entityClassMetaData.getAllFields()) {
                field.setAccessible(true);
                field.set(instance, rs.getObject(field.getName()));
            }

            return instance;
        } catch (Exception e) {
            throw new DataTemplateException(e);
        }
    }
}