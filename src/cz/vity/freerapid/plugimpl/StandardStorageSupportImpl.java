package cz.vity.freerapid.plugimpl;

import cz.vity.freerapid.plugins.webclient.interfaces.ConfigurationStorageSupport;
import cz.vity.freerapid.utilities.LogUtils;
import org.jdesktop.application.ApplicationContext;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.util.logging.Logger;

/**
 * Standard implementation for
 *
 * @author Ladislav Vitasek
 */
public class StandardStorageSupportImpl implements ConfigurationStorageSupport {
    private final static Logger logger = Logger.getLogger(StandardStorageSupportImpl.class.getName());

    private final ApplicationContext context;

    /**
     * Constructor
     *
     * @param context application context
     */
    public StandardStorageSupportImpl(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public boolean configFileExists(String fileName) {
        final File file = new File(context.getLocalStorage().getDirectory(), fileName);
        return file.isFile() && file.exists();
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public <E> E loadConfigFromFile(String fileName, Class<E> type) throws Exception {
        XMLDecoder xmlDecoder = null;
        try {
            xmlDecoder = new XMLDecoder(context.getLocalStorage().openInputFile(fileName), null, null, type.getClassLoader());
            return (E) xmlDecoder.readObject();
        } catch (RuntimeException e) {
            LogUtils.processException(logger, e);
            throw new Exception(e);
        } catch (Exception e) {
            LogUtils.processException(logger, e);
            throw e;
        } finally {
            if (xmlDecoder != null) {
                try {
                    xmlDecoder.close();
                } catch (Exception e) {
                    //ignore
                }
            }
        }
    }

    @Override
    public void storeConfigToFile(Object object, String fileName) throws Exception {
        ClassLoader threadCL = null;
        try {
            threadCL = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(object.getClass().getClassLoader());
            XMLEncoder xmlEncoder = null;
            try {
                xmlEncoder = new XMLEncoder(context.getLocalStorage().openOutputFile(fileName));
                xmlEncoder.writeObject(object);
            } catch (Exception e) {
                LogUtils.processException(logger, e);
                throw e;
            } finally {
                if (xmlEncoder != null) {
                    try {
                        xmlEncoder.close();
                    } catch (Exception e) {
                        LogUtils.processException(logger, e);
                    }
                }
            }
        } finally {
            if (threadCL != null)
                Thread.currentThread().setContextClassLoader(threadCL);
        }
    }

}
