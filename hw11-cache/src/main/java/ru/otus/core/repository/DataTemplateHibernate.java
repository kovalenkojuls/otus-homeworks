package ru.otus.core.repository;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.Id;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.cache.HWCacheDemo;
import ru.otus.cache.HwListener;
import ru.otus.cache.MyCache;

public class DataTemplateHibernate<T> implements DataTemplate<T> {
    private static final Logger logger = LoggerFactory.getLogger(DataTemplateHibernate.class);
    private final Class<T> clazz;
    private final MyCache<String, T> cache = new MyCache<>();
    private final Field idField;

    public DataTemplateHibernate(Class<T> clazz) {
        this.clazz = clazz;
        idField = getIdField();

        HwListener<String, T> listener = new HwListener<String, T>() {
            @Override
            public void notify(String key, T object, String action) {
                logger.info("MyCache: object.id:{}, action:{}", key, action);
            }
        };

        cache.addListener(listener);
    }

    @Override
    public Optional<T> findById(Session session, long id) {
        T objFromCache = cache.get(String.valueOf(id));
        if (objFromCache != null) {
            return Optional.ofNullable(objFromCache);
        }
        return Optional.ofNullable(session.find(clazz, id));
    }

    @Override
    public List<T> findByEntityField(Session session, String entityFieldName, Object entityFieldValue) {
        var criteriaBuilder = session.getCriteriaBuilder();
        var criteriaQuery = criteriaBuilder.createQuery(clazz);
        var root = criteriaQuery.from(clazz);
        criteriaQuery.select(root).where(criteriaBuilder.equal(root.get(entityFieldName), entityFieldValue));

        var query = session.createQuery(criteriaQuery);
        return query.getResultList();
    }

    @Override
    public List<T> findAll(Session session) {
        return session.createQuery(String.format("from %s", clazz.getSimpleName()), clazz)
                .getResultList();
    }

    @Override
    public T insert(Session session, T object) {
        session.persist(object);
        try {
            cache.put(idField.get(object).toString(), object);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return object;
    }

    @Override
    public T update(Session session, T object) {
        var updatedObj = session.merge(object);
        try {
            cache.put(idField.get(updatedObj).toString(), updatedObj);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return updatedObj;
    }

    private Field getIdField() {
        for (Field field: clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                field.setAccessible(true);
                return field;
            }
        }
        throw new RuntimeException("no id field in class " + clazz.getName());
    }
}
