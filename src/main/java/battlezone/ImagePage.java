/*
 * File added by Nathan MacLeod 2019
 */
package battlezone;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 *
 * @author macle
 */
public  abstract class ImagePage extends MenuPage {
    private BufferedImage image;
    
    public ImagePage(int[] screenDimensions) {
        super(screenDimensions);
        image = setImage();
    }
    
    protected abstract BufferedImage setImage();
    
    public abstract int getStateChange(int keyPress);
    
    public void draw(Graphics g) {
        int[] screenDimensions = getScreenDimensions();
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        boolean heightRestricted = false;
        int unrestrictedImageDimensionLength;
        if((double) imageHeight/imageWidth > (double) screenDimensions[1]/screenDimensions[0]) {
            heightRestricted = true;
            unrestrictedImageDimensionLength = ((imageWidth * screenDimensions[1])/imageHeight);
        }
        else
            unrestrictedImageDimensionLength = ((imageHeight * screenDimensions[0])/imageWidth);
        
        if(heightRestricted)
            g.drawImage(image, (screenDimensions[0] - unrestrictedImageDimensionLength)/2, 0, unrestrictedImageDimensionLength, screenDimensions[1], null);
        else
            g.drawImage(image, 0, (screenDimensions[1] - unrestrictedImageDimensionLength)/2, screenDimensions[0], unrestrictedImageDimensionLength, null);
    }
    
}
