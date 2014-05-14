package cz.vity.freerapid.sandbox;

import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.ResourceManager;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Ladislav Vitasek
 */
public class EasyApp extends SingleFrameApplication {
    ResourceMap resource;

    @Override
    protected void initialize(String[] args) {
        ApplicationContext ctxt = getContext();
        //pristup na ResourceManager pres kontext
        ResourceManager mgr = ctxt.getResourceManager();
        //nacteni resources pro tridu EasyApp
        resource = mgr.getResourceMap(EasyApp.class);
    }

    @Override
    protected void startup() {
        //vytvoreni komponenty
        JLabel label = new JLabel();
        final JButton button = new JButton();
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Stiskl jsem tlacitko");
            }
        });
        button.setAction(new MojeAkce());

        button.setAction(new MojeAkce());

        // nacteni lokalizovaneho popisku
        String helloText = resource.getString("helloLabel");
        // podobne bychom mohli vyuzit i nasledujicich metod
        Color backgroundColor = resource.getColor("color");
        String title = resource.getString("title");
        label.setBackground(backgroundColor);
        label.setOpaque(true);
        getMainFrame().setTitle(title);
        label.setText(helloText);
        show(label);
    }
    //...

    private class MojeAkce extends AbstractAction {
        public MojeAkce() {
            super();
            this.putValue(NAME, "Moje akace");
            this.putValue(SMALL_ICON, null);
        }

        public void actionPerformed(ActionEvent e) {
            //udalost tlacitko
        }
    }


}
