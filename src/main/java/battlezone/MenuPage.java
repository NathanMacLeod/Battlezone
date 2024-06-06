/*
 * File added by Nathan MacLeod 2019
 */
package battlezone;

import java.awt.Graphics;
/**
 *
 * @author macle
 */
public abstract class MenuPage {
    //private BufferedImage image;
    private int[] screenDimensions;
    
    public MenuPage(int[] screenDimensions) {
        this.screenDimensions = screenDimensions;
    }
    
    public int[] getScreenDimensions() {
        return screenDimensions;
    }
    
    public abstract int getStateChange(int keyPress);
    
    public abstract void draw(Graphics g);
    
}
