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
public class TrayTest {

    private static final float MAX_OPACITY = 0.9f;

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
                new TrayTest().start();
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

    public static void fadeIn(final Window window, final int fadeOutDuration) {
        window.setOpacity(0);
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final Rectangle bounds = ge.getMaximumWindowBounds();
        final int startY = (int) (bounds.getY() + bounds.getHeight());
        window.setLocation((int) (bounds.getX() + bounds.getWidth() - window.getSize().width) - 2, startY);
        window.setVisible(true);
        final Timer waitTimer = new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fadeOut(window, fadeOutDuration);
            }
        });
        waitTimer.setRepeats(false);

        Timeline dispose = new Timeline(window);
        final MouseInputAdapter delayMouseAdapter = new MouseInputAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                //some optimization needed
                if (waitTimer.isRunning()) {
                    waitTimer.restart();
                }
            }
        };
        window.addMouseMotionListener(delayMouseAdapter);

        dispose.addPropertyToInterpolate("opacity", 0.0f, MAX_OPACITY);
        dispose.addPropertyToInterpolate("y", startY, startY - window.getHeight() - 20);
        dispose.addCallback(new UIThreadTimelineCallbackAdapter() {
            @Override
            public void onTimelineStateChanged(Timeline.TimelineState oldState,
                                               Timeline.TimelineState newState, float durationFraction,
                                               float timelinePosition) {
                if (newState == Timeline.TimelineState.DONE) {
                    waitTimer.start();
                }
            }
        });
        dispose.setDuration(fadeOutDuration);
        dispose.play();
    }

    private static void fadeOut(final Window window, final int fadeOutDuration) {

        final Timeline dispose = new Timeline(window);

        dispose.addPropertyToInterpolate("opacity",MAX_OPACITY, 0.0f);
        dispose.addPropertyToInterpolate("y", window.getY(), window.getY() + window.getHeight() + 20);
        dispose.addCallback(new UIThreadTimelineCallbackAdapter() {
            @Override
            public void onTimelineStateChanged(Timeline.TimelineState oldState,
                                               Timeline.TimelineState newState, float durationFraction,
                                               float timelinePosition) {
                if (newState == Timeline.TimelineState.DONE) {
                    window.setVisible(false);
                    window.dispose();
                }
            }
        });
        dispose.setDuration(fadeOutDuration);
        dispose.play();
    }

}
