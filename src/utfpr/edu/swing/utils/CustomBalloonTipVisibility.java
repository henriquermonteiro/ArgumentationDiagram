/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utfpr.edu.swing.utils;

import javax.swing.JButton;
import javax.swing.JComponent;
import net.java.balloontip.BalloonTip;
import net.java.balloontip.positioners.BalloonTipPositioner;
import net.java.balloontip.styles.BalloonTipStyle;

/**
 *
 * @author henri
 */
public class CustomBalloonTipVisibility extends BalloonTip{

    public CustomBalloonTipVisibility(JComponent attachedComponent, String text) {
        super(attachedComponent, text);
    }

    public CustomBalloonTipVisibility(JComponent attachedComponent, String text, BalloonTipStyle style, boolean useCloseButton) {
        super(attachedComponent, text, style, useCloseButton);
    }

    public CustomBalloonTipVisibility(JComponent attachedComponent, JComponent contents, BalloonTipStyle style, boolean useCloseButton) {
        super(attachedComponent, contents, style, useCloseButton);
    }

    public CustomBalloonTipVisibility(JComponent attachedComponent, JComponent contents, BalloonTipStyle style, Orientation orientation, AttachLocation attachLocation, int horizontalOffset, int verticalOffset, boolean useCloseButton) {
        super(attachedComponent, contents, style, orientation, attachLocation, horizontalOffset, verticalOffset, useCloseButton);
    }

    public CustomBalloonTipVisibility(JComponent attachedComponent, JComponent contents, BalloonTipStyle style, BalloonTipPositioner positioner, JButton closeButton) {
        super(attachedComponent, contents, style, positioner, closeButton);
    }

    public CustomBalloonTipVisibility() {
    }
    
    public void forceVisibility(boolean visible){
        forceSetVisible(visible);
    }
}
