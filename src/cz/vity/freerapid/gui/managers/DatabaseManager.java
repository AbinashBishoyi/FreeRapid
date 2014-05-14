package cz.vity.freerapid.gui.managers;

import cz.vity.freerapid.gui.managers.interfaces.Identifiable;
import cz.vity.freerapid.utilities.LogUtils;
import org.jdesktop.application.Task;
import org.jdesktop.application.TaskService;

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
    private final EntityManagerFactory factory;
    private final static Logger logger = Logger.getLogger(DatabaseManager.class.getName());
    private ManagerDirector director;


    public EntityManager getEntityManager() {
        return factory.createEntityManager();
    }

    public DatabaseManager(ManagerDirector director) {
        this.director = director;
        final String path = new File(director.getContext().getLocalStorage().getDirectory(), "frd.odb").getAbsolutePath();
        logger.info("Database path " + path);
        factory = Persistence.createEntityManagerFactory(path);
        //hook to shutdown database on JVM close
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
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

    public synchronized void saveCollection(Collection<? extends Identifiable> entityCollection) {
        final EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();

            for (Identifiable o : entityCollection) {
                if (o.getIdentificator() == null) {
                    em.persist(o);
                } else {
                    em.merge(o);
                }
            }
            // Operations that modify the database should come here.
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            em.close();
        }
    }

    public synchronized <T extends Identifiable> void removeCollection(Collection<T> entityCollection) {
        final EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            for (T o : entityCollection) {
                if (o.getIdentificator() == null) {
                    continue;
                }
                Object removeObject = em.find(o.getClass(), o.getIdentificator());
                if (removeObject != null) {
                    //em.refresh(removeObject);
                    em.remove(removeObject);
                }
            }
            // Operations that modify the database should come here.
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            em.close();
        }
    }


    public synchronized int removeAll(Class entityClass) {
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
            em.close();
        }
        return affectedResult;
    }

    public synchronized void saveOrUpdate(Identifiable entity) {
        final EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            if (entity.getIdentificator() == null) {
                em.persist(entity);
            } else {
                em.merge(entity);
            }
            // Operations that modify the database should come here.
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive()) {
                try {
                    em.getTransaction().rollback();
                } catch (Exception e) {
                    //ignore
                }
            }
            em.close();
        }
    }

    public synchronized <T> List<T> loadAll(Class<T> entityClass) {
        return loadAll(entityClass, null);
    }

    public synchronized <T> List<T> loadAll(Class<T> entityClass, String orderBy) {
        final EntityManager em = getEntityManager();
        try {
            final TypedQuery<T> query = em.createQuery("SELECT c FROM " + entityClass.getName() + "  c " + ((orderBy == null) ? "" : " ORDER BY " + orderBy), entityClass);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public void runOnTask(final Runnable runnable) {
        final TaskService service = director.getTaskServiceManager().getTaskService(TaskServiceManager.DATABASE_SERVICE);
        service.execute(new Task(director.getContext().getApplication()) {
            protected Object doInBackground() throws Exception {
                Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
                runnable.run();
                return null;
            }

            @Override
            protected void failed(Throwable cause) {
                LogUtils.processException(logger, cause);
            }
        });
    }


}
