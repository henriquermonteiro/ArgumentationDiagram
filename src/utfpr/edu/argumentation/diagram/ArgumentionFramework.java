package utfpr.edu.argumentation.diagram;

import java.awt.BasicStroke;
import utfpr.edu.swing.utils.ForegroundUpdateListenner;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Label;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import utfpr.edu.swing.utils.ColorUtil;

/**
 * Class that represents an argumentation framework as a swing.JContainer.
 * @author Henrique M R Jasinski
 */
public class ArgumentionFramework extends JLayeredPane {

    private final ArrayList<Argument> arguments;
    private final HashSet<Attack> attacks;
    private Component focused = null;
    private Integer focusedType = null;
    private int newXPos = 0;
    private final int gap = 20;

    private final static int ARGUMENTS_DEFAULT_LAYER = 250;
    private final static int ARGUMENTS_ALT_LAYER = 500;
    private final static int ATTACKS_DEFAULT_LAYER = 0;
    private final static int ATTACKS_ALT_LAYER = 750;

    public static final Color DEFAULT_ACCEPTED_NONFOCUSED_ARGUMENT_COLOR = Color.BLACK;
    public static final Color DEFAULT_ACCEPTED_FOCUSED_ARGUMENT_COLOR = Color.BLUE;
    public static final Color DEFAULT_REJECTED_NONFOCUSED_ARGUMENT_COLOR = Color.RED;
    public static final Color DEFAULT_REJECTED_FOCUSED_ARGUMENT_COLOR = new Color(0x8A69AC);
    public static final Color DEFAULT_CLUSTER_BACKGROUND_COLOR = new Color(0xf9f9f9);
    
    public static final Float DEFAULT_CLUSTER_FADEOFF = 0.25f;

    protected Color ACCEPTED_NONFOCUSED_ARGUMENT_COLOR = DEFAULT_ACCEPTED_NONFOCUSED_ARGUMENT_COLOR;
    protected Color ACCEPTED_FOCUSED_ARGUMENT_COLOR = DEFAULT_ACCEPTED_FOCUSED_ARGUMENT_COLOR;
    protected Color REJECTED_NONFOCUSED_ARGUMENT_COLOR = DEFAULT_REJECTED_NONFOCUSED_ARGUMENT_COLOR;
    protected Color REJECTED_FOCUSED_ARGUMENT_COLOR = DEFAULT_REJECTED_FOCUSED_ARGUMENT_COLOR;
    
    protected Float CLUSTER_FADEOFF = DEFAULT_CLUSTER_FADEOFF;

    protected ArrayList<ForegroundUpdateListenner> foregroundListenners;

    private double sizeMultiplier = 1.0;
    private double scaling = 1.0;
    
    private final Label emptyMessage; 

    /**
     * Constructor of an ArgumentationFramework.
     */
    public ArgumentionFramework() {
        super();

        this.setOpaque(true);
        this.setBackground(DEFAULT_CLUSTER_BACKGROUND_COLOR);
        
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                repositionComponents();
            }
        });

        arguments = new ArrayList<>();
        attacks = new HashSet<>();
        foregroundListenners = new ArrayList<>();
        emptyMessage = new Label();
        this.add(emptyMessage);
        
        this.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                boolean overComponent = false;
                for(Argument arg : arguments){
                    overComponent = overComponent | arg.mouseMoved(e.getX(), e.getY());
                }
                for(Attack attck : attacks){
                    overComponent = overComponent | attck.mouseMoved(e.getX(), e.getY());
                }
                if(!overComponent){
                    setFocus(null);
                }
            }
        });
    }
    
    protected ImageIcon getSelectedIcon(Color background, Color border, Color tick, int width, int height, boolean selected){
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        
        Graphics2D g2D = (Graphics2D) img.getGraphics();
        
        g2D.setPaint(new Color(0xFFFFFFFF, true));
        g2D.fillRect(0, 0, width, height);
        
        g2D.setPaint((selected ? background : ColorUtil.blend(background, Color.white, 0.35f)));
        g2D.fillRect(2, 2, width - 4, height - 4);
        
        g2D.setPaint(border);
        g2D.setStroke(new BasicStroke(1.5f));
        g2D.drawRect(2, 2, width - 4, height - 4);
        
//        if(selected){
//            g2D.setPaint(tick);
//            g2D.setStroke(new BasicStroke(2f));
//            g2D.drawPolyline(new int[]{(width/4),(width/2), width-6}, new int[]{height/2, height - 8, 8}, 3);
//        }
        
        return new ImageIcon(img);
    }
    
    private JPanel diagramColorLegend;
    public JPanel createDiagramColorLegend(int flowOrientation, int hgap, int vgap){
        if(diagramColorLegend != null) return diagramColorLegend;
        
        diagramColorLegend = new JPanel(new FlowLayout(flowOrientation, hgap, vgap));
        
        int boxSize = 24;
        
        JCheckBox acceptedNonFocus = new JCheckBox("Accepted non-focused");
        acceptedNonFocus.setSelectedIcon(getSelectedIcon(ACCEPTED_NONFOCUSED_ARGUMENT_COLOR, Color.BLACK, Color.BLACK, boxSize, boxSize, true));
        acceptedNonFocus.setIcon(getSelectedIcon(ACCEPTED_NONFOCUSED_ARGUMENT_COLOR, Color.BLACK, Color.BLACK, boxSize, boxSize, false));
        acceptedNonFocus.setSelected(true);
        acceptedNonFocus.addChangeListener((arg0) -> {
            if(!acceptedNonFocus.isSelected()){
                acceptedNonFocus.setSelected(true);
            }
        });
        acceptedNonFocus.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                setTypeFocus(null);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                setTypeFocus(0);
            }
        });
        JCheckBox acceptedFocus = new JCheckBox("Accepted focused");
        acceptedFocus.setSelectedIcon(getSelectedIcon(ACCEPTED_FOCUSED_ARGUMENT_COLOR, Color.BLACK, Color.BLACK, boxSize, boxSize, true));
        acceptedFocus.setIcon(getSelectedIcon(ACCEPTED_FOCUSED_ARGUMENT_COLOR, Color.BLACK, Color.BLACK, boxSize, boxSize, false));
        acceptedFocus.setSelected(true);
        acceptedFocus.addChangeListener((arg0) -> {
            if(!acceptedFocus.isSelected()){
                acceptedFocus.setSelected(true);
            }
        });
        acceptedFocus.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                setTypeFocus(null);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                setTypeFocus(1);
            }
        });
        JCheckBox rejectedNonFocus = new JCheckBox("Rejected non-focused");
        rejectedNonFocus.setSelectedIcon(getSelectedIcon(REJECTED_NONFOCUSED_ARGUMENT_COLOR, Color.BLACK, Color.BLACK, boxSize, boxSize, true));
        rejectedNonFocus.setIcon(getSelectedIcon(REJECTED_NONFOCUSED_ARGUMENT_COLOR, Color.BLACK, Color.BLACK, boxSize, boxSize, false));
        rejectedNonFocus.setSelected(true);
        rejectedNonFocus.addChangeListener((arg0) -> {
            if(!rejectedNonFocus.isSelected()){
                rejectedNonFocus.setSelected(true);
            }
        });
        rejectedNonFocus.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                setTypeFocus(null);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                setTypeFocus(2);
            }
        });
        JCheckBox rejectedFocus = new JCheckBox("Rejected focused");
        rejectedFocus.setSelectedIcon(getSelectedIcon(REJECTED_FOCUSED_ARGUMENT_COLOR, Color.BLACK, Color.BLACK, boxSize, boxSize, true));
        rejectedFocus.setIcon(getSelectedIcon(REJECTED_FOCUSED_ARGUMENT_COLOR, Color.BLACK, Color.BLACK, boxSize, boxSize, false));
        rejectedFocus.setSelected(true);
        rejectedFocus.addChangeListener((arg0) -> {
            if(!rejectedFocus.isSelected()){
                rejectedFocus.setSelected(true);
            }
        });
        rejectedFocus.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                setTypeFocus(null);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                setTypeFocus(3);
            }
        });
        
        diagramColorLegend.add(acceptedNonFocus);
        diagramColorLegend.add(acceptedFocus);
        diagramColorLegend.add(rejectedNonFocus);
        diagramColorLegend.add(rejectedFocus);
        
        return diagramColorLegend;
    }

    /**
     * Returns false. Allows for propper drawing of stacked components.
     * @return false
     */
    @Override
    public boolean isOptimizedDrawingEnabled() {
        return false;
    }
    
    /**
     * Defines the message displayed when the ArgumentationFramework is empty.
     * @param message 
     */
    public void setEmptyMessage(String message){
        emptyMessage.setText(message);
    }

    public double getScaling() {
        return scaling;
    }

    public void setScaling(double scaling) {
        this.scaling = scaling;
    }
    
    public int unScaledX(int x){
        return (int) ((x-5) / scaling);
    }
    
    public int unScaledY(int y){
        return (int) ((y-5) / scaling);
    }

    /**
     * Adds a new argument if it have not been added yet.
     * @param arg argument to be added
     */
    public void addArgument(Argument arg) {
        if (arg != null) {
            if (!arguments.contains(arg)) {
                arguments.add(arg);
                
                arg.setSizeMultiplier(sizeMultiplier);

                Dimension d = arg.getPreferredSize();

                arg.setBounds(newXPos, 0, d.width, d.height);
                this.add(arg, (Integer) (ARGUMENTS_DEFAULT_LAYER));
                foregroundListenners.add(arg);
                
                emptyMessage.setVisible(false);

                newXPos += d.width + gap;
            }
        }
    }

    public boolean containsArgument(Argument arg) {
        return arguments.stream().anyMatch((a) -> (a.containsArgument(arg)));
    }

    /**
     * Add a new attack instance.
     * Arguments a1 and a2 must have been already added or be a subargument of an already added argument.
     * If the attack is NOT bidirectional the relation is defined as a1 attacks a2.
     * If there is already an attack semantically equal to a2 attacks a1, than the previous attack is set to bidirectional.
     * @param a1 first argument
     * @param a2 second argument
     * @param bidirectional if the attack if mutual. 
     */
    public void addAttack(Argument a1, Argument a2, boolean bidirectional) {
        if (a1 == null || a2 == null) {
            return;
        }

        if (!containsArgument(a1) || !containsArgument(a2)) {
            return;
        }

        Attack attck = new Attack(a1, a2, this).setBidirectional(bidirectional);

        if (attacks.contains(attck)) {
            for (Attack at : attacks) {
                if (at.equals(attck)) {
                    if (!at.isBidirectional()) {
                        if (at.isInverse(attck)) {
                            at.setBidirectional(true);
                            repaint();
                        }
                    }

                    break;
                }
            }
        } else {
            attacks.add(attck);
            attck.setBounds(attck.getProperX(), attck.getProperY(), attck.getProperWidth(), attck.getProperHeight());

            this.add(attck, (Integer) (ATTACKS_DEFAULT_LAYER));
        }
    }

    /**
     * Set the every component's bound.
     */
    public void repositionComponents() {
        newXPos = 0;
        
        arguments.forEach((arg) -> {
            Dimension d = arg.getPreferredSize();
            arg.setBounds(newXPos, 0, d.width, d.height);

            newXPos += d.width + gap;
        });
        
        attacks.forEach((attck) -> {
            attck.setBounds(attck.getProperX(), attck.getProperY(), attck.getProperWidth(), attck.getProperHeight());
        });
        
        Dimension myD = this.getSize();
        Dimension emptyMD = emptyMessage.getPreferredSize();
        emptyMessage.setBounds((myD.width - emptyMD.width)/2, (myD.height - emptyMD.height)/2, emptyMD.width, emptyMD.height);
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2D = (Graphics2D) g;
        AffineTransform at = g2D.getTransform();
        
        g2D.translate(5, 5);
        g2D.scale(scaling, scaling);
        super.paint(g);
        
        g2D.setTransform(at);
    }

    /**
     * Returns the minimum size for properly rendering the ArgumentationFramework.
     * Preferred size = Minimum size
     * @return the minimun size of the ArgumentationFramework
     */
    @Override
    public Dimension getPreferredSize() {
        return getMinimumSize();
    }

    /**
     * Returns the minimum size for properly rendering the ArgumentationFramework.
     * @return the minimun size of the ArgumentationFramework
     */
    @Override
    public Dimension getMinimumSize() {
        Dimension dim1 = super.getMinimumSize();
        Dimension dim2 = emptyMessage.getPreferredSize();
        
        dim1.setSize(Math.max(dim1.width, dim2.width), Math.max(dim1.height, dim2.height));

        int w = 0, h = 0, count = 0;

        for (Argument a : arguments) {
            Dimension pref = a.getPreferredSize();
            w += pref.width;
            h = Math.max(h, pref.height);

            count++;
        }

        return new Dimension((int)(Math.max(w + ((count - 1) * gap), dim1.width) * scaling) + 10, (int)(Math.max(h, dim1.height) * scaling) + 10);
    }
    
    public Point getAFPositionOnFrame(Container target){
        Point p = (Point) getLocation().clone();
        
        Container parent = getParent();
        while(parent != null && parent != target){
            p.x += parent.getX();
            p.y += parent.getY();
            parent = parent.getParent();
        }
        
        return p;
    }

    /**
     * Returns true if the component is considered focused.
     * False otherwise.
     * @param comp targeted compoenent
     * @return if the target component is considered focused
     */
    protected boolean isFocus(Component comp) {
        if (focused != null) {
//            return true;
//        }
        
            if (comp instanceof Argument) {
                if (focused instanceof Argument) {
                    return ((Argument) focused).containsArgument((Argument) comp);
                } else if (focused instanceof Attack) {
                    return ((Attack) focused).isAbout((Argument) comp);
                }
            }

            return comp == focused;
        }else if(focusedType != null){
            if (comp instanceof Argument) {
                return ((Argument) comp).getType() == focusedType;
            }
            return false;
        }
        
        return true;
    }

    /**
     * Moves the focused components to to front of the painel.
     */
    private void moveFocusToFront() {
        if (focused == null) {
            return;
        }

        if (focused instanceof Argument) {
            arguments.stream().filter((a) -> (a.containsArgument((Argument) focused))).forEachOrdered((a) -> {
                this.setLayer(a, ARGUMENTS_ALT_LAYER);
            });
        } else if (focused instanceof Attack) {
            for (Attack at : attacks) {
                if (at == focused) {
                    this.setLayer(at, ATTACKS_ALT_LAYER);
                    this.setLayer(at.getArgument1(), ARGUMENTS_ALT_LAYER);
                    this.setLayer(at.getArgument2(), ARGUMENTS_ALT_LAYER);
                }
            }
        }
    }

    /**
     * Moves focused components to the default position.
     */
    private void moveFocusToBack() {
        if (focused == null) {
            return;
        }

        if (focused instanceof Argument) {
            arguments.stream().filter((a) -> (a.containsArgument((Argument) focused))).forEachOrdered((a) -> {
                this.setLayer(a, ARGUMENTS_DEFAULT_LAYER);
            });
        } else if (focused instanceof Attack) {
            for (Attack at : attacks) {
                if (at == focused) {
                    this.setLayer(at, ATTACKS_DEFAULT_LAYER);
                    this.setLayer(at.getArgument1(), ARGUMENTS_DEFAULT_LAYER);
                    this.setLayer(at.getArgument2(), ARGUMENTS_DEFAULT_LAYER);
                }
            }
        }
    }

    /**
     * Sets the current focused component. If null all components are considered focused.
     * @param comp the focused component
     */
    public void setFocus(Component comp) {
        if (comp instanceof Argument || comp instanceof Attack) {
            if (focused != null) {
                moveFocusToBack();
            }

            focused = comp;
            moveFocusToFront();
        } else {
            moveFocusToBack();
            focused = null;
        }

        attacks.forEach((a) -> {
            a.isTranslucent(!isFocus(a));
        });
        arguments.forEach((a) -> {
            a.isTranslucent(!isFocus(a));
        });

    }

    /**
     * Sets the current type focused. If null all components are considered focused.
     * @param type the focused type component
     */
    public void setTypeFocus(Integer type) {
        if(type != null){
            if(type < 0 || type > 3) return;
        }
        
        focusedType = type;
        attacks.forEach((a) -> {
            a.isTranslucent(!isFocus(a));
        });
        arguments.forEach((a) -> {
            a.isTranslucent(!isFocus(a));
        });

    }

    /**
     * Sets the line multiplier to the ArgumentionFramework and it's components.
     * @param sizeMultiplier line thickness multiplier. Must be >=1
     */
    public void setSizeMultiplier(double sizeMultiplier) {
        if (sizeMultiplier < 1) {
            sizeMultiplier = 1.0;
        }
        this.sizeMultiplier = sizeMultiplier;
        this.arguments.forEach((arg) -> {
            arg.setSizeMultiplier(this.sizeMultiplier);
        });
        revalidate();
        repaint();
    }

    /**
     * Notifies the components of the ArgumentationFramework that a color has changed.
     */
    public void fireForegroundUpdated() {
        arguments.forEach((arg0) -> {
            arg0.foregroundUpdated();
        });
    }

    /**
     * 
     * @return 
     */
    public Color getAcceptedNonfocusedArgumentColor() {
        return ACCEPTED_NONFOCUSED_ARGUMENT_COLOR;
    }

    /**
     * 
     * @param ACCEPTED_NONFOCUSED_ARGUMENT_COLOR
     * @return 
     */
    public ArgumentionFramework setAcceptedNonfocusedArgumentColor(Color ACCEPTED_NONFOCUSED_ARGUMENT_COLOR) {
        this.ACCEPTED_NONFOCUSED_ARGUMENT_COLOR = ACCEPTED_NONFOCUSED_ARGUMENT_COLOR;
        fireForegroundUpdated();
        return this;
    }

    /**
     * 
     * @return 
     */
    public Color getAcceptedFocusedArgumentColor() {
        return ACCEPTED_FOCUSED_ARGUMENT_COLOR;
    }

    /**
     * 
     * @param ACCEPTED_FOCUSED_ARGUMENT_COLOR
     * @return 
     */
    public ArgumentionFramework setAcceptedFocusedArgumentColor(Color ACCEPTED_FOCUSED_ARGUMENT_COLOR) {
        this.ACCEPTED_FOCUSED_ARGUMENT_COLOR = ACCEPTED_FOCUSED_ARGUMENT_COLOR;
        fireForegroundUpdated();
        return this;
    }

    /**
     * 
     * @return 
     */
    public Color getRejectedNonfocusedArgumentColor() {
        return REJECTED_NONFOCUSED_ARGUMENT_COLOR;
    }

    /**
     * 
     * @param REJECTED_NONFOCUSED_ARGUMENT_COLOR
     * @return 
     */
    public ArgumentionFramework setRejectedNonfocusedArgumentColor(Color REJECTED_NONFOCUSED_ARGUMENT_COLOR) {
        this.REJECTED_NONFOCUSED_ARGUMENT_COLOR = REJECTED_NONFOCUSED_ARGUMENT_COLOR;
        fireForegroundUpdated();
        return this;
    }

    /**
     * 
     * @return 
     */
    public Color getRejectedFocusedArgumentColor() {
        return REJECTED_FOCUSED_ARGUMENT_COLOR;
    }

    /**
     * 
     * @param REJECTED_FOCUSED_ARGUMENT_COLOR
     * @return 
     */
    public ArgumentionFramework setRejectedFocusedArgumentColor(Color REJECTED_FOCUSED_ARGUMENT_COLOR) {
        this.REJECTED_FOCUSED_ARGUMENT_COLOR = REJECTED_FOCUSED_ARGUMENT_COLOR;
        fireForegroundUpdated();
        return this;
    }

    /**
     * Get the current fadeOff value.
     * @return fadeOffValue
     */
    public Float getFadeoff() {
        return CLUSTER_FADEOFF;
    }

    /**
     * Sets the fedeOff of the components that are not focused.
     * Muste be in the range [0,1]
     * @param CLUSTER_FADEOFF fadeOff value
     */
    public void setFadeoff(float CLUSTER_FADEOFF) {
        this.CLUSTER_FADEOFF = CLUSTER_FADEOFF;
    }

    /**
     * Clears the argumentation framework.
     */
    public void clear() {
        this.attacks.forEach((attck) -> {this.remove(attck); attck.clear();});
        this.attacks.clear();
        this.arguments.forEach((arg) -> {this.remove(arg); arg.clear();});
        this.arguments.clear();
        focused = null;
        newXPos = 0;
        emptyMessage.setVisible(true);
    }
}
