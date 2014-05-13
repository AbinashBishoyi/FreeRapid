package cz.vity.freerapid.swing;

import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;
import java.awt.*;

/**
 * Lepsi theme pro Kunstoff, zadne tucne fonty atp.
 *
 * @author Vity
 */
public final class KunstoffMetalTheme extends DefaultMetalTheme {
    private final ColorUIResource color = new ColorUIResource(0, 0, 0);
    private final FontUIResource font = new FontUIResource("Dialog", Font.PLAIN, 11);

    public KunstoffMetalTheme() {
        super();
    }

    public final ColorUIResource getControlTextColor() {
        return color;
    }

    public final ColorUIResource getMenuTextColor() {
        return color;
    }

    public final ColorUIResource getSystemTextColor() {
        return color;
    }

    public final ColorUIResource getUserTextColor() {
        return color;
    }

    public final FontUIResource getControlTextFont() {
        return font;
    }

    public final FontUIResource getMenuTextFont() {
        return font;
    }

    public final FontUIResource getSystemTextFont() {
        return font;
    }

    public final FontUIResource getUserTextFont() {
        return font;
    }

    public final FontUIResource getWindowTitleFont() {
        return font;
    }

}