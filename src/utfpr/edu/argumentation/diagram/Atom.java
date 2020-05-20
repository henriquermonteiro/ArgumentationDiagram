package utfpr.edu.argumentation.diagram;

import utfpr.edu.swing.utils.ColorUtil;
import utfpr.edu.swing.utils.ForegroundUpdateListenner;
import utfpr.edu.swing.utils.SharpDashedBorder;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import net.java.balloontip.BalloonTip;
import net.java.balloontip.positioners.LeftBelowPositioner;
import net.java.balloontip.styles.ToolTipBalloonStyle;

/**
 * Class that represents an atom from an argument as a swing.JComponent.
 * An argument's conclusion is considered an Atom.
 * @author Henrique M R Jasinski
 */
public class Atom extends JLabel implements ForegroundUpdateListenner {

    public static final int ACCEPTED_NONFOCUSED_ARGUMENT_TYPE = 0;
    public static final int ACCEPTED_FOCUSED_ARGUMENT_TYPE = 1;
    public static final int REJECTED_NONFOCUSED_ARGUMENT_TYPE = 2;
    public static final int REJECTED_FOCUSED_ARGUMENT_TYPE = 3;

    private boolean strict;
    private int type;
    private ArgumentionFramework myFramework;
    private boolean useClusterForegroundColorPallet = true;
    private boolean translucent = false;

    private Color borderColor = getForeground();
    
    private double sizeMultiplier = 1.0;
    
    public final BalloonTip toolTip;

    /**
     * Constructor with tooltip = "" , strict = true , type = Atom.ACCEPTED_NONFOCUSED_ARGUMENT_TYPE and myFramework = null
     * @param label label displayed
     */
    public Atom(String label) {
        this(label, "", true, ACCEPTED_NONFOCUSED_ARGUMENT_TYPE, null);
    }

    /**
     * Constructor with strict = true , type = Atom.ACCEPTED_NONFOCUSED_ARGUMENT_TYPE and myFramework = null
     * @param label label displayed
     * @param tooltip tooltip displayed
     */
    public Atom(String label, String tooltip) {
        this(label, tooltip, true, ACCEPTED_NONFOCUSED_ARGUMENT_TYPE, null);
    }

    /**
     * Constructor with tooltip = "" , type = Atom.ACCEPTED_NONFOCUSED_ARGUMENT_TYPE and myFramework = null
     * @param label label displayed
     * @param strict if the Atom is strict
     */
    public Atom(String label, boolean strict) {
        this(label, "", strict, ACCEPTED_NONFOCUSED_ARGUMENT_TYPE, null);
    }

    /**
     * Constructor with type = Atom.ACCEPTED_NONFOCUSED_ARGUMENT_TYPE and myFramework = null
     * @param label label displayed
     * @param tooltip tooltip displayed
     * @param strict if the Atom is strict
     */
    public Atom(String label, String tooltip, boolean strict) {
        this(label, tooltip, strict, ACCEPTED_NONFOCUSED_ARGUMENT_TYPE, null);
    }

    /**
     * Changed the color based on the Atom type.
     */
    protected void updateForegroundByType() {
        switch (type) {
            case ACCEPTED_NONFOCUSED_ARGUMENT_TYPE:
                if (myFramework != null) {
                    borderColor = (myFramework.ACCEPTED_NONFOCUSED_ARGUMENT_COLOR);
                } else {
                    borderColor = (ArgumentionFramework.DEFAULT_ACCEPTED_NONFOCUSED_ARGUMENT_COLOR);
                }
                break;
            case ACCEPTED_FOCUSED_ARGUMENT_TYPE:
                if (myFramework != null) {
                    borderColor = (myFramework.ACCEPTED_FOCUSED_ARGUMENT_COLOR);
                } else {
                    borderColor = (ArgumentionFramework.DEFAULT_ACCEPTED_FOCUSED_ARGUMENT_COLOR);
                }
                break;
            case REJECTED_NONFOCUSED_ARGUMENT_TYPE:
                if (myFramework != null) {
                    borderColor = (myFramework.REJECTED_NONFOCUSED_ARGUMENT_COLOR);
                } else {
                    borderColor = (ArgumentionFramework.DEFAULT_REJECTED_NONFOCUSED_ARGUMENT_COLOR);
                }
                break;
            case REJECTED_FOCUSED_ARGUMENT_TYPE:
                if (myFramework != null) {
                    borderColor = (myFramework.REJECTED_FOCUSED_ARGUMENT_COLOR);
                } else {
                    borderColor = (ArgumentionFramework.DEFAULT_REJECTED_FOCUSED_ARGUMENT_COLOR);
                }
                break;
        }

        if (translucent) {
            borderColor = (ColorUtil.blend(borderColor, (myFramework == null ? ArgumentionFramework.DEFAULT_CLUSTER_BACKGROUND_COLOR : myFramework.getBackground()), (myFramework == null? ArgumentionFramework.DEFAULT_CLUSTER_FADEOFF : myFramework.getFadeoff())));
        }
        
        setText(getText().replaceAll("#([0-9a-fA-F]){6}", (translucent ? "#" + Integer.toHexString(ColorUtil.blend(Color.BLACK, (myFramework == null? ArgumentionFramework.DEFAULT_CLUSTER_BACKGROUND_COLOR : myFramework.getBackground()), (myFramework == null? ArgumentionFramework.DEFAULT_CLUSTER_FADEOFF : myFramework.getFadeoff())).getRGB()).substring(2)  : "#000000")));
        
        if (!strict) {
            this.setBorder(BorderFactory.createCompoundBorder(new SharpDashedBorder(borderColor, 0, (float)(1*sizeMultiplier), BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5}, 0), BorderFactory.createEmptyBorder(3, 3, 3, 3)));
        } else {
            this.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(borderColor, (int) (1 * sizeMultiplier)), BorderFactory.createEmptyBorder(3, 3, 3, 3)));
        }
    }

    /**
     * Change the Atom color. Mimics a change in the alpha.
     * @param translucent if the atom is translucent
     */
    public void setTranslucent(boolean translucent) {
        boolean updated = this.translucent != translucent;
        this.translucent = translucent;
        if (updated) {
            updateForegroundByType();
        }
    }

    /**
     * Sets the Atom line thickness multiplier.
     * @param sizeMultiplier line multiplier. Must be >= 1
     * @return this Atom
     */
    public Atom setSizeMultiplier(double sizeMultiplier) {
        if(sizeMultiplier < 1){
            sizeMultiplier = 1.0;
        }
        this.sizeMultiplier = sizeMultiplier;
        updateForegroundByType();
        return this;
    }

    /**
     * Constructor.
     * String in the label is changed such that characters after '_' are subscript and after '^' are superscripted.
     * @param label label displayed
     * @param tooltip tooltip displayed
     * @param strict if the atom is strict
     * @param type the type of the Atom
     * @param myFramework the argumentationframework reference
     */
    public Atom(String label, String tooltip, boolean strict, int type, ArgumentionFramework myFramework) {
        super(label.replaceAll("^([a-zA-Z0-9]*)(_([a-zA-Z0-9]*))?(\\^([a-zA-Z0-9]*))?", "<html><font color=#000000>$1<sub>$3</sub><sup>$5</sup></font></html>"));
        this.strict = strict;
        this.type = type;
        this.myFramework = myFramework;

        this.toolTip = new BalloonTip(this, tooltip, new ToolTipBalloonStyle(new Color(184, 207, 229), new Color(99, 130, 191)), false);
        this.toolTip.setVisible(false);
        this.toolTip.setPositioner(new AtomTooltipPositioner(5, 5));

        updateForegroundByType();
    }

    /**
     * Set the ArgumentationFramework reference.
     * If no argumentation framework is provided, the default values are used.
     * @param myFramework ArgumentationFramewrok reference
     * @return this Atom
     */
    public Atom setMyFramewrok(ArgumentionFramework myFramework) {
        this.myFramework = myFramework;
        return this;
    }

    /**
     * Sets the Atom type.
     * @param type atom type
     * @return this Atom
     */
    public Atom setType(int type) {
        this.type = type;
        updateForegroundByType();
        return this;
    }

    /**
     * Define if the Atom is strict.
     * @param strict if the Atom is strict
     * @return this Atom
     */
    public Atom setStrict(boolean strict) {
        this.strict = strict;
        return this;
    }

    /**
     * Implements ForegroundUpdateListener
     */
    @Override
    public void foregroundUpdated() {
        updateForegroundByType();
    }

    /**
     * Returns the x position the argument in relation to the root argument. The
     * root argument is an argument which parent is not an Argument.
     *
     * @return the x position relative to the root
     */
    protected int getXToRoot() {
        int x = 0;

        Container parent = getParent();

        while (parent instanceof Argument) {
            x += parent.getX();
            parent = parent.getParent();
        }

        return x;
    }

    /**
     * Returns the y position the argument in relation to the root argument. The
     * root argument is an argument which parent is not an Argument.
     *
     * @return the y position relative to the root
     */
    protected int getYToRoot() {
        int y = 0;

        Container parent = getParent();

        while (parent instanceof Argument) {
            y += parent.getY();
            parent = parent.getParent();
        }

        return y;
    }

    void clear() {
        toolTip.closeBalloon();
    }
    
    class AtomTooltipPositioner extends LeftBelowPositioner{

        public AtomTooltipPositioner(int hO, int vO) {
            super(hO, vO);
        }

        @Override
        protected void determineLocation(Rectangle attached) {
            if(myFramework != null){
                Point myFrameP = myFramework.getAFPositionOnFrame(getBalloonTip().getTopLevelContainer());
                attached.setSize((int)(getWidth() * myFramework.getScaling()), (int)(getHeight() * myFramework.getScaling()));
                attached.setLocation((int)((getX() + getXToRoot()) * myFramework.getScaling()) + myFrameP.x, (int)((getY() + getYToRoot()) * myFramework.getScaling()) + myFrameP.y);
            }
            super.determineLocation(attached);
        }
        
    }
}
