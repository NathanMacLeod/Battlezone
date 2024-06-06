/*
 * File added by Nathan MacLeod 2019
 */
package battlezone;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.KeyEvent;
/**
 *
 * @author macle
 */
public class WriteHighScorePage extends MenuPage {
    int[] nameChars = new int[] {65, 65, 65};
    int charIndex;
    boolean letterFlicker = false;
    double flickerTime = 0.45;
    double flickerCounter = flickerTime;
    
    public WriteHighScorePage(int[] screenDimensions) {
        super(screenDimensions);
    }
    
    public int getStateChange(int keyPressed) {
        switch(keyPressed) {
            case KeyEvent.VK_SPACE:
            case KeyEvent.VK_ENTER:
                return 0;
        }
        return -1;
    }
    
    public void enterHighScore(int keyPressed, Battlezone battlezone) {
        switch(keyPressed) {
            case KeyEvent.VK_LEFT:
                charIndex--;
                if(charIndex < 0) {
                    charIndex = 0;
                }
                break;
            case KeyEvent.VK_RIGHT:
                charIndex++;
                if(charIndex > 2) {
                    charIndex = 2;
                }
                break;
            case KeyEvent.VK_DOWN:
                nameChars[charIndex]++;
                if(nameChars[charIndex] > 90) {
                    nameChars[charIndex] = 65;
                }
                break;
            case KeyEvent.VK_UP:
                nameChars[charIndex]--;
                if(nameChars[charIndex] < 65) {
                    nameChars[charIndex] = 90;
                }
                break;
            case KeyEvent.VK_ENTER:
                battlezone.saveScore();
        }
    }
    
    private void writeCenteredText(String message, int size, int x, int y, Graphics g, boolean typeWriter) {
        Font font;
        if(typeWriter)
            font = new Font(Font.MONOSPACED, Font.PLAIN, size);
        else 
            font = new Font("Futura", Font.PLAIN, size);
        int Y = (y);
        int X = (x) - g.getFontMetrics(font).stringWidth(message)/2;
        g.setFont(font);
        g.drawString(message, X, Y);
    }
    
    public void update(double timePassed) {
        flickerCounter -= timePassed;
        if(flickerCounter <= 0) {
            letterFlicker = !letterFlicker;
            flickerCounter = flickerTime;
        }
    }
    
    public void draw(Graphics g){}
    
    public void draw(Graphics g, int score) {
        int textSize = 30;
        double pWidth = getScreenDimensions()[0];
        double pHeight = getScreenDimensions()[1];
        g.setColor(Color.green);
        writeCenteredText("Congrats, your score of " + score + " is high enough to be on the top 5", textSize, (int)pWidth/2, (int)pHeight * 1/8, g, false);
        textSize = 20;
        writeCenteredText("Use arrow keys to enter the name for the score to be saved with", textSize, (int)pWidth/2, (int)pHeight * 2/8, g, false);
        writeCenteredText("Hit enter to save, or space to not save", textSize, (int)pWidth/2, (int)pHeight * 3/16, g, false);
        textSize = 160;
        char[] chars = new char[3];
        for(int i = 0; i < 3; i++) {
            char c;
            if(letterFlicker && charIndex == i) {
                c = ' ';
            }
            else {
                c = (char) nameChars[i];
            }
            chars[i] = c;
        }
        writeCenteredText(chars[0] + "  " + chars[1] + "  " + chars[2], textSize, (int)pWidth/2, (int)(pHeight * 5/8) - 10, g, true);
        writeCenteredText("_  _  _", textSize, (int)pWidth/2, (int)(pHeight) * 5/8, g, true);
    }
    
    public int[] getNameChars() {
        return nameChars;
    }
    
}
