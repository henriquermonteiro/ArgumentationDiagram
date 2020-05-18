package utfpr.edu.test;

import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.ToolTipManager;
import utfpr.edu.argumentation.diagram.Argument;
import utfpr.edu.argumentation.diagram.ArgumentionFramework;
import utfpr.edu.argumentation.diagram.Atom;

/**
 * Test class for rendering a test diagram.
 * 
 * @author Henrique M R Jasinski
 */
public class DiagramTest {
    public static JButton bu1;
    public static JButton bu2;

    /**
     * Shows an example diagram in a new frame.
     * 
     * @param args not used.
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ToolTipManager.sharedInstance().setDismissDelay(60000);

        ArgumentionFramework cluster = new ArgumentionFramework();
        
        Atom a = new Atom("B1", "openFracture(man_32)", false).setMyFramewrok(cluster);
        Atom b = new Atom("B2", "hasFractBone(man_32)", true).setMyFramewrok(cluster);
        Atom c = new Atom("B3", "injuredSevere(man_32)", true).setMyFramewrok(cluster);
        Atom d = new Atom("B4", "!injuredSevere(man_32)", true).setMyFramewrok(cluster);
        Atom e = new Atom("B5", "fractBoneIs(man_32,arm)", true).setMyFramewrok(cluster);
        

        Argument arg2 = new Argument(a, "Aep2", "r_1^2", true, new Argument(c, "Ast1", "r_1^2")).setMyCluster(cluster).setType(Atom.REJECTED_NONFOCUSED_ARGUMENT_TYPE);
        Argument arg1 = new Argument(b, "Aep2", "r1_4^2", false, new Argument(d, "Ast2", "r_1^2")).setMyCluster(cluster).setType(Atom.ACCEPTED_NONFOCUSED_ARGUMENT_TYPE);

        Argument arg3 = new Argument(e, "A", "r1_4^2").setMyCluster(cluster).setType(Argument.REJECTED_FOCUSED_ARGUMENT_TYPE);

        cluster.addArgument(arg1);
        cluster.addArgument(arg2);
        cluster.addArgument(arg3);
        
        cluster.setSizeMultiplier(2.0);

        frame.setLayout(new FlowLayout());
        frame.getContentPane().add(cluster);
        

        frame.setVisible(true);
        frame.setSize(600, 400);
        
        System.out.println("ready");
    }

}
