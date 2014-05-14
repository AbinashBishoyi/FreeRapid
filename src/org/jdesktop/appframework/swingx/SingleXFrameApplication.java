/*
 * Created on 26.02.2007
 *
 */
package org.jdesktop.appframework.swingx;

import org.jdesktop.appframework.swingx.XProperties.XTableProperty;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.SessionStorage;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXTable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class SingleXFrameApplication extends SingleFrameApplication {
    private static final Logger logger = Logger.getLogger(SingleXFrameApplication.class.getName());
    private boolean started = false;

    /**
     * Overridden to force a JXFrame as main frame and inject SwingX specific session properties.
     */
    @Override
    protected void initialize(String[] args) {
        injectSessionProperties();
        setMainFrame(createXMainFrame());
    }

    public void prepareDialog(JDialog c, boolean visible) {
        if (c == null) {
            throw new IllegalArgumentException("null JDialog");
        }
        if (!hadBeenPrepared(c)) {
            prepareWindow(c);
        }
        if (visible) {
            c.setVisible(true);
        }
    }

    /**
     * Checks and returns whether the given RootPaneContainer already has been prepared. As a side-effect, the container
     * is marked as prepared (wrong place?)
     *
     * @param c
     * @return
     */
    private boolean hadBeenPrepared(RootPaneContainer c) {
        JComponent rootPane = c.getRootPane();
        // These initializations are only done once
        Object k = "SingleFrameApplication.initRootPaneContainer";
        boolean prepared = Boolean.TRUE.equals(rootPane.getClientProperty(k));
        if (!prepared) {
            rootPane.putClientProperty(k, Boolean.TRUE);
        }
        return prepared;
    }


    @Override
    protected void startup() {
        this.started = true;
    }


    /**
     * Prepares the given window. Injects properties from app context. Restores session state if appropriate. Registers
     * listeners to try and track session state.
     *
     * @param root
     */

    protected void prepareWindow(Window root) {
        configureWindow(root);
        // If the window's size doesn't appear to have been set, do it
        if ((root.getWidth() < 150) || (root.getHeight() < 20)) {
            root.pack();
            if (!root.isLocationByPlatform()) {
                Component owner = (root != getMainFrame()) ? getMainFrame()
                        : null;
                root.setLocationRelativeTo(owner); // center the window
            }
        }
        // Restore session state
        String filename = sessionFilename(root);
        if (filename != null) {
            try {
                ApplicationContext ac = getContext();
                ac.getSessionStorage().restore(root, filename);
            } catch (Exception e) {
                logger.log(Level.WARNING, "couldn't restore sesssion", e);
            }
        }
        {//Vity's hack
            //   final Point location = root.getLocation();
            //System.out.println("location = " + location);
            final Dimension size = root.getPreferredSize();
            final boolean invalidWidth = root.getWidth() < size.width;
            final boolean invalidHeight = root.getHeight() < size.height;
            if (invalidWidth || (invalidHeight)) {
                if (invalidWidth && invalidHeight) {
                    root.pack();
                } else {
                    root.setSize(invalidWidth ? size.width : root.getWidth(), invalidHeight ? size.height : root.getHeight());
                }
                if (!root.isLocationByPlatform()) {
                    Component owner = (root != getMainFrame()) ? getMainFrame()
                            : null;
                    root.setLocationRelativeTo(owner); // center the window
                }
            }
        }
        root.addWindowListener(getDialogListener());
    }

    private WindowListener getDialogListener() {
        return new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveSession(e.getWindow());
            }

            @Override
            public void windowClosed(WindowEvent e) {
                saveSession(e.getWindow());
            }

        };
    }


    /**
     * Save session state for the component hierarchy rooted by the mainFrame. SingleFrameApplication subclasses that
     * override shutdown need to remember call {@code super.shutdown()}.
     */
    @Override
    protected void shutdown() {
        if (!started)
            return;
        List<Window> windows = new ArrayList<Window>();
        final JFrame mainFrame = getMainFrame();
        if (mainFrame instanceof JXFrame) {
            windows.add(mainFrame);
            for (int i = 0; i < getMainFrame().getOwnedWindows().length; i++) {
                windows.add(getMainFrame().getOwnedWindows()[i]);
            }
        }
        for (Window window : windows) {
            if (window.isShowing() || window.isValid())
                saveSession(window);
            else if (window instanceof JFrame) {
                JFrame f = (JFrame) window;
                final Object init = f.getRootPane().getClientProperty("initialized");
                if (Boolean.TRUE.equals(init))
                    saveSession(window);
            }
        }
    }

    private String sessionFilename(Window window) {
        if (window == null) {
            return null;
        } else {
            String name = window.getName();
            return (name == null) ? null : name + ".session.xml";
        }
    }

    private void saveSession(Window window) {
        String filename = sessionFilename(window);
        if (filename != null) {
            ApplicationContext appContext = getContext();
            try {
                appContext.getSessionStorage().save(window, filename);
            }
            catch (IOException e) {
                logger.log(Level.WARNING, "couldn't save session", e);
            } catch (SecurityException e) {
                logger.log(Level.WARNING, "couldn't save session", e);
            }
        }
    }

    @Override
    public void show(JFrame c) {
        super.show(c);
        final Point onScreen = c.getLocationOnScreen();
        final int x = Math.max(0, onScreen.x);
        final int y = Math.max(0, onScreen.y);
//        final boolean isMaximized = c.getExtendedState() == JFrame.MAXIMIZED_BOTH;
        if ((c.getWidth() < 150) || (c.getHeight() < 20)) {
            c.pack();
        }
        if (onScreen.x != x || onScreen.y != y) {
            if (!c.isLocationByPlatform()) {
                Component owner = (c != getMainFrame()) ? getMainFrame()
                        : null;
                c.setLocationRelativeTo(owner); // center the window
            }
        }
        c.getRootPane().putClientProperty("initialized", Boolean.TRUE);
    }

    /**
     * Deletes the session state by deleting the file. Useful during development when restoring to old state is not
     * always the desired behaviour. Pending: this is incomplete, deletes the mainframe state only.
     */
    protected void deleteSessionState() {
        ApplicationContext context = getContext();
        try {
            context.getLocalStorage().deleteFile("mainFrame.session.xml");
        } catch (IOException e) {
            logger.log(Level.WARNING, "couldn't delete sesssion", e);
        } catch (SecurityException e) {
            logger.log(Level.WARNING, "couldn't delete sesssion", e);
        }
    }

    protected JXFrame createXMainFrame() {
        JXFrame xFrame = new JXFrame();
        ApplicationContext appContext = getContext();
        String title = appContext.getResourceMap().getString("Application.title");
        xFrame.setStartPosition(JXFrame.StartPosition.Manual);
        xFrame.setTitle(title);
        xFrame.setName("mainFrame");
        return xFrame;
    }

    /**
     * Registers SwingX specific Properties for session storage. <p>
     */
    protected void injectSessionProperties() {
        SessionStorage storage = getContext().getSessionStorage();
        storage.putProperty(JXTable.class, new XTableProperty());
        new XProperties().registerPersistenceDelegates(getContext());
    }


}