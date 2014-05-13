package cz.vity.freerapid.utilities;

import cz.vity.freerapid.model.DownloadFile;
import org.jdesktop.application.ApplicationContext;

import java.beans.*;
import java.io.*;
import java.net.URL;

/**
 * @author Ladislav Vitasek
 */
public class XMLUtils {
    private ApplicationContext context;

    public XMLUtils(ApplicationContext context) {
        this.context = context;
    }

    /* If an exception occurs in the XMLEncoder/Decoder, we want
    * to throw an IOException.  The exceptionThrow listener method
    * doesn't throw a checked exception so we just set a flag
    * here and check it when the encode/decode operation finishes
    */
    private static class AbortExceptionListener implements ExceptionListener {
        public Exception exception = null;

        public void exceptionThrown(Exception e) {
            if (exception == null) {
                exception = e;
            }
        }
    }

    private static boolean persistenceDelegatesInitialized = false;

    public void save(Object bean, final String fileName) throws IOException {
        AbortExceptionListener el = new AbortExceptionListener();
        XMLEncoder e = null;
        /* Buffer the XMLEncoder's output so that decoding errors don't
       * cause us to trash the current version of the specified file.
       */
        ByteArrayOutputStream bst = new ByteArrayOutputStream();
        try {
            e = new XMLEncoder(bst);
            initDelegates(e);
            e.setExceptionListener(el);
            e.writeObject(bean);
        }
        finally {
            if (e != null) {
                e.close();
            }
        }
        if (el.exception != null) {
            throw new LSException("save failed \"" + fileName + "\"", el.exception);
        }
        OutputStream ost = null;
        try {
            ost = openOutputFile(fileName);
            ost.write(bst.toByteArray());
        }
        finally {
            if (ost != null) {
                ost.close();
            }
        }
    }


    class PrimitivePersistenceDelegate extends PersistenceDelegate {
        protected boolean mutatesTo(Object oldInstance, Object newInstance) {
            return oldInstance.equals(newInstance);
        }

        protected Expression instantiate(Object oldInstance, Encoder out) {
            return new Expression(oldInstance, oldInstance.getClass(),
                    "new", new Object[]{oldInstance.toString()});
        }
    }


    private void initDelegates(XMLEncoder e) {
        final PersistenceDelegate defDelegate = new PersistenceDelegate() {
            protected Expression instantiate(Object oldInstance, Encoder out) {
                return new Expression(oldInstance, oldInstance.getClass(), "new", new Object[]{oldInstance.toString()});
            }
        };
//        e.setPersistenceDelegate(URL.class, new PrimitivePersistenceDelegate());
        //e.setPersistenceDelegate(URL.class, defDelegate);
        e.setPersistenceDelegate(URL.class,
                new PersistenceDelegate() {
                    protected Expression instantiate(Object oldInstance, Encoder out) {
                        return new Expression(oldInstance, oldInstance.getClass(), "new", new Object[]{oldInstance.toString()});
                    }
                });

        e.setPersistenceDelegate(File.class, new DefaultPersistenceDelegate(
                new String[]{"absolutePath"}));
//        final DefaultPersistenceDelegate defaultPersistenceDelegate = new DefaultPersistenceDelegate(
//                new String[]{"fileUrl", "saveToDirectory"});
//        e.setPersistenceDelegate(DownloadFile.class, defaultPersistenceDelegate);
    }


    private OutputStream openOutputFile(String fileName) throws IOException {
        File dir = getDirectory();
        if (!dir.isDirectory()) {
            if (!dir.mkdirs()) {
                throw new LSException("couldn't create directory " + dir);
            }
        }
        File path = new File(dir, fileName);
        try {
            return new BufferedOutputStream(new FileOutputStream(path));
        }
        catch (IOException e) {
            throw new LSException("couldn't open output file \"" + fileName + "\"", e);
        }
    }

    private File getDirectory() {
        return (context != null) ? context.getLocalStorage().getDirectory() : new File("c:\\");
    }


    private InputStream openInputFile(String fileName) throws IOException {
        File path = new File(getDirectory(), fileName);
        try {
            return new BufferedInputStream(new FileInputStream(path));
        }
        catch (IOException e) {
            throw new LSException("couldn't open input file \"" + fileName + "\"", e);
        }
    }

    public Object load(String fileName) throws IOException {
        InputStream ist;
        try {
            ist = openInputFile(fileName);
        }
        catch (IOException e) {
            return null;
        }
        AbortExceptionListener el = new AbortExceptionListener();
        XMLDecoder d = null;
        try {
            d = new XMLDecoder(ist);
            d.setExceptionListener(el);
            Object bean = d.readObject();
            if (el.exception != null) {
                throw new LSException("load failed \"" + fileName + "\"", el.exception);
            }
            return bean;
        }
        finally {
            if (d != null) {
                d.close();
            }
        }
    }

    /* Papers over the fact that the String,Throwable IOException
     * constructor was only introduced in Java 6.
     */
    private static class LSException extends IOException {
        public LSException(String s, Throwable e) {
            super(s);
            initCause(e);
        }

        public LSException(String s) {
            super(s);
        }
    }

    public static void main(String[] args) throws IOException {
        final DownloadFile downloadFile = new DownloadFile(new URL("http://seznam.cz"), new File("c:\\"));
        new XMLUtils(null).save(downloadFile, "out.xml");
    }


}
