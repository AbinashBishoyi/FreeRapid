package cz.vity.freerapid.xmlimport;

import cz.vity.freerapid.xmlimport.ver1.Plugins;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.helpers.DefaultValidationEventHandler;
import javax.xml.validation.SchemaFactory;
import java.io.StringReader;

/**
 * @author Ladislav Vitasek
 */
public class XMLBind {

    private static final String RESOURCES_SCHEMA_XSD = "resources/schema.xsd";

    /**
     * Nacte data z XML pomoci technologie JAXB a vlozi je
     *
     * @param xmlData xml struktura dat
     * @return instance objektu Plugins, ktery obsahuje seznam dostupnych pluginu na server
     * @throws JAXBException
     * @throws SAXException
     */
    public Plugins loadPluginList(String xmlData) throws JAXBException, SAXException {
        final JAXBContext ctx = getContext();
        final Unmarshaller unmarshaller = ctx.createUnmarshaller();
        unmarshaller.setEventHandler(new DefaultValidationEventHandler());
        SchemaFactory sf = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
        unmarshaller.setSchema(sf.newSchema(this.getClass().getResource(RESOURCES_SCHEMA_XSD)));
        //unmarshalling XML dat
        return (Plugins) unmarshaller.unmarshal(new StringReader(xmlData));
    }

    private JAXBContext getContext() throws JAXBException {
        return JAXBContext.newInstance(Plugins.class.getPackage().getName());
    }

}
