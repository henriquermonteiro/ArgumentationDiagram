package utfpr.edu.argumentation.diagram;

import utfpr.edu.swing.utils.ColorUtil;
import utfpr.edu.swing.utils.ForegroundUpdateListenner;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.java.balloontip.BalloonTip;
import net.java.balloontip.positioners.LeftBelowPositioner;
import net.java.balloontip.styles.ToolTipBalloonStyle;

/**
 * Class that represents and draws an argument as a swing.JComponent.
 *
 * @author Henrique M R Jasinski
 */
public class Argument extends JPanel implements ForegroundUpdateListenner {

    public static final int ACCEPTED_NONFOCUSED_ARGUMENT_TYPE = 0;
    public static final int ACCEPTED_FOCUSED_ARGUMENT_TYPE = 1;
    public static final int REJECTED_NONFOCUSED_ARGUMENT_TYPE = 2;
    public static final int REJECTED_FOCUSED_ARGUMENT_TYPE = 3;

    private int type;
    private ArgumentionFramework myFramework;

    private Atom conclusion;
    private final JLabel argID;
    private final JLabel rule;
    private List<Argument> subArguments;
    private boolean strictRule;
    private boolean translucent = false;
    private final Argument thisRef;
    private BalloonTip ruleTooltipB;

    static int bracketGap = 6;
    static int bracketWidth = 8;

    private double sizeMultiplier = 1.0;

    /**
     * Constructor
     *
     * @param conclusion conclusion Atom
     * @param type argument type
     * @param myFramework ArgumentionFramework reference
     * @param _argID argument identifier
     * @param ruleID rule identifier
     * @param ruleTooltip rule tooltip text
     * @param strictRule if the rule is strict
     * @param subArguments the subarguments array
     */
    public Argument(Atom conclusion, int type, ArgumentionFramework myFramework, String _argID, String ruleID, String ruleTooltip, boolean strictRule, Argument... subArguments) {
        super();
        thisRef = this;

        this.setLayout(new ArgumentLayout(this));
        this.setOpaque(false);

        this.type = type;
        this.myFramework = myFramework;

        this.conclusion = conclusion;
        this.strictRule = strictRule;
        this.subArguments = new ArrayList<>();
        this.rule = new JLabel(ruleID.replaceAll("^([a-zA-Z0-9]*)(_([a-zA-Z0-9]*))?(\\^([a-zA-Z0-9]*))?", "<html><font color=#000000>$1<sub>$3</sub><sup>$5</sup></font></html>"));
        this.rule.setOpaque(true);
        this.rule.setBackground(new Color(0xf9f9f9));
        
        if(ruleTooltip != null){
            ruleTooltipB = new BalloonTip(this.rule, ruleTooltip, new ToolTipBalloonStyle(new Color(184, 207, 229), new Color(99, 130, 191)), false);
            ruleTooltipB.setVisible(false);
            ruleTooltipB.setPositioner(new RuleTooltipPositioner(5, 5));
        }
        
        this.argID = new JLabel(_argID.replaceAll("^([a-zA-Z0-9]*)(_([a-zA-Z0-9]*))?(\\^([a-zA-Z0-9]*))?", "<html><font color=#000000>$1<sub>$3</sub><sup>$5</sup></font></html>"));
        this.argID.setOpaque(true);
        this.argID.setBackground(new Color(0xf9f9f9));

        this.add(this.conclusion);
        this.add(this.rule);
        this.add(this.argID);

        int maxArgW = conclusion.getWidth();
        int maxArgH = 0;
        if (subArguments != null) {
            for (Argument arg : subArguments) {
                if (arg == null) {
                    continue;
                }
                this.subArguments.add(arg);
                this.add(arg);

                maxArgW = Math.max(maxArgW, arg.getWidth());
                maxArgH = Math.max(maxArgH, arg.getHeight());
            }
        }

        updateForegroundByType();

        this.getLayout().layoutContainer(this);
    }

    /**
     * Recursive method that defines a parent ArgumentionFramework focused
     * component.
     *
     * @param comp component to set as focused
     */
    protected void setParentFocus(Component comp) {
        if (getParent() instanceof ArgumentionFramework) {
            ((ArgumentionFramework) getParent()).setFocus(comp);
            return;
        }

        if (getParent() instanceof Argument) {
            ((Argument) getParent()).setParentFocus(comp);
        }
    }

    public boolean mouseMoved(int mouseX, int mouseY) {
        Rectangle bounds = this.conclusion.getBounds();
        bounds.x += getXToRoot() + getX();
        bounds.y += getYToRoot() + getY();
        
        if(this.conclusion.toolTip != null)
            this.conclusion.toolTip.setVisible(bounds.contains(myFramework.unScaledX(mouseX), myFramework.unScaledY(mouseY)));
        
        bounds = this.rule.getBounds();
        bounds.x += getXToRoot() + getX();
        bounds.y += getYToRoot() + getY();
        
        if(this.ruleTooltipB != null)
            this.ruleTooltipB.setVisible(bounds.contains(myFramework.unScaledX(mouseX), myFramework.unScaledY(mouseY)));
        
        bounds = this.argID.getBounds();
        bounds.x += getXToRoot() + getX();
        bounds.y += getYToRoot() + getY();
        if (bounds.contains(myFramework.unScaledX(mouseX), myFramework.unScaledY(mouseY))) {
            setParentFocus(thisRef);
            return true;
        } else {
            boolean onSubArg = false;
            for(Argument arg : subArguments){
                onSubArg = onSubArg | arg.mouseMoved(mouseX, mouseY);
            }
            
            return onSubArg;
//            setParentFocus(null);
        }
    }

    /**
     * Constructor with type = Argument.ACCEPTED_NONFOCUSED_ARGUMENT_TYPE ,
     * myFramework = null and strictRule = true
     *
     * @param conclusion conclusion Atom
     * @param argID argument identifier
     * @param ruleID rule identifier
     * @param subArguments the subarguments array
     */
    public Argument(Atom conclusion, String argID, String ruleID, Argument... subArguments) {
        this(conclusion, ACCEPTED_NONFOCUSED_ARGUMENT_TYPE, null, argID, ruleID, null, true, subArguments);
    }

    /**
     * Constructor with type = Argument.ACCEPTED_NONFOCUSED_ARGUMENT_TYPE ,
     * myFramework = null , strictRule = true and subArguments = null
     *
     * @param conclusion conclusion Atom
     * @param argID argument identifier
     * @param ruleID rule identifier
     */
    public Argument(Atom conclusion, String argID, String ruleID) {
        this(conclusion, ACCEPTED_NONFOCUSED_ARGUMENT_TYPE, null, argID, ruleID, null, true, (Argument[]) null);
    }

    /**
     * Constructor with type = Argument.ACCEPTED_NONFOCUSED_ARGUMENT_TYPE and
     * myFramework = null
     *
     * @param conclusion conclusion Atom
     * @param _argID argument identifier
     * @param ruleID rule identifier
     * @param strictRule if the rule is strict
     * @param subArguments the subarguments array
     */
    public Argument(Atom conclusion, String _argID, String ruleID, boolean strictRule, Argument... subArguments) {
        this(conclusion, ACCEPTED_NONFOCUSED_ARGUMENT_TYPE, null, _argID, ruleID, null, strictRule, subArguments);
    }

    /**
     * Defines the Argument type. The type must be one of:
     * Argument.ACCEPTED_NONFOCUSED_ARGUMENT_TYPE,
     * Argument.ACCEPTED_FOCUSED_ARGUMENT_TYPE,
     * Argument.REJECTED_NONFOCUSED_ARGUMENT_TYPE,
     * Argument.REJECTED_FOCUSED_ARGUMENT_TYPE
     *
     * @param type argument type
     * @return this argument
     */
    public Argument setType(int type) {
        this.type = type;
        updateForegroundByType();
        this.conclusion.setType(type);
        this.subArguments.forEach((sub) -> {
            sub.setType(type);
        });
        return this;
    }

    public int getType() {
        return type;
    }

    public void setRuleTooltipText(String tooltip) {
        if(ruleTooltipB != null){
            ruleTooltipB.closeBalloon();
        }
        
        if(tooltip == null) return;
        
        ruleTooltipB = new BalloonTip(this.rule, tooltip, new ToolTipBalloonStyle(new Color(184, 207, 229), new Color(99, 130, 191)), false);
        ruleTooltipB.setVisible(false);
        ruleTooltipB.setPositioner(new RuleTooltipPositioner(5, 5));
    }

    /**
     * Defines the ArgumentationFramework reference. If no ArgumentCluster is
     * defined, the default configurations are used.
     *
     * @param myFramework ArgumentationFramework reference
     * @return this argument
     */
    public Argument setMyFramework(ArgumentionFramework myFramework) {
        this.myFramework = myFramework;
        subArguments.forEach((arg) -> {arg.setMyFramework(myFramework);});
        return this;
    }

    /**
     * Tests if arg is an subargument.
     *
     * @param arg the argument tested
     * @return true if arg is an subargument, false otherwise
     */
    public boolean containsArgument(Argument arg) {
        if (this.argID.getText().equals(arg.argID.getText())) {
            System.out.print("");
        }
        if (this.equals(arg)) {
            return true;
        }

        return subArguments.stream().anyMatch((a) -> (a.containsArgument(arg)));
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

    /**
     * Returns the x position of the east border of the argument's conclusion.
     *
     * @return the east x position
     */
    protected int getConclusionEastXBorder() {
        return conclusion.getX() + conclusion.getWidth() + getXToRoot();
    }

    /**
     * Returns the x position of the west border of the argument's conclusion.
     *
     * @return the west x position
     */
    protected int getConclusionWeastXBorder() {
        return conclusion.getX() + getXToRoot();
    }

    /**
     * Returns the x position in the middle of the argument's conclusion.
     *
     * @return the middle x position
     */
    protected int getConclusionMiddleXPosition() {
        return conclusion.getX() + (conclusion.getWidth() / 2);
    }

    /**
     * Returns the y position in the middle of the argument's conclusion.
     *
     * @return the middle y position
     */
    protected int getConclusionMiddleYPosition() {
        return conclusion.getY() + (conclusion.getHeight() / 2);
    }

    /**
     * Recursive method that get the component parent. If the parent is a
     * ArgumentCluster checks if the arguments has focus. If the parent is an
     * Argument, calls haveFocus(comp). Else returns false.
     *
     * @param comp the component that the focus is tested
     * @return true if the component has the focus, false otherwise
     */
    protected boolean haveFocus(Component comp) {
        if (getParent() instanceof ArgumentionFramework) {
            return ((ArgumentionFramework) getParent()).isFocus(comp);
        }
        if (getParent() instanceof Argument) {
            return ((Argument) getParent()).haveFocus(comp);
        }

        return false;
    }

    /**
     * Changes the argument color. Mimics an alpha change. If translucesn is
     * true the colors change acording to ArgumentationFramework.CLUSTER_FADEOFF
     * or ArgumentationFramework.DEFAULT_CLUSTER_FADEOFF. Updates the conclusion
     * and subarguments setTranlucent(translucent).
     *
     * @param translucent if the argument must be translucent
     */
    public void isTranslucent(boolean translucent) {
        this.translucent = translucent;
        updateForegroundByType();

        rule.setText(rule.getText().replaceAll("#([0-9a-fA-F]){6}", (translucent ? "#" + Integer.toHexString(ColorUtil.blend(Color.BLACK, (myFramework == null ? ArgumentionFramework.DEFAULT_CLUSTER_BACKGROUND_COLOR : myFramework.getBackground()), (myFramework == null ? ArgumentionFramework.DEFAULT_CLUSTER_FADEOFF : myFramework.getFadeoff())).getRGB()).substring(2) : "#000000")));
        argID.setText(argID.getText().replaceAll("#([0-9a-fA-F]){6}", (translucent ? "#" + Integer.toHexString(ColorUtil.blend(Color.BLACK, (myFramework == null ? ArgumentionFramework.DEFAULT_CLUSTER_BACKGROUND_COLOR : myFramework.getBackground()), (myFramework == null ? ArgumentionFramework.DEFAULT_CLUSTER_FADEOFF : myFramework.getFadeoff())).getRGB()).substring(2) : "#000000")));

        rule.setForeground((translucent ? getForeground().brighter() : getForeground()));
        argID.setForeground(getForeground());

        conclusion.setTranslucent(translucent);

        for (Argument arg : subArguments) {
            arg.isTranslucent(!haveFocus(arg));
        }

//        repaint();
        repaintParent();
    }

    private void repaintParent() {
        if (getParent() instanceof Argument) {
            ((Argument) getParent()).repaintParent();
        } else {
            getParent().repaint();
        }
    }

    /**
     * Paints the lines from rules and brackets of the argument.
     *
     * @param g graphics instance
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D g2d = (Graphics2D) g.create();

        g2d.setPaint(getForeground());
        g2d.setStroke(new BasicStroke((float) sizeMultiplier));

        // paint right bracket if has no subarguments
        if (subArguments.isEmpty()) {
            int baseX = conclusion.getX() + conclusion.getWidth() + bracketGap + (int) (sizeMultiplier / 2);

            // Right "border"
            g2d.drawLine(baseX + bracketWidth - (int) Math.round(sizeMultiplier), conclusion.getY() - 1 + ((int) (sizeMultiplier / 2)), baseX + bracketWidth - (int) Math.round(sizeMultiplier), conclusion.getY() + conclusion.getHeight());
            // Top "border"
            g2d.drawLine(baseX, conclusion.getY() - 1 + ((int) (sizeMultiplier / 2)), baseX + bracketWidth - (int) Math.round(sizeMultiplier), conclusion.getY() - 1 + ((int) (sizeMultiplier / 2)));
            // Botton "border"
            g2d.drawLine(baseX, conclusion.getY() + conclusion.getHeight(), baseX + bracketWidth - (int) Math.round(sizeMultiplier), conclusion.getY() + conclusion.getHeight());

            return;
        }

        int middleX = getConclusionMiddleXPosition();

        if (!strictRule) {
            g2d.setStroke(new BasicStroke((float) (1 * sizeMultiplier), BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5}, 0));
        }

        int halfWay = rule.getY() + (int) (1.6 * rule.getHeight());

        // Draw line from conclusion to rule label
        g2d.drawLine(middleX, conclusion.getY() + conclusion.getHeight(), middleX, rule.getY());
        // Draw line from rule to half distance to subarguments
        g2d.drawLine(middleX, rule.getY() + rule.getHeight() + 2, middleX, halfWay);

        int minX = conclusion.getX();
        int maxHeight = conclusion.getY() + conclusion.getHeight();

        for (Argument arg : subArguments) {
            int conclusionMX = arg.getConclusionMiddleXPosition() + arg.getX();

            // Draw vertical line from top of subargument to half distance to rule
            g2d.drawLine(conclusionMX, arg.getY() - 2, conclusionMX, halfWay);
            // Draw horizontal line from top of subargument to half distance to rule
            g2d.drawLine(conclusionMX, halfWay, middleX, halfWay);

            minX = Math.min(minX, arg.getX());
            maxHeight = Math.max(maxHeight, arg.getY() + arg.getHeight() - arg.argID.getHeight());
        }

        g2d.setStroke(new BasicStroke((float) (1 * sizeMultiplier)));

        // Paint left bracket
        minX -= bracketGap + 1 + (int) (sizeMultiplier / 2);
        // Left "border"
        g2d.drawLine(minX - bracketWidth + (int) (sizeMultiplier), conclusion.getY(), minX - bracketWidth + (int) (sizeMultiplier), maxHeight);
        // Top "border"
        g2d.drawLine(minX - bracketWidth + (int) (sizeMultiplier), conclusion.getY(), minX, conclusion.getY());
        // Bottom "border"
        g2d.drawLine(minX - bracketWidth + (int) (sizeMultiplier), maxHeight, minX, maxHeight);
    }

    /**
     * Updates the foreground color by the argument type. Colors are defined in
     * the ArgumentationFramework class. Triggers the conclusion and
     * subarguments foregroundUpdated() methods.
     */
    protected void updateForegroundByType() {
        switch (type) {
            case ACCEPTED_NONFOCUSED_ARGUMENT_TYPE:
                if (myFramework != null) {
                    setForeground(myFramework.ACCEPTED_NONFOCUSED_ARGUMENT_COLOR);
                } else {
                    setForeground(ArgumentionFramework.DEFAULT_ACCEPTED_NONFOCUSED_ARGUMENT_COLOR);
                }
                break;
            case ACCEPTED_FOCUSED_ARGUMENT_TYPE:
                if (myFramework != null) {
                    setForeground(myFramework.ACCEPTED_FOCUSED_ARGUMENT_COLOR);
                } else {
                    setForeground(ArgumentionFramework.DEFAULT_ACCEPTED_FOCUSED_ARGUMENT_COLOR);
                }
                break;
            case REJECTED_NONFOCUSED_ARGUMENT_TYPE:
                if (myFramework != null) {
                    setForeground(myFramework.REJECTED_NONFOCUSED_ARGUMENT_COLOR);
                } else {
                    setForeground(ArgumentionFramework.DEFAULT_REJECTED_NONFOCUSED_ARGUMENT_COLOR);
                }
                break;
            case REJECTED_FOCUSED_ARGUMENT_TYPE:
                if (myFramework != null) {
                    setForeground(myFramework.REJECTED_FOCUSED_ARGUMENT_COLOR);
                } else {
                    setForeground(ArgumentionFramework.DEFAULT_REJECTED_FOCUSED_ARGUMENT_COLOR);
                }
                break;
        }

        if (translucent) {
            setForeground(ColorUtil.blend(getForeground(), (myFramework == null ? ArgumentionFramework.DEFAULT_CLUSTER_BACKGROUND_COLOR : myFramework.getBackground()), (myFramework == null ? ArgumentionFramework.DEFAULT_CLUSTER_FADEOFF : myFramework.getFadeoff())));
        }

        conclusion.foregroundUpdated();
        subArguments.forEach((arg0) -> {
            arg0.foregroundUpdated();
        });
    }

    /**
     * Implementats the ForegroundUpdatedListener. Calls
     * updateForegroundByType();
     */
    @Override
    public void foregroundUpdated() {
        updateForegroundByType();
    }

    /**
     * Set the line thickness multiplier. Must be >= 1.
     *
     * @param sizeMultiplier line multiplier
     */
    public void setSizeMultiplier(double sizeMultiplier) {
        if (sizeMultiplier < 1) {
            sizeMultiplier = 1.0;
        }
        this.sizeMultiplier = sizeMultiplier;
        this.conclusion.setSizeMultiplier(this.sizeMultiplier);
        this.subArguments.forEach((arg) -> {
            arg.setSizeMultiplier(this.sizeMultiplier);
        });
        revalidate();
        repaint();
    }

    /**
     * Returns the required witdh of a bracket. Defined by Argument.bracketGap +
     * Argument.bracketWidth
     *
     * @return bracket required width
     */
    protected int getBracketWidth() {
        return bracketGap + bracketWidth;
    }

    public void clear() {
        ruleTooltipB.closeBalloon();
        conclusion.clear();
        subArguments.forEach((arg) -> {arg.clear();});
    }

    /**
     * Class responsible for defining the layout of any given Argument.
     */
    class ArgumentLayout implements LayoutManager {

        private final Argument argument;

        private int subWidth = 0;
        private int subHeight = 0;

        /**
         * Constructor. Sets the reference for the respective Argument.
         *
         * @param arg0 Argument instance
         */
        public ArgumentLayout(Argument argument) {
            this.argument = argument;
        }

        /**
         * Unused.
         *
         * @param arg0
         */
        @Override
        public void addLayoutComponent(String arg0, Component arg1) {
        }

        /**
         * Unused.
         *
         * @param arg0
         */
        @Override
        public void removeLayoutComponent(Component arg0) {
        }

        /**
         * Returns the minimum layout size. Preferred size = minimum size.
         *
         * @param arg0 the container
         * @return minimum layout size
         */
        @Override
        public Dimension preferredLayoutSize(Container arg0) {
            return minimumLayoutSize(arg0);
        }

        /**
         * Returns the minimun size.
         *
         * @param arg0 the container
         * @return minimum layout size
         */
        @Override
        public Dimension minimumLayoutSize(Container arg0) {
            Dimension concPrefD = conclusion.getPreferredSize();
            Dimension argLPrefD = argID.getPreferredSize();

            int divideBy = -1;

            // Find maximum subargument height and width
            for (Argument arg : subArguments) {
                Dimension argPrefSize = arg.getPreferredSize();
                subHeight = Math.max(subHeight, argPrefSize.height - arg.argID.getPreferredSize().height + 5);
                subWidth = Math.max(subWidth, argPrefSize.width);

                if (divideBy == -1) {
                    if (arg.subArguments.isEmpty()) {
                        divideBy = 2;
                    } else {
                        divideBy = 1;
                    }
                }
            }

            if (divideBy == -1) {
                divideBy = 2;
            }

            int subArgsTotalWidth = (subWidth + (subArguments.size() > 1 ? 5 : 0)) * subArguments.size();
            int concTotalWidth = concPrefD.width + Math.max(argLPrefD.width - (concPrefD.width / 2), getBracketWidth());

            int width = Math.max(subArgsTotalWidth + getBracketWidth() + (int) (argLPrefD.width / divideBy) - (divideBy == 1 ? getBracketWidth() - 2 : 0), concTotalWidth);
            int height = subHeight + concPrefD.height + argLPrefD.height + (subArguments.isEmpty() ? 5 : (conclusion.getPreferredSize().height * 3) + 5);

            return new Dimension(width, height);
        }

        /**
         * Define each component position.
         *
         * @param arg0 the container
         */
        @Override
        public void layoutContainer(Container arg0) {
            int y;

            minimumLayoutSize(arg0);

            Component ax = argument.conclusion;
            Dimension axPrefSize = ax.getPreferredSize();

            if (argument.subArguments.isEmpty()) {
                ax.setBounds(0, 5, axPrefSize.width, axPrefSize.height);

                Component ax2 = argument.argID;
                Dimension ax2PrefSize = ax2.getPreferredSize();

                ax2.setBounds(ax.getX() + (axPrefSize.width / 2), ax.getY() + axPrefSize.height, ax2PrefSize.width, ax2PrefSize.height);
                return;
            }
            int minX = arg0.getWidth();

            y = (axPrefSize.height * 4) + 5 + subHeight;

            Component ax2 = argument.argID;
            Dimension ax2PrefSize = ax2.getPreferredSize();

            ax2.setBounds(0, y, ax2PrefSize.width, ax2PrefSize.height);

            y -= subHeight;

            Argument middleArg = null;
            Argument middleArg2 = null;
            boolean isOdd = subArguments.size() % 2 == 1;
            int midIndex = subArguments.size() / 2;

            int startX = (int) (ax2PrefSize.width / 2) + getBracketWidth();

            int offset = 0;
            for (Argument arg : argument.subArguments) {
                if (offset == 0) {
                    if (!arg.subArguments.isEmpty()) {
                        startX += (int) (ax2PrefSize.width / 2) - getBracketWidth() + 2;
                    }
                }
                Dimension argPrefSize = arg.getPreferredSize();
                arg.setBounds(startX + (offset * (subWidth + 5)), y, argPrefSize.width, argPrefSize.height);

                if (offset == midIndex) {
                    middleArg = arg;
                }
                if (!isOdd) {
                    if (offset == midIndex - 1) {
                        middleArg2 = arg;
                    }
                }

                offset++;

                minX = Math.min(minX, arg.getX());
            }

            y -= axPrefSize.height * 2.5;

            int middleX = (isOdd ? middleArg.getConclusionMiddleXPosition() + middleArg.getX() : (middleArg2.getConclusionMiddleXPosition() + middleArg2.getX() + middleArg.getConclusionMiddleXPosition() + middleArg.getX()) / 2);
            ax = argument.rule;
            axPrefSize = ax.getPreferredSize();

            ax.setBounds(middleX - (axPrefSize.width / 2), y, axPrefSize.width, axPrefSize.height);

            ax = argument.conclusion;
            axPrefSize = ax.getPreferredSize();

            y -= axPrefSize.height * 1.5;

            ax.setBounds(middleX - (axPrefSize.width / 2), y, axPrefSize.width, axPrefSize.height);
        }

    }
    
    class RuleTooltipPositioner extends LeftBelowPositioner{

        public RuleTooltipPositioner(int hO, int vO) {
            super(hO, vO);
        }

        @Override
        protected void determineLocation(Rectangle attached) {
            if(myFramework != null){
                Point myFrameP = myFramework.getAFPositionOnFrame(getBalloonTip().getTopLevelContainer());
                attached.setSize((int)(rule.getWidth() * myFramework.getScaling()), (int)(rule.getHeight() * myFramework.getScaling()));
                attached.setLocation((int)((rule.getX() + getX() + getXToRoot()) * myFramework.getScaling()) + myFrameP.x, (int)((rule.getY() + getY() + getYToRoot()) * myFramework.getScaling()) + myFrameP.y);
            }
            super.determineLocation(attached);
        }
        
    }
}
