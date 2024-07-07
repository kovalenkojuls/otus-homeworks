package ru.otus.jdbc.mapper;

import ru.otus.crm.model.annotation.Id;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EntityClassMetaDataImpl<T> implements EntityClassMetaData<T> {
    private final Class<T> clazz;
    private Field idField = null;
    private List<Field> fieldsWithoutId = null;
    private List<Field> allFields = null;

    public EntityClassMetaDataImpl(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public String getName() {
        return clazz.getSimpleName().toLowerCase();
    }

    @Override
    public Constructor<T> getConstructor() {
        try {
            return clazz.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Field getIdField() {
        if (idField != null) {
            return idField;
        }
        
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                idField = field;
                return field;
            }
        }
        
        throw new RuntimeException("the id field was not found in class " + clazz.getName());
    }

    @Override
    public List<Field> getAllFields() {
        if (allFields == null) {
            allFields = Arrays.asList(clazz.getDeclaredFields());
        }
        return allFields;
    }

    @Override
    public List<Field> getFieldsWithoutId() {
        if (this.fieldsWithoutId != null) {
            return this.fieldsWithoutId;
        }

        List<Field> fieldsWithoutId = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) continue;
            fieldsWithoutId.add(field);
        }

        this.fieldsWithoutId = fieldsWithoutId;
        return this.fieldsWithoutId;
    }
}
