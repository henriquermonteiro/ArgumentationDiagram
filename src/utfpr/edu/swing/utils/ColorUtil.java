package utfpr.edu.swing.utils;

import java.awt.Color;

/**
 * Utility class for blending colors.
 * Reference code: https://stackoverflow.com/a/2355637 thanks to David
 * @author Henrique M R Jasinski.
 */
public class ColorUtil {
    /**
     * Return a color that is the blend of two colors.
     * return color = (color1 * Amount) + (color2 * (1 - Amount))
     * 
     * @param clOne first color
     * @param clTwo second color
     * @param fAmount the ratio of the first color to be used
     * @return the resulting color of the blend operation
     */
    public static Color blend(Color clOne, Color clTwo, float fAmount) {
        if(fAmount > 1 || fAmount < 0) return clOne;
        if(clTwo == null) return clOne;
        
        float fInverse = 1.0f - fAmount;

        float afOne[] = new float[3];
        clOne.getColorComponents(afOne);
        float afTwo[] = new float[3]; 
        clTwo.getColorComponents(afTwo);    

        float afResult[] = new float[3];
        afResult[0] = afOne[0] * fAmount + afTwo[0] * fInverse;
        afResult[1] = afOne[1] * fAmount + afTwo[1] * fInverse;
        afResult[2] = afOne[2] * fAmount + afTwo[2] * fInverse;

        return new Color (afResult[0], afResult[1], afResult[2]);
    }
}
