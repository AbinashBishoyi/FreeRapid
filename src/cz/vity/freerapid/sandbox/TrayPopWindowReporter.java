package cz.vity.freerapid.sandbox;

import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.callback.UIThreadTimelineCallbackAdapter;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

/**
 * @author Vity
 */
public class TrayPopWindowReporter {

    private static final float MAX_OPACITY = 0.9f;
    private Timer waitTimer;
    private enum PopWindowState {
        OPENING, SHOWING, CLOSING, NONE
    }
    private PopWindowState state = PopWindowState.NONE;


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel(
                            UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (UnsupportedLookAndFeelException e) {
                    e.printStackTrace();
                }
                new TrayPopWindowReporter().start();
            }
        });
    }

    private void start() {
        final TrayPopWindow window = new TrayPopWindow();
        window.setTextLabel("3 new links were added");
        window.setAlwaysOnTop(true);
        window.toFront();
        fadeIn(window, 1500);
    }

    public void fadeIn(final TrayPopWindow window, final int fadeInDuration) {
        state = PopWindowState.OPENING;
        window.setOpacity(0);
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final Rectangle bounds = ge.getMaximumWindowBounds();
        final int startY = (int) (bounds.getY() + bounds.getHeight());
        window.setLocation((int) (bounds.getX() + bounds.getWidth() - window.getSize().width) - 2, startY);
        window.setVisible(true);
        final ActionListener listener = new ActionListener() {


            @Override
            public void actionPerformed(ActionEvent e) {
                if (window.getMousePosition(true) != null) {
                    waitTimer.restart();
                    return;
                }
                if (state == PopWindowState.SHOWING) {
                    fadeOut(window, fadeInDuration * 2 / 3);
                }
            }
        };

        window.getBtnClose().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (state == PopWindowState.SHOWING) {
                    fadeOut(window, fadeInDuration / 8);
                }
            }
        });

        waitTimer = new Timer(2500, listener);
        waitTimer.setRepeats(false);

        Timeline dispose = new Timeline(window);

        window.addMouseMotionListener(new MouseInputAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
            }
        });

        dispose.addPropertyToInterpolate("opacity", 0.0f, MAX_OPACITY);
        dispose.addPropertyToInterpolate("y", startY, startY - window.getHeight() - 20);
        dispose.addCallback(new UIThreadTimelineCallbackAdapter() {
            @Override
            public void onTimelineStateChanged(Timeline.TimelineState oldState,
                                               Timeline.TimelineState newState, float durationFraction,
                                               float timelinePosition) {
                if (newState == Timeline.TimelineState.DONE) {
                    state = PopWindowState.SHOWING;
                    waitTimer.start();
                }
            }
        });
        dispose.setDuration(fadeInDuration);
        dispose.play();
    }

    private void fadeOut(final Window window, final int fadeOutDuration) {
        this.state = PopWindowState.CLOSING;
        final Timeline dispose = new Timeline(window);
        dispose.addPropertyToInterpolate("opacity", MAX_OPACITY, 0.0f);
       // dispose.addPropertyToInterpolate("y", window.getY(), window.getY() + window.getHeight() + 20);
        dispose.addCallback(new UIThreadTimelineCallbackAdapter() {
            @Override
            public void onTimelineStateChanged(Timeline.TimelineState oldState,
                                               Timeline.TimelineState newState, float durationFraction,
                                               float timelinePosition) {
                if (newState == Timeline.TimelineState.DONE) {
                    window.setVisible(false);
                    window.dispose();
                    state = PopWindowState.NONE;
                }
            }
        });
        dispose.setDuration(fadeOutDuration);
        dispose.play();
    }

}
