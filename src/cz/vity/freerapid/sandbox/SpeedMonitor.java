package cz.vity.freerapid.sandbox;

import javax.swing.*;
import java.awt.*;
import static java.awt.Color.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;


/**
 * Tracks Speed
 */

public class SpeedMonitor extends JPanel {
    private int w, h;
    private BufferedImage bimg;
    private Graphics2D big;
    private final static Font font = new Font("Times New Roman", Font.PLAIN, 11);

    private int columnInc;
    private int pts[];
    private int ptNum = 0;
    private int ascent, descent;
    //private float freeMemory, totalMemory;
    private Rectangle graphOutlineRect = new Rectangle();
    private Rectangle2D mfRect = new Rectangle2D.Float();
    private Rectangle2D muRect = new Rectangle2D.Float();
    private Line2D graphLine = new Line2D.Float();
    private Color graphColor = new Color(46, 139, 87);
    private Color mfColor = new Color(0, 100, 0);
    private static float max = -9999999;
    private float maximum;
    private float currentSpeed;


    public SpeedMonitor() {
        setBackground(BLACK);
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
//                    if (thread == null) start();
//                    else stop();
            }
        });
        this.addComponentListener(new ComponentAdapter() {


            public void componentResized(ComponentEvent e) {
                Dimension d = getSize();
                if (d.width < 135 || d.height < 80)
                    return;

                if (d.width != w || d.height != h) {
                    w = d.width;
                    h = d.height;
//                    if (bimg != null)
//                        bimg.flush();
                    bimg = (BufferedImage) createImage(w, h);
                    big = bimg.createGraphics();
                    big.setFont(font);
                    FontMetrics fm = big.getFontMetrics(font);
                    ascent = fm.getAscent();
                    descent = fm.getDescent();
                }
            }
        });
    }

    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    public Dimension getPreferredSize() {
        return new Dimension(135, 80);
    }


    public void paint(Graphics g) {

        if (big == null) {
            return;
        }

        big.setBackground(getBackground());
        big.clearRect(0, 0, w, h);

        if (maximum > max) {
            max = maximum;
            pts = null;
        } else {
            maximum = max;
        }

        // .. Draw allocated and used strings ..
        big.setColor(GREEN);
        big.drawString(String.valueOf("asdasdasd") + "KB/s", 4.0f, (float) ascent + 0.5f);

//        currentSpeed = maximum - currentSpeed;

        big.drawString(String.valueOf(currentSpeed) + "KB/s average", 4, h - descent);

        // Calculate remaining size
        float ssH = ascent + descent;
        float remainingHeight = h - (ssH * 2) - 0.5f;
        float blockHeight = remainingHeight / 10;
        float blockWidth = 20.0f;
        //float remainingWidth = (float) (w - blockWidth - 10);

        // .. Memory Free ..
        big.setColor(mfColor);
        int MemUsage = (int) ((currentSpeed / maximum) * 10);
        int i = 0;
        for (; i < MemUsage; i++) {
            mfRect.setRect(5, ssH + i * blockHeight,
                    blockWidth, blockHeight - 1);
            big.fill(mfRect);
        }

        // .. Memory Used ..
        big.setColor(GREEN);
        for (; i < 10; i++) {
            muRect.setRect(5, ssH + i * blockHeight,
                    blockWidth, blockHeight - 1);
            big.fill(muRect);
        }

        // .. Draw History Graph ..
        big.setColor(graphColor);
        int graphX = 30;
        int graphY = (int) ssH;
        int graphW = w - graphX - 5;
        int graphH = (int) remainingHeight;
        graphOutlineRect.setRect(graphX, graphY, graphW, graphH);
        big.draw(graphOutlineRect);

        int graphRow = graphH / 10;

        // .. Draw row ..
        for (int j = graphY; j <= graphH + graphY; j += graphRow) {
            graphLine.setLine(graphX, j, graphX + graphW, j);
            big.draw(graphLine);
        }

        // .. Draw animated column movement ..
        int graphColumn = graphW / 15;

        if (columnInc == 0) {
            columnInc = graphColumn;
        }

        for (int j = graphX + columnInc; j < graphW + graphX; j += graphColumn) {
            graphLine.setLine(j, graphY, j, graphY + graphH);
            big.draw(graphLine);
        }

        --columnInc;

        if (pts == null) {
            pts = new int[graphW];
            ptNum = 0;
        } else if (pts.length != graphW) {
            int tmp[];
            if (ptNum < graphW) {
                tmp = new int[ptNum];
                System.arraycopy(pts, 0, tmp, 0, tmp.length);
            } else {
                tmp = new int[graphW];
                System.arraycopy(pts, pts.length - tmp.length, tmp, 0, tmp.length);
                ptNum = tmp.length - 2;
            }
            pts = new int[graphW];
            System.arraycopy(tmp, 0, pts, 0, tmp.length);
        } else {
            big.setColor(YELLOW);
            pts[ptNum] = (int) (graphY + graphH * currentSpeed);
            for (int j = graphX + graphW - ptNum, k = 0; k < ptNum; k++, j++) {
                if (k != 0) {
                    if (pts[k] != pts[k - 1]) {
                        big.drawLine(j - 1, pts[k - 1], j, pts[k]);
                    } else {
                        big.fillRect(j, pts[k], 1, 1);
                    }
                }
            }
            if (ptNum + 2 == pts.length) {
                // throw out oldest point
                System.arraycopy(pts, 1, pts, 0, ptNum - 1);
                --ptNum;
            } else {
                ptNum++;
            }
        }
        g.drawImage(bimg, 0, 0, this);
    }


    public void setCurrentSpeed(float currentSpeed) {
        this.currentSpeed = currentSpeed;
        repaint();
    }

    public void setMaximum(float maximum) {
        this.maximum = maximum;
    }

    public void start() {

    }

    public void stop() {

    }


    public static void main(String s[]) {
        final cz.vity.freerapid.sandbox.SpeedMonitor demo = new cz.vity.freerapid.sandbox.SpeedMonitor();
        WindowListener l = new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }

            public void windowDeiconified(WindowEvent e) {
                //demo.surf.start();
            }

            public void windowIconified(WindowEvent e) {
                //demo.surf.stop();
            }
        };
        JFrame f = new JFrame("Java2D Demo - MemoryMonitor");
        f.addWindowListener(l);
        f.getContentPane().add("Center", demo);
        f.pack();
        f.setSize(new Dimension(200, 200));
        f.setVisible(true);
    }
}