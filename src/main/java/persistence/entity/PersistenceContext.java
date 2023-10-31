package persistence.entity;

import domain.Snapshot;
import java.util.Map;

public interface PersistenceContext {
    Object getEntity(Integer id);

    void addEntity(Integer key, Object id, Object entity);

    void removeEntity(Integer id);

    boolean isValidEntity(Integer id);

    <T, I> void getDatabaseSnapshot(Integer key, EntityPersister<T> persister, I input);

    Map<Integer, Snapshot> comparison();
}
