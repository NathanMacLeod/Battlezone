/*
 * File added by Nathan MacLeod 2019
 */
package gameObject;
import java.awt.Graphics;
import java.awt.Color;
import battlezone.Battlezone;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import javax.imageio.ImageIO;
import java.awt.Font;
/**
 *
 * @author macle
 */
public class HUD implements Updatable {
    private double angle;
    private double rotationSpeed;
    private double reticleBlinkTime = 0.15;
    private double reticleBlinkCounter = 0;
    private boolean reticleOn = true;
    private BufferedImage crack;
    private BufferedImage life;
    private Blip radarBlip;
    
    int[] radarPosition;
    double radarSize;
    
    int[] reticlePosition;
    double reticleSize;
    
    
    public HUD(double rotationSpeed, int[] radarPosition, double radarSize, int[] reticlePosition, double reticleSize) {
        this.rotationSpeed = rotationSpeed;
        radarBlip = new Blip(0, 0, 255, 130);
        angle = Math.PI;
        this.radarSize = radarSize;
        this.reticlePosition = reticlePosition;
        this.reticleSize = reticleSize;
        this.radarPosition = radarPosition;
        
        try {
            crack = ImageIO.read(Battlezone.class.getClassLoader().getResourceAsStream("sprites/crack.png"));
            life = ImageIO.read(Battlezone.class.getClassLoader().getResourceAsStream("sprites/life.png"));
        }
        catch(Exception e) {
            e.printStackTrace(System.err);
        }
    }
    
    public void draw(Graphics g, Battlezone battlezone, int[] screenDimensions) {
        if(battlezone.getPlayer().getDead()) {
            drawCrack(g, screenDimensions);
        }
        else {
            drawRadar(g, radarSize, radarPosition);
            drawReticle(g, battlezone, reticleSize, reticlePosition);
        }
        
        drawScore(g, battlezone, screenDimensions[0]);
        drawLives(g, battlezone);
    }
    
    public void drawScore(Graphics g, Battlezone battlezone, int screenWidth) {
        int score = battlezone.getScore();
        
        String s = "";
        if(score < 100000000) 
            s = (s + (score + 1000000000)).substring(1);
        else
            s += score;
        
        s = "SCORE" + "      " + s;
        int fontSize = 30;
        Font font = new Font("Futura", Font.PLAIN, fontSize);
        g.setFont(font);
        g.setColor(Color.GREEN);
        int stringWidth = g.getFontMetrics(font).stringWidth(s);
        g.drawString(s, screenWidth - stringWidth - 20, 30);
    }
    
    public void drawLives(Graphics g, Battlezone battlezone) {
        int y = 30;
        int startX = 30;
        int imageWidth = 70;
        int imageHeight = 30;
        int spaceing = 20;
        
        for(int i = 0; i < battlezone.getLives(); i++) {
            int x = startX + (imageWidth + spaceing) * i;
            g.drawImage(life, x, y, imageWidth, imageHeight, null);
        } 
        
    }
    
    public void drawCrack(Graphics g, int[] screenDimensions) {
        int size = screenDimensions[0];
        g.drawImage(crack, 0, (screenDimensions[1]/2) - size/2, size, size, null);
    }
    
    private void drawRadar(Graphics g, double radius, int[] position) {
        g.setColor(Color.red);
        
        g.drawOval((int) (position[0] - radius), (int) (position[1] - radius), (int) (radius * 2), (int) (radius * 2));
        
        double lineProportion = 9.0/10;
        
        g.drawLine(position[0], position[1], (int) (position[0] + radius/2), (int) (position[1] - (1.7321/2 * radius)));
        g.drawLine(position[0], position[1], (int) (position[0] - radius/2), (int) (position[1] - (1.7321/2 * radius)));
        
        g.drawLine((int) (position[0] - radius), position[1], (int)(position[0] - (radius  *lineProportion)), position[1]);
        g.drawLine((int) (position[0] + radius), position[1], (int)(position[0] + (radius  *lineProportion)), position[1]);
        g.drawLine(position[0], (int) (position[1] - radius), position[0], (int) (position[1] - (radius * lineProportion)));
        g.drawLine(position[0], (int) (position[1] + radius), position[0], (int) (position[1] + (radius * lineProportion)));
        
        g.drawLine(position[0], position[1], (int)(Math.cos(angle) * radius) + position[0], (int)-(Math.sin(angle) * radius) + position[1]);
        radarBlip.draw(g, radius, position);
    }
    
    private void drawReticle(Graphics g, Battlezone battlezone, double size, int[] position) {
        if(!reticleOn)
            return;
        
        g.setColor(Color.green);
        double spacing = size/1.8;
        
        g.drawLine((int) (position[0] - size/2), (int) (position[1] - spacing), (int) (position[0] + size/2), (int) (position[1] - spacing));
        g.drawLine((int) (position[0]), (int) (position[1] - spacing), (int) (position[0]), (int) (position[1] - spacing - size));
        g.drawLine((int) (position[0] - size/2), (int) (position[1] + spacing), (int) (position[0] + size/2), (int) (position[1] + spacing));
        g.drawLine((int) (position[0]), (int) (position[1] + spacing), (int) (position[0]), (int) (position[1] + spacing + size));
        
        boolean fireReticle = false;
        double curlSize = size * 1.0/10;
        
        if(battlezone.getEnemy() != null && !battlezone.getEnemy().getDead()) {
            double fireReticleArc = Math.PI/64;

            double angleToTank = getAngleOfPlayerToEnemyTank(battlezone);

            double counterDist = ((battlezone.getPlayer().getYRot() + (Math.PI/2)) % (Math.PI * 2)) - angleToTank;
            if(counterDist < 0) 
                counterDist += Math.PI * 2;
            double clockwiseDist = (Math.PI * 2) - counterDist;

            
            fireReticle = counterDist < fireReticleArc || clockwiseDist < fireReticleArc;
        }
        if(fireReticle) {
            curlSize *= 2;
            
            
            g.drawLine((int) (position[0] - size/2), (int) (position[1] - spacing), (int) (position[0] - size/2 + curlSize), (int) (position[1] - spacing + curlSize));
            g.drawLine((int) (position[0] + size/2), (int) (position[1] - spacing), (int) (position[0] + size/2 - curlSize), (int) (position[1] - spacing + curlSize));
            
            g.drawLine((int) (position[0] - size/2), (int) (position[1] + spacing), (int) (position[0] - size/2 + curlSize), (int) (position[1] + spacing - curlSize));
            g.drawLine((int) (position[0] + size/2), (int) (position[1] + spacing), (int) (position[0] + size/2 - curlSize), (int) (position[1] + spacing - curlSize));
            
        }
        else {
            
            g.drawLine((int) (position[0] - size/2), (int) (position[1] - spacing), (int) (position[0] - size/2), (int) (position[1] - spacing + curlSize));
            g.drawLine((int) (position[0] + size/2), (int) (position[1] - spacing), (int) (position[0] + size/2), (int) (position[1] - spacing + curlSize));
            
            g.drawLine((int) (position[0] - size/2), (int) (position[1] + spacing), (int) (position[0] - size/2), (int) (position[1] + spacing - curlSize));
            g.drawLine((int) (position[0] + size/2), (int) (position[1] + spacing), (int) (position[0] + size/2), (int) (position[1] + spacing - curlSize));
        }
    }
    
    private double getAngleOfPlayerToEnemyTank(Battlezone battlezone) {
        PlayerTank player = battlezone.getPlayer();
        Enemy enemy = battlezone.getEnemy();
        double relativeX = enemy.getX() - player.getX();
        double relativeZ = (enemy.getZ() - player.getZ());
        double angleToTank = Math.atan(relativeZ/relativeX);
        if(relativeX < 0 && relativeZ >= 0) {
            angleToTank += Math.PI;
        }
        if(relativeX > 0 && relativeZ <= 0) {
            angleToTank += Math.PI * 2;
        }
        if(relativeX <= 0 && relativeZ <= 0) {
            angleToTank += Math.PI;
        }
        return angleToTank;
    }
    
    public void update(double timePassed, Battlezone battlezone) {
        if(battlezone.getPlayer().getDead())
            return;
        
        rotateRadar(battlezone, timePassed);
        radarBlip.fade(timePassed);
        if(!battlezone.getPlayer().getOnFireCooldown()) {
            reticleBlinkCounter = 0;
            reticleOn = true;
        }
        else {
            reticleBlinkCounter -= timePassed;
            if(reticleBlinkCounter <= 0) {
                reticleOn = !reticleOn;
                reticleBlinkCounter = reticleBlinkTime;
            }
        }
    }
    
    private void rotateRadar(Battlezone battlezone, double timePassed) {
        double arcSize = timePassed * rotationSpeed;
        PlayerTank player = battlezone.getPlayer();
        Enemy enemy = battlezone.getEnemy();
        if(enemy == null || enemy.getDead()) {
            angle -= arcSize;
            if(angle < 0)
                angle += Math.PI * 2;
            return;
        }
        double relativeX = enemy.getX() - player.getX();
        double relativeZ = (enemy.getZ() - player.getZ());
        double angleToTank = getAngleOfPlayerToEnemyTank(battlezone);
        
        double mapRadius = battlezone.getMapRadius();
        double currentAngle = angle;
        double angleAfter = angle - (arcSize * 1.5);
        angleToTank -= player.getYRot();
        
        if(angleToTank > Math.PI * 2) 
            angleToTank -= Math.PI * 2;
        if(angleToTank < 0) 
            angleToTank += Math.PI * 2;
        
        if(angleToTank >= angleAfter && angleToTank  < currentAngle) {
            radarBlip.setAlpha(255);
            double xPos = relativeX/mapRadius;
            double yPos = relativeZ/mapRadius;
            radarBlip.setPos((Math.cos(-player.getYRot()) * xPos) - (Math.sin(-player.getYRot()) * yPos), (Math.sin(-player.getYRot()) * xPos) + (Math.cos(-player.getYRot()) * yPos));
        }
        angle -= arcSize;
        if(angle < 0)
            angle += Math.PI * 2;
    }
    
    private static class Blip {
        double x;
        double y;
        double alphaValue;
        double porportionalRadius = 1.0/25;
        double fadeSpeed;
        
        public Blip(double x, double y, double alpha, double fade) {
            this.x = x;
            this.y = y;
            alphaValue = alpha;
            fadeSpeed = fade;
        }
        
        public void setAlpha(double a) {
            alphaValue = a;
        }
        
        public void setPos(double x, double y) {
            this.x = x;
            this.y = y;
        }   
        
        public void draw(Graphics g, double radius, int[] position) {
            if(alphaValue <= 0)
                return;
            g.setColor(new Color(255, 0, 0, (int) alphaValue));
            g.fillOval((int) ((x - porportionalRadius) * radius) + position[0], (int) -((y + porportionalRadius) * radius) + position[1], (int) (porportionalRadius  * 2 * radius), (int)(porportionalRadius  * radius * 2));
        }
        
        public void fade(double time) {
            alphaValue -= time * fadeSpeed;
        }
    }
    
}
