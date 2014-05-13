package cz.vity.freerapid.swing.components;

import cz.vity.freerapid.swing.ComponentFactory;
import cz.vity.freerapid.swing.Swinger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Vity
 */
public class MemoryIndicator extends JPanel {
    private final JProgressBar progressBar;

    public MemoryIndicator() {
        this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        this.add(progressBar = new JProgressBar());
        final JButton button = ComponentFactory.getToolbarButton();
        button.setName("btnGC");
        Swinger.getResourceMap().injectComponent(button);
        button.setIconTextGap(0);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    public void run() {
                        //SystemUtil.runGc();
                    }
                }).start();
            }
        });
        this.add(button);
        progressBar.setIndeterminate(false);
        progressBar.setMinimum(0);
        progressBar.setStringPainted(true);

        updateInfo();
        final Timer timer = new Timer(2500, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateInfo();
            }
        });
        timer.start();

        this.addPropertyChangeListener("visible", new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                if (Boolean.TRUE.equals(evt.getNewValue())) {
                    timer.start();
                } else
                    timer.stop();
            }
        });

    }

    private void updateInfo() {
        Runtime s_runtime = Runtime.getRuntime();
        int max = (int) ((s_runtime.maxMemory() / 1024) / 1024);
        //int totalMemory = (int) ((s_runtime.totalMemory() / 1024) / 1024);
        long used_memory = s_runtime.totalMemory() - s_runtime.freeMemory();
        used_memory = (used_memory / 1024) / 1024;
        final String s = Swinger.getResourceMap().getString("memoryIndicator", used_memory, max);
        progressBar.setMaximum(max);
        progressBar.setValue((int) used_memory);
        progressBar.setString(s);
    }

}
