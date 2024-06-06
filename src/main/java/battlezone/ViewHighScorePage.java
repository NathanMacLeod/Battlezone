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
public class ViewHighScorePage extends MenuPage {
    
    public ViewHighScorePage(int[] screenDimensions) {
        super(screenDimensions);
    }
    
    public int getStateChange(int keyPressed) {
        switch(keyPressed) {
            case KeyEvent.VK_SPACE:
                return 0;
        }
        return -1;
    }
    
    public void draw(Graphics g){}
    
    private void writeCenteredText(String message, int size, int x, int y, Graphics g) {
        Font font = new Font("Futura", Font.PLAIN, size);
        int Y = (y);
        int X = (x) - g.getFontMetrics(font).stringWidth(message)/2;
        g.setFont(font);
        g.drawString(message, X, Y);
    }
    
    private String[] makeScoresDisplayable(String scores, Battlezone battlezone) {
        //makes the string into what the displayed version should be
        String[] ray = battlezone.getStringArrayScores(scores);
        for(int i = 0; i < ray.length; i++) {
            ray[i] = (i + 1) + ".  " + ray[i].substring(0, 3) + "   " + ray[i].substring(3);
        }
        return ray;
    }
    
    public void draw(Graphics g, Battlezone battlezone) {
        int textSize = 60;
        double pWidth = getScreenDimensions()[0];
        double pHeight = getScreenDimensions()[1];
        g.setColor(Color.green);
        writeCenteredText("High Scores:", textSize, (int)pWidth/2, (int)pHeight * 1/8, g);
        textSize = 40;
        try {
            String[] scores = makeScoresDisplayable(battlezone.getHighScores(), battlezone);
            for(int i = 0; i < scores.length; i++) {
                writeCenteredText(scores[i], textSize, (int)pWidth/2, (int)pHeight * (2 + i)/8, g);
            }
        }
        catch(Exception e) {
            writeCenteredText("Couldnt find scores", textSize, (int)pWidth/2, (int)pHeight * 2/8, g);
            writeCenteredText("Make sure the scores text file is in the asteroids package", textSize, (int)pWidth/2, (int)pHeight * 4/8, g);
        }
        writeCenteredText("Space to return to main menu", textSize, (int)pWidth/2, (int)pHeight * 7/8, g);
    }
    
}
