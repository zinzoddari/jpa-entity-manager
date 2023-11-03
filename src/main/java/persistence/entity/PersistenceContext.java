package persistence.entity;

import domain.Snapshot;
import java.util.Map;

public interface PersistenceContext {

    Object getEntity(Integer id);

    void addEntity(Integer key, Object id, Object entity);

    void addEntity(Integer key, Snapshot snapshot);

    void removeEntity(Integer id);

    boolean isEntityInSnapshot(Integer id);

    boolean isEntityInContext(Integer id);

    <T, I> Snapshot getDatabaseSnapshot(Integer key, EntityPersister<T> persister, I input);

    Map<Integer, Snapshot> comparison();
}
