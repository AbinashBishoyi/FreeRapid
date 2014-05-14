package cz.vity.freerapid.gui.managers;

import cz.vity.freerapid.core.tasks.CoreTask;
import cz.vity.freerapid.gui.managers.interfaces.Identifiable;
import cz.vity.freerapid.swing.Swinger;
import cz.vity.freerapid.utilities.LogUtils;
import org.jdesktop.application.TaskService;

import javax.persistence.*;
import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public class DatabaseManager {
    private final static Logger logger = Logger.getLogger(DatabaseManager.class.getName());
    private final ManagerDirector director;
    private final EntityManagerFactory factory;

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
                    em.remove(removeObject);
                }
            }
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
        runOnTask(runnable, null);
    }

    public void runOnTask(final Runnable runnable, final Runnable succeeded) {
        final TaskService service = director.getTaskServiceManager().getTaskService(TaskServiceManager.DATABASE_SERVICE);

        service.execute(new CoreTask(director.getContext().getApplication()) {
            @Override
            protected void succeeded(Object result) {
                if (succeeded != null) {
                    succeeded.run();
                }
            }

            @Override
            protected Object doInBackground() throws Exception {
                runnable.run();
                return null;
            }

            @Override
            protected void failed(Throwable cause) {
                LogUtils.processException(logger, cause);
                if (cause instanceof PersistenceException && cause
                        .getMessage().contains("error 141")) {
                    Swinger.showErrorDialog(director.getContext().getResourceMap(), "DatabaseManager.databaseIsAlreadyUsed", cause);
                } else super.failed(cause);
            }
        });
    }

}
