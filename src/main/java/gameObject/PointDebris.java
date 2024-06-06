/*
 * File added by Nathan MacLeod 2019
 */
package gameObject;
import Matrix.Matrix;
import battlezone.Battlezone;
import java.awt.Graphics;
import Geometry.Point;
/**
 *
 * @author macle
 */
public class PointDebris extends MovingObject { 
    private double lifeTimeLeft;
    
    public static void explode(Battlezone battlezone, double[] position) {
        double maxVelocity = 20;
        double life = 0.5;
        for(int i = 0; i < 25; i++) {
            double[] pos = new double[] {position[0], position[1], position[2], 0, 0, 0};
            pos[3] = Math.random() * 2 * Math.PI;
            pos[4] = Math.random() * 2 * Math.PI;
            PointDebris p = new PointDebris(pos, Math.random() * maxVelocity, life);
            battlezone.addUpdatable(p);
        }
    }
    
    public PointDebris(double[] position, double velocity, double time) {
        super(new Point[] {new Point(0, 0, 0)}, null, position, null, null, velocity, new double[] {position[3], position[4]});
        lifeTimeLeft = time;
    }
    
    public void draw(Graphics g, Matrix transformationMatrix, double fov, int[] screenDimensions) {
        Point[] copyOfPoints = getCopyOfPoints();
        transformPointsToWorldPos(copyOfPoints);
        transformPoints(transformationMatrix, copyOfPoints);
        drawPoint(copyOfPoints[0], g, 2, fov, screenDimensions);
    }
    
    public void update(double timePassed, Battlezone battlezone) {
        super.update(timePassed, battlezone);
        lifeTimeLeft -= timePassed;
        if(lifeTimeLeft <= 0) {
            battlezone.removeUpdatable(this);
        }
    }
    
}
