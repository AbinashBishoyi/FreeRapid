package cz.vity.freerapid.sandbox;

import java.beans.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Vity
 */
public class TestEncoder {
    public static void main(String[] args) throws IOException {
        XMLEncoder e = new XMLEncoder(System.out);
        e.setPersistenceDelegate(URL.class, new PrimitivePersistenceDelegate());
        e = new XMLEncoder(System.out);//intention
        //  e.setPersistenceDelegate(URL.class, new PrimitivePersistenceDelegate());
        final TestBean bean = new TestBean();
        try {
            bean.setUrl(new URL("http://test"));
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        }
        final MyExceptionListener exListener = new MyExceptionListener();
        e.setExceptionListener(exListener);
        e.writeObject(bean);
        if (exListener.ex != null)
            throw new IOException("XMLEnc failed", exListener.ex);
        e.close();
    }


    static class PrimitivePersistenceDelegate extends PersistenceDelegate {

        protected Expression instantiate(Object oldInstance, Encoder out) {
            return new Expression(oldInstance, oldInstance.getClass(),
                    "new", new Object[]{oldInstance.toString()});
        }
    }

    private static class MyExceptionListener implements ExceptionListener {
        Exception ex;

        public void exceptionThrown(Exception e) {
            if (this.ex == null)
                this.ex = e;
        }
    }

    public static class TestBean {
        private URL url;

        public TestBean() {

        }

        public URL getUrl() {
            return url;
        }

        public void setUrl(URL url) {
            this.url = url;
        }
    }


}
