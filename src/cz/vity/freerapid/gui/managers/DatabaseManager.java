package cz.vity.freerapid.gui.managers;

import cz.vity.freerapid.utilities.LogUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public class DatabaseManager {
    private EntityManager entityManager;
    private final EntityManagerFactory factory;
    private final static Logger logger = Logger.getLogger(DatabaseManager.class.getName());


    public EntityManager getEntityManager() {
        return entityManager == null ? entityManager = factory.createEntityManager() : entityManager;
    }

    public DatabaseManager(ManagerDirector director) {
        final String path = new File(director.getContext().getLocalStorage().getDirectory(), "frd.odb").getAbsolutePath();
        logger.info("Database path " + path);
        factory = Persistence.createEntityManagerFactory(path);
        //hook to shutdown database on JVM close
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                if (entityManager != null) {
                    try {
                        entityManager.close();
                    } catch (Exception e) {
                        LogUtils.processException(logger, e);
                        //ignore
                    }
                }
                if (factory != null) {
                    try {
                        factory.close();
                    } catch (Exception e) {
                        LogUtils.processException(logger, e);
                        //ignore
                    }
                }
            }
        }));
    }

    public void saveCollection(Collection entityCollection) {
        final EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            for (Object o : entityCollection) {
                em.persist(o);
            }
            // Operations that modify the database should come here.
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        }
    }

    public void removeCollection(Collection entityCollection) {
        final EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            for (Object o : entityCollection) {
                em.remove(o);
            }
            // Operations that modify the database should come here.
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        }
    }


    public int removeAll(Class entityClass) {
        int affectedResult = 0;
        final EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            affectedResult = em.createQuery("DELETE FROM " + entityClass.getName()).executeUpdate();
            // Operations that modify the database should come here.
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        }
        return affectedResult;
    }

    public void saveOrUpdate(Object entity) {
        final EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            if (em.contains(entity)) {
                em.merge(entity);
            } else {
                em.persist(entity);
            }
            // Operations that modify the database should come here.
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        }
    }

    public <T> List<T> loadAll(Class<T> entityClass) {
        final EntityManager em = getEntityManager();
        final TypedQuery<T> query = em.createQuery("SELECT c FROM " + entityClass.getName() + "  c", entityClass);
        return query.getResultList();
    }
}
