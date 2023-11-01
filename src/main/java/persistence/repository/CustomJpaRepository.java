package persistence.repository;

import persistence.entity.EntityManager;

public class CustomJpaRepository<T, ID> {
    private final Class<T> entityClass;
    private final EntityManager entityManager;

    public CustomJpaRepository(EntityManager entityManager, Class<T> entityClass) {
        this.entityClass = entityClass;
        this.entityManager = entityManager;
    }

    public T findById(ID id) {
        return entityManager.find(entityClass, id);
    }

    public T save(T t) {
        return entityManager.persist(t);
    }

    public void delete(ID id) {
        entityManager.remove(entityClass, id);
        commit();
    }

    public void commit() {
        entityManager.flush();
    }
}
