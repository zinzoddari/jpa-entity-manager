package persistence.entity;

import java.util.List;
import java.util.Map;

public class EntityManagerImpl implements EntityManager {
    private final PersistenceContext persistenceContext;
    private final Map<String, EntityPersister<?>> persisterMap;

    EntityManagerImpl(Map<String, EntityPersister<?>> persisterMap) {
        this.persisterMap = persisterMap;
        this.persistenceContext = new PersistenceContextImpl();
    }

    @Override
    public <T> List<T> findAll(Class<T> tClazz) {
        return (List<T>) getPersister(tClazz).findAll();
    }

    @Override
    public <R, I> R find(Class<R> rClass, I input) {
        final EntityPersister<?> persister = getPersister(rClass);
        int key = persister.getHashCode(input);

        if(!persistenceContext.isEntityInContext(key)) {
            persistenceContext.addEntity(key, input, persistenceContext.getDatabaseSnapshot(key, persister, input));
        }

        return (R) persistenceContext.getEntity(key);
    }

    @Override
    public <T> T persist(T t) {
        final EntityPersister<?> persister = getPersister(t.getClass());
        Object id = persister.getIdValue(t);
        int key = persister.getHashCode(id);

        if(!persistenceContext.isEntityInContext(key)) {
            getPersister(t.getClass()).insert(t);
            persistenceContext.getDatabaseSnapshot(key, persister, id);
        }

        persistenceContext.addEntity(key, id, t);

        return (T) persistenceContext.getEntity(key);
    }

    @Override
    public <T> void remove(T t, Object arg) {
        final EntityPersister<?> persister = getPersister(t.getClass());
        int id = persister.getHashCode(arg);

        persistenceContext.removeEntity(id);
    }

    public void flush() {
        persistenceContext.comparison().forEach((id, data) -> {
            if(!persistenceContext.isEntityInContext(id)) {
                getPersister(data.getObjectClass()).delete(data.getId());
                return;
            }

            getPersister(data.getObjectClass()).update(data, data.getId());
        });
    }

    private <T> EntityPersister<?> getPersister(Class<T> tClass) {
        return persisterMap.get(tClass.getName());
    }
}
