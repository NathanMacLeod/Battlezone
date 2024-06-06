/*
 * File added by Nathan MacLeod 2019
 */
package gameObject;
import Geometry.*;
import java.util.ArrayList;
import battlezone.Battlezone;
/**
 *
 * @author macle
 */
public class TankShell extends MovingObject {
    Point[] dragPoints;//used to pull the hitbox out to the distance the shell travels each time.
    //this avoids the shell simply passing through something if it goes to fast
    private boolean friendly;
    
    public TankShell(double[] position, Model m, boolean friendly) {
        super(position, m, 180, new double[] {position[3], position[4] + Math.PI/2});
        dragPoints = new Point[2];
        dragPoints[0] = m.collisionBox.getModelPoints()[0];
        dragPoints[1] = m.collisionBox.getModelPoints()[1];
        this.friendly = friendly;
    }
    
    public void move(double timePassed, Battlezone battlezone) {
        ArrayList<Obstacle> obstacles = battlezone.getObstacles();
        super.move(timePassed, battlezone);
        dragPoints[0].set(new double[] {dragPoints[0].getX(), dragPoints[0].getY(), -getVelocity() * timePassed});
        dragPoints[1].set(new double[] {dragPoints[1].getX(), dragPoints[1].getY(), -getVelocity() * timePassed});
        
        boolean collision = false;
        for(Obstacle ob : obstacles) {
            if(ob.bulletBoxCollision(getCollisionBox())) {
                collision = true;
                break;
            }
        }
        CollideableObject t;
        if(friendly)
            t = (CollideableObject) battlezone.getEnemy();
        else
            t = (CollideableObject) battlezone.getPlayer();
                    
        if(t != null && t.bulletBoxCollision(getCollisionBox())) {
            FreefallingDebris.explode(battlezone, getPosition());
            if(friendly)
                ((Enemy)t).setDead(true);
            else
                ((PlayerTank)t).setDead(true);
            collision = true;
            battlezone.removeUpdatable((Updatable) t);
        }
        
        if(collision) {
            PointDebris.explode(battlezone, getPosition());
            battlezone.removeUpdatable((Updatable) this);
        }
    }
    
}
