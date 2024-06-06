/*
 * File added by Nathan MacLeod 2019
 */
package gameObject;


/**
 *
 * @author macle
 */
public interface Enemy {
    public double getX();
    public double getZ();
    public boolean getDead();
    public void setYRot(double d);
    public void setDead(boolean b);
}
