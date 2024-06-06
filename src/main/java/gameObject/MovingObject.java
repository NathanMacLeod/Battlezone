/*
 * File added by Nathan MacLeod 2019
 */
package gameObject;

import Geometry.Point;
import Geometry.Hitbox;
import Geometry.Object3D;
import java.util.ArrayList;
import battlezone.Battlezone;

/**
 *
 * @author macle
 */
public class MovingObject extends CollideableObject {
    private double velocity;
    private double[] direction;//xAngle, yAngle
    
    public MovingObject(Point[] points, int[][] lines, double[] position, Point[] bulletHitBox, Point[] collisionBox, double velocity, double[] direction) {
        super(points, lines, position, bulletHitBox, collisionBox);
        this.velocity = velocity;
        this.direction = direction;
    }
    
    public MovingObject(double[] position, Model m, double velocity, double[] direction) {
        super(position, m);
        this.velocity = velocity;
        this.direction = direction;
    }
    
    protected void move(double timePassed, Battlezone battlezone) {
        double magnitude = velocity * timePassed;
        
        double yComp = magnitude * Math.sin(direction[0]);
        double xzComp = magnitude * Math.cos(direction[0]);
        double zComp = xzComp * Math.sin(direction[1]);
        double xComp = xzComp * Math.cos(direction[1]);

        setX(getX() + xComp);
        setY(getY() + yComp);
        setZ(getZ() + zComp);
        
        
    }
    
    public void rotate(double rotateVals[], double time) {
        setXRot(getXRot() + (rotateVals[0] * time));
        setYRot(getYRot() + (rotateVals[1] * time));
        setZRot(getZRot() + (rotateVals[2] * time));
    }
    
    public void setDirectionToAngle() {
        direction = new double[] {getXRot(), getYRot() + (Math.PI/2)};
    }
    
    public double getVelocity() {
        return velocity;
    }
    
    public double[] getDirection() {
        return direction;
    }
    
    public void setVelocity(double d) {
        velocity = d;
    }
    
    public void setDirection(double[] d) {
        direction = d;
    }
    
    public void update(double time, Battlezone battlezone) {
        super.update(time, battlezone);
        move(time, battlezone);
    }
}
