package cz.vity.freerapid.sandbox;

import cz.vity.freerapid.utilities.DescriptorUtils;
import cz.vity.freerapid.utilities.LogUtils;
import cz.vity.freerapid.xmlimport.XMLBind;
import cz.vity.freerapid.xmlimport.ver1.Plugin;
import cz.vity.freerapid.xmlimport.ver1.Plugins;
import org.java.plugin.JpfException;
import org.java.plugin.ObjectFactory;
import org.java.plugin.PluginManager;
import org.java.plugin.registry.PluginDescriptor;
import org.java.plugin.standard.StandardPluginLocation;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Ladislav Vitasek
 */
public class UpdateData2XML {
    private final static Logger logger = Logger.getLogger(UpdateData2XML.class.getName());
    private static final String RESOURCES_SCHEMA_XSD = "resources/schema.xsd";


    public static void main(String[] args) {
        final File f = new File("c:\\develope\\freerapid-plugintools\\dist");
        final File output = new File("d:\\www\\data.xml");
        new UpdateData2XML().start(f, output, "http://wordrider.net/freerapid/plugs");
    }

    private void start(File directory, File output, String url) {
        final ObjectFactory objectFactory = ObjectFactory.newInstance();
        final PluginManager pluginManager = objectFactory.createManager();

        File[] plugins = directory.listFiles(new FilenameFilter() {

            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".frp");
            }

        });
        final int length = plugins.length;
        final PluginManager.PluginLocation[] loc = new PluginManager.PluginLocation[length];

        for (int i = 0; i < length; i++) {

            try {
                final String path = plugins[i].toURI().toURL().toExternalForm();
                logger.info("Plugins path:" + path);
                final URL context = new URL("jar:" + path + "!/");
                final URL manifest = new URL("jar:" + path + "!/plugin.xml");

                //loc[i] = StandardPluginLocation.create(plugins[i]);
                loc[i] = new StandardPluginLocation(context, manifest);
            } catch (MalformedURLException e) {
                LogUtils.processException(logger, e);
            }

            //loc[i] = StandardPluginLocation.create(plugins[i]);
            //logger.info("Plugin location: " + loc);
        }

        try {
            pluginManager.publishPlugins(loc);
        } catch (JpfException e) {
            LogUtils.processException(logger, e);
        }
        final Collection<PluginDescriptor> descriptorList = pluginManager.getRegistry().getPluginDescriptors();
        final List<Plugin> pluginList = new ArrayList<Plugin>(descriptorList.size());
        for (PluginDescriptor desc : descriptorList) {
            final String path = new File(desc.getLocation().getPath()).getPath();

            int indexStart = path.indexOf("file:\\");
            indexStart = indexStart + "file:\\".length();
            int indexEnd = path.lastIndexOf('!');

            final Plugin p = new Plugin();
            final File file = new File(path.substring(indexStart, indexEnd));
            final String fileName = file.getName();
            p.setFilename(fileName);
            p.setFilesize(file.length());
            p.setId(desc.getId());
            p.setVendor(desc.getVendor());
            p.setVersion(desc.getVersion().toString());
            final boolean b = DescriptorUtils.getAttribute("premium", false, desc);
            p.setPremium(b ? "yes" : "no");
            p.setServices(DescriptorUtils.getAttribute("services", desc.getId(), desc));
            p.setMinVer(DescriptorUtils.getAttribute("minver", desc.getId(), desc));
            p.setMaxVer(DescriptorUtils.getAttribute("maxver", desc.getId(), desc));
            p.setUrl(url + "/" + fileName);
            pluginList.add(p);
        }
        saveToFile(pluginList, output);
    }

    private void saveToFile(List<Plugin> list, File f) {
        try {
            final JAXBContext ctx = getContext();
            SchemaFactory sf = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
            final Marshaller marshaller = ctx.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setSchema(sf.newSchema(XMLBind.class.getResource(RESOURCES_SCHEMA_XSD)));

            final Plugins plugins = new Plugins();
            plugins.getPlugin().addAll(list);
            marshaller.marshal(plugins, f);
        } catch (JAXBException e) {
            LogUtils.processException(logger, e);
        } catch (SAXException e) {
            LogUtils.processException(logger, e);
        }

    }

    private JAXBContext getContext() throws JAXBException {
        return JAXBContext.newInstance(Plugins.class.getPackage().getName());
    }

}
