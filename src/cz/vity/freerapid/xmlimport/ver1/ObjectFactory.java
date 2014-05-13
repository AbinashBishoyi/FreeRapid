package cz.vity.freerapid.xmlimport.ver1;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the cz.vity.freerapid.xmlimport.ver1 package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Services_QNAME = new QName("", "services");
    private final static QName _Url_QNAME = new QName("", "url");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: cz.vity.freerapid.xmlimport.ver1
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Plugin }
     */
    public Plugin createPlugin() {
        return new Plugin();
    }

    /**
     * Create an instance of {@link Plugins }
     */
    public Plugins createPlugins() {
        return new Plugins();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "services")
    public JAXBElement<String> createServices(String value) {
        return new JAXBElement<String>(_Services_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "url")
    public JAXBElement<String> createUrl(String value) {
        return new JAXBElement<String>(_Url_QNAME, String.class, null, value);
    }

}
