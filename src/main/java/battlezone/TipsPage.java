/*
 * File added by Nathan MacLeod 2019
 */
package battlezone;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 *
 * @author macle
 */
public class TipsPage extends ImagePage {
    public TipsPage(int[] screenDimensions) {
        super(screenDimensions);
    }
    
    public int getStateChange(int keyPressed) {
        switch(keyPressed) {
            case KeyEvent.VK_SPACE:
                return 0;
            case KeyEvent.VK_S:
                return 3;
        }
        return -1;
    } 
    
    protected BufferedImage setImage() {
        try {
            return ImageIO.read(Battlezone.class.getClassLoader().getResourceAsStream("sprites/Tips.png"));
        }
        catch(Exception e) {
            e.printStackTrace(System.err);
            return null;
        }
    }
}
