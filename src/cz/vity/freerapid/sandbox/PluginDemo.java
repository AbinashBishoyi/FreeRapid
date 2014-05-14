package cz.vity.freerapid.sandbox;

import org.java.plugin.ObjectFactory;
import org.java.plugin.PluginManager;
import org.java.plugin.registry.PluginDescriptor;
import org.java.plugin.standard.StandardPluginLocation;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;

/**
 * @author Ladislav Vitasek
 */
public class PluginDemo {
    void usePlugin() throws Exception {
        //vytvoreni nove instance plugin manageru
        PluginManager pluginManager = ObjectFactory.newInstance().createManager();
        final File file = new File("Cesta_k_souboru_s_pluginem");//cesta k souboru s pluginem

        PluginManager.PluginLocation location = StandardPluginLocation.create(file);
        //deklarace pole umisteni souboru pluginu
        PluginManager.PluginLocation[] locations = new PluginManager.PluginLocation[1];
        locations[0] = location;//je k dispozici pouze 1 plugin
        pluginManager.publishPlugins(locations); //registrace pluginu v manageru

        //iterovani skrze plugin manifesty registrovanych pluginu
        final Iterator<PluginDescriptor> it = pluginManager.getRegistry().getPluginDescriptors().iterator();
        String pluginId = it.next().getId(); //ziskani ID pluginu
        //vytvoreni instance DemoPluginu
        final DemoPluginTool pluginTool = (DemoPluginTool) pluginManager.getPlugin(pluginId);
        //volani funkcnosti pluginu
        final String[] favoriteURLLinks = pluginTool.getFavoriteURLLinks();
        //vypis do konzole
        System.out.println(Arrays.toString(favoriteURLLinks));

        pluginManager.shutdown();
    }
}
