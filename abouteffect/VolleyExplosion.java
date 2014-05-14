package cz.vity.freerapid.gui.dialogs.abouteffect;

import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.TimelineScenario;
import org.pushingpixels.trident.ease.Spline;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author KirilG
 */
public class VolleyExplosion {
    private int x;

    private int y;

    private Color color;

    private final Set<SingleExplosion> circles;

    public VolleyExplosion(int x, int y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.circles = new HashSet<SingleExplosion>();
    }

    public TimelineScenario getExplosionScenario() {
        TimelineScenario scenario = new TimelineScenario.Parallel();

        int duration = 1000 + (int) (1000 * Math.random());
        for (int i = 0; i < 18; i++) {
            float dist = (float) (50 + 10 * Math.random());
            float radius = (float) (2 + 2 * Math.random());
            for (float delta = 0.6f; delta <= 1.0f; delta += 0.2f) {
                float circleRadius = radius * delta;

                double degrees = 20.0 * (i + Math.random());
                float radians = (float) (2.0 * Math.PI * degrees / 360.0);

                float initDist = delta * dist / 10.0f;
                float finalDist = delta * dist;
                float initX = (float) (this.x + initDist
                        * Math.cos(radians));
                float initY = (float) (this.y + initDist
                        * Math.sin(radians));
                float finalX = (float) (this.x + finalDist
                        * Math.cos(radians));
                float finalY = (float) (this.y + finalDist
                        * Math.sin(radians));

                SingleExplosion circle = new SingleExplosion(this.color,
                        initX, initY, circleRadius);
                Timeline timeline = new Timeline(circle);
                timeline.addPropertyToInterpolate("x", initX, finalX);
                timeline.addPropertyToInterpolate("y", initY, finalY);
                timeline.addPropertyToInterpolate("opacity", 1.0f, 0.0f);
                timeline.setDuration(duration - 200
                        + (int) (400 * Math.random()));
                timeline.setEase(new Spline(0.4f));

                synchronized (this.circles) {
                    circles.add(circle);
                }
                scenario.addScenarioActor(timeline);
            }
        }

        return scenario;
    }

    public void paint(Graphics g) {
        synchronized (this.circles) {
            for (SingleExplosion circle : this.circles) {
                circle.paint(g);
            }
        }
    }
}