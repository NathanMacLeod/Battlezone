/*
 * File added by Nathan MacLeod 2019
 */
package gameObject;

import java.util.ArrayList;
import java.awt.Graphics;
import java.awt.Color;
import battlezone.Battlezone;
/**
 *
 * @author macle
 */
public class BackgroundImage implements Updatable {
    private int width;
    private ArrayList<BackgroundEntity> entities;
    private ArrayList<UpdatingBackgroundEntity> updating;
    private int[] currentScale;
    private int[] screenDimensions;
    
    public BackgroundImage(int[] screenDimensions) {
        this.screenDimensions = screenDimensions;
        currentScale = new int[] {1600, 900};
        addEntities();
        width = 5000;
        double[] porportions = new double[] {(double)screenDimensions[0]/currentScale[0], (double)screenDimensions[1]/currentScale[1]};
        scaleValues(porportions);
    }
    
    private void scaleValues(double[] porportions) {
        width *= porportions[0];
        for(BackgroundEntity e : entities)
            e.scale(porportions);
    }
    
    private void addEntities() {
        entities = new ArrayList<BackgroundEntity>();
        updating = new ArrayList<UpdatingBackgroundEntity>();
        int[][] linePoints = new int[][] {
            {700, 0, 1000, -100}, {1000, -100, 1500, 0}, {1000, -100, 1200, 0},
            {1300, -40, 1800, -150}, {1800, -150, 2200, 0}, {2100, -37, 2300, -300},
            {2300, -300, 2320, -270}, {2320, -270, 2350, -290}, {2350, -290, 2360, -260}, {2360, -260, 2380, -305},
            {2380, -305, 2500, 0},
            {2470, -76, 2700, -150}, {2700, -150, 3200, 0}, {2700, -150, 2900, 0},
            {3500, 0, 3800, -140}, {3800, -140, 3950, 0}, {3800, -140, 4500, 0}
        
        };
        Volcano v = new Volcano(new int[] {2350, -290}, 1.5, 1000, 700, -500, updating, entities);
        updating.add(v);
        entities.add(v);
        createLines(linePoints);
    }
    
    private void createLines(int[][] linePoints) {
        for(int i = 0; i < linePoints.length; i++) {
            int[] p1 = new int[] {linePoints[i][0], linePoints[i][1]};
            int[] p2 = new int[] {linePoints[i][2], linePoints[i][3]};
            entities.add(new BackgroundLine(p1, p2));
        }
    }
    
    public void update(double timePassed, Battlezone battlezone) {
        for(int i = 0; i < updating.size(); i++) {
            UpdatingBackgroundEntity e = updating.get(i);
            e.update(timePassed);
            if(e instanceof VolcanoParticle && ((VolcanoParticle) e).getDead()) {
                entities.remove(e);
                updating.remove(i);
                i--;
            }
        }
    }
    
    public void drawBackground(Graphics g, double angle) {
        int position = (int)(width * ((Math.PI * 2) - (angle % (Math.PI * 2)))/(Math.PI * 2));
        double rightBound = position + screenDimensions[0];
        g.setColor(Color.green);
        g.drawLine(0, screenDimensions[1]/2, screenDimensions[0], screenDimensions[1]/2);
        for(BackgroundEntity e : entities) {
            if(e.withinBounds(position, rightBound)) {
                e.draw(g, -position, screenDimensions[1]/2);
            }
        }
        
        if(position + screenDimensions[0] > width) {
            for(BackgroundEntity e : entities) {
                if(e.withinBounds(0, position + screenDimensions[0] - width)) {
                    e.draw(g, width - position, screenDimensions[1]/2);
                }
            }
        }
    }
    
    private interface BackgroundEntity {
        public boolean withinBounds(double lowBound, double upperBound);
        public void scale(double[] porportions);
        public void draw(Graphics g, int translation, int vertShift);
    }
    
    private interface UpdatingBackgroundEntity {
        public void update(double timePassed);
    }
    
    private static class VolcanoParticle implements BackgroundEntity, UpdatingBackgroundEntity {
        private int[] pos;
        private double xVelocity;
        private double yVelocity;
        private double gravity;
        private double lifeTime;
        private double lifeLeft;
        
        public VolcanoParticle(int[] pos, double xVel, double yVel, double grav, double life) {
            this.pos = pos;
            xVelocity = xVel;
            yVelocity = yVel;
            gravity = grav;
            lifeTime = life;
            lifeLeft = life;
        }
        
        public boolean getDead() {
            return lifeLeft <= 0 || pos[1] > 0;
        }
        
        public void update(double timePassed) {
            lifeLeft -= timePassed;
            yVelocity += (gravity * timePassed);
            pos[0] += xVelocity * timePassed;
            pos[1] += yVelocity * timePassed;
        }
        
        public boolean withinBounds(double lowBound, double upperBound) {
            return pos[0] >= lowBound && pos[0] <= upperBound;
        }
        
        public void scale(double[] porportions) {
            pos[0] *= porportions[0];
            pos[1] *= porportions[1];
        }
        
        public void draw(Graphics g, int translation, int vertShift) {
            g.setColor(new Color(0, 255, 0, (int) (255 * lifeLeft/lifeTime)));
            g.fillOval(pos[0] + translation - 3, pos[1] + vertShift - 3, 6, 6);
        }
        
    }
    
    private static class Volcano implements BackgroundEntity, UpdatingBackgroundEntity {
        private int[] pos;
        private double maxTimeForParticle;
        private double timeToNextParticle;
        private double gravity;
        private double maxXVelocity;
        private double maxYVelocity;
        private double maxParticleLife;
        private ArrayList<UpdatingBackgroundEntity> updateList;
        private ArrayList<BackgroundEntity> drawList;
        
        public Volcano(int[] pos, double time, double gravity, double xVel, double yVel, ArrayList<UpdatingBackgroundEntity> updateList, ArrayList<BackgroundEntity> drawList) {
            this.pos = pos;
            maxTimeForParticle = time;
            timeToNextParticle = 0;
            this.gravity = gravity;
            maxXVelocity = xVel;
            maxYVelocity = yVel;
            maxParticleLife = Math.sqrt(-pos[1] * 2/gravity) * 3;
            this.updateList = updateList;
            this.drawList = drawList;
        }
        
        public void update(double timePassed) {
            timeToNextParticle -= timePassed;
            if(timeToNextParticle <= 0) {
                timeToNextParticle = maxTimeForParticle * Math.random();
                double xVel = (maxXVelocity * 2 * Math.random()) - maxXVelocity;
                double yVel = Math.random() * maxYVelocity;
                double life = maxParticleLife * Math.random();
                int[] position = new int[] {pos[0], pos[1]};
                VolcanoParticle p = new VolcanoParticle(position, xVel, yVel, gravity, life);
                updateList.add(p);
                drawList.add(p);
            }
        }
        
        public boolean withinBounds(double lowBound, double upperBound) {
            return false;
        }
        
        public void scale(double[] porportions) {
            pos[0] *= porportions[0];
            pos[1] *= porportions[1];
            gravity *= porportions[1];
            maxXVelocity *= porportions[0];
            maxYVelocity *= porportions[1];
        }
        
        public void draw(Graphics g, int translation, int vertShift) {
            
        }
        
    }
    
    private static class BackgroundLine implements BackgroundEntity{
        private int[] p1;
        private int[] p2;
        
        public BackgroundLine(int[] p1, int[] p2) {
            this.p1 = p1;
            this.p2 = p2;
        }
        
        public boolean withinBounds(double lowBound, double upperBound) {
            if((p1[0] >= lowBound && p1[0] <= upperBound) || (p2[0] >= lowBound && p2[0] <= upperBound))
                return true;
            return false;
        }
        
        public void scale(double[] porportions) {
            p1[0] *= porportions[0];
            p2[0] *= porportions[0];
            p1[1] *= porportions[1];
            p2[1] *= porportions[1];
        }
        
        public void draw(Graphics g, int translation, int vertShift) {
            g.setColor(Color.GREEN);
            g.drawLine(p1[0] + translation, vertShift + p1[1], p2[0] + translation, vertShift + p2[1]);
        }
        
    }
}
