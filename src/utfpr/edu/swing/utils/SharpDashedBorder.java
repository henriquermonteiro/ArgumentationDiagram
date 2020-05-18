package utfpr.edu.swing.utils;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import javax.swing.border.AbstractBorder;

/**
 * A custom dashed line border with sharp lines.
 * 
 * @author Henrique M R Jasinski
 */
public class SharpDashedBorder extends AbstractBorder {

    private final Color borderColour;
    private final int gap;
    private final float tickness;
    private final int cap;
    private final int join;
    private final float miterlimit;
    private final float[] dash;
    private final float dash_phase;

    /**
     * Constructor receives the BasicStroke parameters, the inset of the border and the border color.
     * @param borderColour border color
     * @param inset inset of border
     * @param tickness Stroke parameter
     * @param cap Stroke parameter
     * @param join Stroke parameter
     * @param miterlimit Stroke parameter
     * @param dash Stroke parameter
     * @param dash_phase Stroke parameter
     */
    public SharpDashedBorder(Color borderColour, int inset, float tickness, int cap, int join, float miterlimit, float[] dash, float dash_phase) {
        this.borderColour = borderColour;
        this.gap = inset + (int)tickness;
        this.tickness = tickness;
        this.cap = cap;
        this.join = join;
        this.miterlimit = miterlimit;
        this.dash = dash;
        this.dash_phase = dash_phase;
    }

    /**
     * Paints the border using a BasicStroke.
     * BasicStroke defined as BasicStroke(tickness, cap, join, miterlimit, dash, dash_phase)
     * @param c target component 
     * @param g graphics instance
     * @param x position x
     * @param y position y
     * @param width border width
     * @param height border height
     */
    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        super.paintBorder(c, g, x, y, width, height);
        Graphics2D g2d = null;
        if (g instanceof Graphics2D) {
            g2d = (Graphics2D) g.create();
            g2d.setColor(borderColour);
            g2d.setStroke(new BasicStroke(tickness, cap, join, miterlimit, dash, dash_phase));
            //Left Border
            g2d.drawLine(x + ((int)(tickness/2)), height, x + ((int)(tickness/2)), y);
            // Top Border
            g2d.drawLine(x, y + ((int)(tickness/2)), width, y + ((int)(tickness/2)));
            // Right Border
            g2d.drawLine(width - (Math.round(tickness/2)), y, width - (Math.round(tickness/2)), height);
            // Bottom Border
            g2d.drawLine(width, height - (Math.round(tickness/2)), x, height - (Math.round(tickness/2)));
        }
    }

    /**
     * Return the insets of the border.
     * Insets are defined by the inset parameter from constructor.
     * @param c the component which the border belongs
     * @return the resulting insets
     */
    @Override
    public Insets getBorderInsets(Component c) {
        return (getBorderInsets(c, new Insets(gap, gap, gap, gap)));
    }

    /**
     * Return the insets of the border.
     * Insets are defined by the inset parameter from constructor.
     * @param c the component which the border belongs
     * @param insets the insets from the component
     * @return the updated insets
     */
    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.left = insets.top = insets.right = insets.bottom = gap;
        return insets;
    }

    /**
     * Border is always opaque.
     * @return true
     */
    @Override
    public boolean isBorderOpaque() {
        return true;
    }
}
