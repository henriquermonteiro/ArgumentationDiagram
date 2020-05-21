package utfpr.edu.argumentation.diagram;

import utfpr.edu.swing.utils.ColorUtil;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Objects;
import javax.swing.JPanel;

/**
 * Represents an attack as a swing.JComponent.
 * @author Henrique M R Jasinski
 */
public class Attack extends JPanel {
    
    private ArgumentionFramework myFramework;

    private Argument a1;
    private Argument a2;
    private Argument leftMost;
    private Argument rightMost;
    private boolean bidirectional = false;
    private Attack thisRef;

    private BufferedImage img;
    private boolean lockRedraw = false;
    private boolean transparent = false;
    private boolean dirty = true;
    private char type = ' ';

    /**
     * Constructor of an Attack.
     * Is interpreted as arg1 attacks arg2.
     * If myFramework is not set, the default values are used.
     * @param arg1 first argument
     * @param arg2 second argument
     * @param myFramework ArgumentationFramework reference
     */
    public Attack(Argument arg1, Argument arg2, ArgumentionFramework myFramework) {
        super();
        this.myFramework = myFramework;
        thisRef = this;
        
        if (arg1 == null || arg2 == null) {
            throw new NullPointerException("Argument a1 and a2 can not be null");
        }

        if (arg1.equals(arg2)) {
            throw new IllegalArgumentException("Arguments a1 and a2 must be different");
        }

        this.a1 = arg1;
        this.a2 = arg2;

        if ((arg1.getXToRoot() + arg1.getX()) < (arg2.getXToRoot() + arg2.getX())) {
            leftMost = arg1;
            rightMost = arg2;
            type = '>';
        } else {
            leftMost = arg2;
            rightMost = arg1;
            type = '<';
        }

        this.setOpaque(false);

//        this.addMouseMotionListener(new MouseMotionAdapter() {
//            @Override
//            public void mouseMoved(MouseEvent e) {
//                if (!onTop(e.getX(), e.getY())) {
//                    if (lockRedraw) {
//                        lockRedraw = false;
//
//                        if (getParent() instanceof ArgumentionFramework) {
//                            ((ArgumentionFramework) getParent()).setFocus(null);
//                        }
//                    }
//                } else {
//                    if (!lockRedraw) {
//                        lockRedraw = true;
//
//                        if (getParent() instanceof ArgumentionFramework) {
//                            ((ArgumentionFramework) getParent()).setFocus(thisRef);
//                        }
//                    }
//                }
//            }
//        });
//
//        this.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseExited(MouseEvent e) {
//                lockRedraw = false;
//
//                if (getParent() instanceof ArgumentionFramework) {
//                    ((ArgumentionFramework) getParent()).setFocus(null);
//                }
//            }
//        });

        img = null;
    }

    /**
     * Defines the Argumentation Framework reference.
     * If not set, the default values are used.
     * @param myFramework the ArgumentationFramework reference
     * @return this Attack
     */
    public Attack setMyFramework(ArgumentionFramework myFramework) {
        this.myFramework = myFramework;
        return this;
    }
    
    private boolean onTop(int x, int y) {
        if (img == null) {
            return false;
        }
        if (img.getWidth() - 1 < x || img.getHeight() - 1 < y) {
            return false;
        }

        return img.getRGB(x, y) != 16777216;
    }
    
    private boolean onTop(int x, int y, Rectangle bound) {
        int correctedX = x - bound.x;
        int correctedY = y - bound.y;
        
        if(correctedX < 0 || correctedY < 0){
            return false;
        }
        
        if (img == null) {
            return false;
        }
        if (img.getWidth() - 1 < correctedX || img.getHeight() - 1 < correctedY) {
            return false;
        }

        return img.getRGB(correctedX, correctedY) != 16777216;
    }
    
    public boolean mouseMoved(int mouseX, int mouseY){
        if(onTop(myFramework.unScaledX(mouseX), myFramework.unScaledY(mouseY), getBounds())){
            if (!lockRedraw) {
                lockRedraw = true;

                if (getParent() instanceof ArgumentionFramework) {
                    ((ArgumentionFramework) getParent()).setFocus(thisRef);
                }
            }
            return true;
        }else{
            if (lockRedraw) {
                lockRedraw = false;

//                if (getParent() instanceof ArgumentionFramework) {
//                    ((ArgumentionFramework) getParent()).setFocus(null);
//                }
            }
            return false;
        }
    }

    /**
     * Defines if the attack is bidirectional
     * @param bidirectional if the argumento is bidirectional
     * @return this Attack
     */
    public Attack setBidirectional(boolean bidirectional) {
        this.bidirectional = bidirectional;
        return this;
    }

    /**
     * Returns the first argument.
     * @return first argument
     */
    public Argument getArgument1() {
        return a1;
    }

    /**
     * Returns the second argument.
     * @return second argument
     */
    public Argument getArgument2() {
        return a2;
    }
    
    /**
     * Tests if arg is subargument of this.a1 or this.a2.
     * @param arg target argument
     * @return true if arg is subargument of this.a1 or this.a2, false otherwise
     */
    public boolean isAbout(Argument arg){
        return a1.containsArgument(arg) || a2.containsArgument(arg);
    }

    /**
     * If the attack is bidirectional.
     * @return true if the attack is bidirectional, false otherwise
     */
    public boolean isBidirectional() {
        return bidirectional;
    }

    /**
     * Tests if attack is the inverse of this.
     * @param attack tested attack
     * @return true if attack.a1 == this.a2 AND attack.a2 == this.a1
     */
    public boolean isInverse(Attack attack) {
        return (this.equals(attack) ? attack.a1.equals(a2) : false);
    }

    /**
     * Tests if obj is equal to this.
     * @param obj tested object
     * @return if this == obj
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Attack)) {
            return false;
        }

        Attack objAttk = (Attack) obj;

        if (!objAttk.a1.equals(a1) && !objAttk.a1.equals(a2)) {
            return false;
        }

        if (!objAttk.a2.equals(a1) && !objAttk.a2.equals(a2)) {
            return false;
        }

        return true;
    }

    /**
     * Auxiliary to the equals override.
     * @return 
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.leftMost);
        hash = 89 * hash + Objects.hashCode(this.rightMost);
        return hash;
    }

    /**
     * Returns the proper y position for the attack component
     * @return components proper y position
     */
    public int getProperX() {
        return leftMost.getConclusionEastXBorder() + leftMost.getX();
    }

    /**
     * Returns the proper x position for the attack component
     * @return components proper x position
     */
    public int getProperY() {
        return Math.min(leftMost.getConclusionMiddleYPosition() + leftMost.getY(), rightMost.getConclusionMiddleYPosition() + rightMost.getY()) - 5;
    }

    /**
     * Returns the proper width for the attack component
     * @return components proper width
     */
    public int getProperWidth() {
        return (rightMost.getConclusionWeastXBorder() + rightMost.getX()) - (leftMost.getConclusionEastXBorder() + leftMost.getX());
    }

    /**
     * Returns the proper height for the attack component
     * @return components proper height
     */
    public int getProperHeight() {
        int max = (leftMost.getConclusionMiddleYPosition() + leftMost.getY()) - (rightMost.getConclusionMiddleYPosition() + rightMost.getY());
        if (max < 0) max *= -1;
        return max+10;
    }

    /**
     * Paints the Attack.
     * @param g graphics instance.
     */
    @Override
    public void paint(Graphics g) {
        if (!(this.getParent() instanceof ArgumentionFramework)) {
            throw new IllegalCallerException("An attack must be child of a ArgumentCluster to be painted.");
        }

        ArgumentionFramework cluster = (ArgumentionFramework) this.getParent();

        if (!cluster.containsArgument(a1) || !cluster.containsArgument(a2)) {
            throw new IllegalCallerException("Parent ArgumentCluster must contains both arguments.");
        }

        Graphics2D g2d = (Graphics2D) g.create();

        if (img != null && !dirty) {
            g2d.drawImage(img, 0, 0, this);
            return;
        }

        img = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);

        Point2D lMP = new Point(leftMost.getConclusionEastXBorder() + leftMost.getX(), leftMost.getConclusionMiddleYPosition() + leftMost.getY());
        Point2D rMP = new Point(rightMost.getConclusionWeastXBorder() + rightMost.getX(), rightMost.getConclusionMiddleYPosition() + rightMost.getY());

        double distance = lMP.distance(rMP);

        Graphics2D g2dBuffer = img.createGraphics();
        
        int posAx = 0;
        int posAy = (int) lMP.getY() - this.getY() - 5;
        int trAxSize = 10;
        int trAxMidd = 4;

        g2dBuffer.setPaint(new Color(1, 1, 1, 1));
        g2dBuffer.fillRect(0, 0, img.getWidth(), img.getHeight());

        g2dBuffer.setPaint((transparent ? ColorUtil.blend(getForeground(), (myFramework == null ? ArgumentionFramework.DEFAULT_CLUSTER_BACKGROUND_COLOR : myFramework.getBackground()), (myFramework == null ? ArgumentionFramework.DEFAULT_CLUSTER_FADEOFF : myFramework.getFadeoff())) : getForeground()));

        
        g2dBuffer.translate(0,posAy);
        g2dBuffer.rotate(Math.atan2((rMP.getY() - lMP.getY()), rMP.getX() - lMP.getX()));
        
        if (type == '<' || bidirectional) {
            g2dBuffer.fillPolygon(new int[]{trAxSize + posAx, posAx, trAxSize + posAx}, new int[]{0, trAxMidd, trAxSize}, 3);
        }

        posAx = (int) (distance - trAxSize) - 1;

        if (type == '>' || bidirectional) {
            g2dBuffer.fillPolygon(new int[]{trAxSize + posAx, posAx, posAx}, new int[]{trAxMidd, 0, trAxSize}, 3);
        }

        g2dBuffer.setStroke(new BasicStroke(3f));
        g2dBuffer.drawLine((type == '<' || bidirectional ? trAxSize : 1) + 1, 4, (int) (distance - (type == '>' || bidirectional ? trAxSize : 0)) - 2, 4);

        
        g2d.drawImage(img, 0, 0, this);

        dirty = false;
    }

    /**
     * Defines if the Attack is translucent. Mimics an alpha chanel.
     * @param translucent 
     */
    public void isTranslucent(boolean translucent) {

        if (this.transparent != translucent) {
            this.transparent = translucent;
            dirty = true;

            repaint();
        }

    }

    public void clear() {
        
    }
}
