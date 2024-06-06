/*
*File added by Nathan MacLeod 2019
*/

package gameObject;

import battlezone.Battlezone;


public class FreefallingDebris
  extends MovingObject
{
  private double xVel;
  private double yVel;
  private double zVel;
  private double xAngVel;
  private double yAngVel;
  private double zAngVel;
  private static final double GRAVITY = 30.0D;
  
  public static void explode(Battlezone battlezone, double[] position)
  {
    double maxVelocity = 40.0D;
    double maxAngVelocity = 10.0D;
    for (int i = 0; i < 6; i++) {
      double[] pos = { position[0], position[1], position[2], 0.0D, 0.0D, 0.0D };
      double[] velocity = new double[6];
      pos[3] = (Math.random() * 2.0D * 3.141592653589793D);
      pos[4] = (Math.random() * 2.0D * 3.141592653589793D);
      pos[5] = (Math.random() * 2.0D * 3.141592653589793D);
      velocity[0] = (Math.random() * maxVelocity * 2.0D - maxVelocity);
      velocity[1] = (-Math.abs(Math.random() * maxVelocity));
      velocity[2] = (Math.random() * maxVelocity * 2.0D - maxVelocity);
      velocity[3] = (Math.random() * maxAngVelocity * 2.0D - maxAngVelocity);
      velocity[4] = (Math.random() * maxAngVelocity * 2.0D - maxAngVelocity);
      velocity[5] = (Math.random() * maxAngVelocity * 2.0D - maxAngVelocity);
      FreefallingDebris p = new FreefallingDebris(position, velocity, ModelManager.getRandomDebrisModel(1.0D));
      battlezone.addUpdatable(p);
    }
  }
  

  public FreefallingDebris(double[] position, double[] velocities, Model m)
  {
    super(position, m, 0.0D, null);
    xVel = velocities[0];
    yVel = velocities[1];
    zVel = velocities[2];
    xAngVel = velocities[3];
    yAngVel = velocities[4];
    zAngVel = velocities[5];
  }
  
  public void move(double timePassed, Battlezone battlezone)
  {
    setX(getX() + xVel * timePassed);
    setY(getY() + yVel * timePassed);
    setZ(getZ() + zVel * timePassed);
    setXRot(getXRot() + xAngVel * timePassed);
    setYRot(getYRot() + yAngVel * timePassed);
    setZRot(getZRot() + zAngVel * timePassed);
    yVel += 30.0D * timePassed;
    
    if (getY() > 0.0D) {
      battlezone.removeUpdatable(this);
    }
  }
}
