/*
 * Copyright (C) 2006 Sun Microsystems, Inc. All rights reserved. Use is
 * subject to license terms.
 */
package org.jdesktop.application;

import static org.jdesktop.application.Application.KEY_APPLICATION_ICON;
import static org.jdesktop.application.Application.KEY_APPLICATION_TITLE;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Logger;

public class FrameView extends View {
    public static final String MAIN_FRAME_NAME = "mainFrame";

    private static final Logger logger = Logger.getLogger(FrameView.class.getName());
    private JFrame frame = null;

    public FrameView(Application application) {
        super(application);
    }

    /**
     * Return the JFrame used to show this View
     *
     * <p>
     * This method may be called at any time; the JFrame is created lazily
     * and cached.  For example:
     * <pre>
     *  &#064;Override protected void startup() {
     *     getFrame().setJMenuBar(createMenuBar());
     *     show(createMainPanel());
     * }
     * </pre>
     *
     * @return this application's  main frame
     */
    public JFrame getFrame() {
        if (frame == null) {
            ResourceMap resourceMap = getContext().getResourceMap();
            String title = resourceMap.getString(KEY_APPLICATION_TITLE);

            frame = new JFrame(title);
            frame.setName(MAIN_FRAME_NAME);
            if (resourceMap.containsKey(KEY_APPLICATION_ICON)) {
                Image icon = resourceMap.getImageIcon(KEY_APPLICATION_ICON).getImage();
                frame.setIconImage(icon);
            }
        }
        return frame;
    }

    /**
     * Sets the JFrame use to show this View
     * <p>
     * This method should be called from the startup method by a
     * subclass that wants to construct and initialize the main frame
     * itself.  Most applications can rely on the fact that {code
     * getFrame} lazily constructs the main frame and initializes
     * the {@code frame} property.
     * <p>
     * If the main frame property was already initialized, either 
     * implicitly through a call to {@code getFrame} or by
     * explicitly calling this method, an IllegalStateException is 
     * thrown.  If {@code frame} is null, an IllegalArgumentException
     * is thrown.
     * <p>
     * This property is bound.
     *
     *
     *
     * @param frame the new value of the frame property
     * @see #getFrame
     */
    public void setFrame(JFrame frame) {
        if (frame == null) {
            throw new IllegalArgumentException("null JFrame");
        }
        if (this.frame != null) {
            throw new IllegalStateException("frame already set");
        }
        this.frame = frame;
        firePropertyChange("frame", null, this.frame);
    }

    @Override
    public JRootPane getRootPane() {
        return getFrame().getRootPane();
    }
}
