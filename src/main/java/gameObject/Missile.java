/*
 * File added by Nathan MacLeod 2019
 */
package gameObject;


import java.util.ArrayList;
import battlezone.Battlezone;
/**
 *
 * @author Nathan
 */
public class Missile extends MovingObject implements Enemy{
    private double turnSpeed = 0.1;
    private double numWaypoints = 4;
    private double fallSpeed = 60;
    private double verticleTraverseSpeed = 80;
    private double maxDivertDistance = 60;
    private ArrayList<double[]> waypoints;
    private boolean initiallyLanded = false;
    private boolean midAir = true;
    private boolean dead = false;
    
    public static void main(String[] args) {
        
    }
    
    public Missile(double[] position, double scale) {
        super(position, ModelManager.getMissileModel(scale), 80, new double[] {0, position[4]});
    }
    
    private void initializeWaypoints(Battlezone battlezone) {
        waypoints = new ArrayList<double[]>();
        double previousY = 0;
        for(int i = 0; i < numWaypoints; i++) {
            double x = battlezone.getMapRadius() * (i + 1)/(numWaypoints + 1);
            double y = ((Math.random() * 2 * maxDivertDistance) - maxDivertDistance);
            if(i != 0 && ((y < 0 && previousY < 0) || (y > 0 && previousY > 0)))
                y *= -1;
            previousY = y;
            PlayerTank player = battlezone.getPlayer();
            double playerAngle = player.getYRot() + Math.PI/2;
            
            double[] point = new double[] {player.getX() + (Math.cos(playerAngle) * x) - (Math.sin(playerAngle) * y), player.getZ() + (Math.sin(playerAngle) * x) + (Math.cos(playerAngle) * y)};
            waypoints.add(0, point);
        }
        if(waypoints.size() != 0)
            setDirectionToNextWaypoint();
    }
    
    private double getAngleToPoint(double[] point) {
        double relativeX = point[0] - getX();
        double relativeZ = (point[1] - getZ());
        double angle = Math.atan(relativeZ/relativeX);
        if(relativeX < 0 && relativeZ >= 0) {
            angle += Math.PI;
        }
        if(relativeX > 0 && relativeZ <= 0) {
            angle += Math.PI * 2;
        }
        if(relativeX <= 0 && relativeZ <= 0) {
            angle += Math.PI;
        }
        return angle;
    }
    
    private void setDirectionToNextWaypoint() {
        double angle = getAngleToPoint(waypoints.get(0));
        setDirection(new double[] {0, angle});
        setYRot(angle - Math.PI/2);
    }
    
    private double getSquaredDistToNextWaypoint() {
        double[] p = waypoints.get(0);
        return Math.pow(p[0] - getX(), 2) + Math.pow(p[1] - getZ(), 2);
    }
    
    private void checkForWaypointAchieved(double timePassed, Battlezone battlezone) {
        if(getSquaredDistToNextWaypoint() <= Math.pow(getVelocity() * timePassed, 2)) {
            waypoints.remove(0);
            if(waypoints.size() != 0)
                setDirectionToNextWaypoint();
            else {
                PlayerTank player = battlezone.getPlayer();
                double angle = getAngleToPoint(new double[] {player.getX(), player.getZ()});
                setDirection(new double[] {0, angle});
                setYRot(angle - Math.PI/2);
            }
                
        }
    }
    
    private void trackPlayer(Battlezone battlezone, double timePassed) {
        double[] angles = getAngularDistsToTank(battlezone.getPlayer());
        if((angles[0] < angles[1] && angles[0] > Math.PI/2) || (angles[1] < angles[0] && angles[1] > Math.PI/2)) {
            
        }    
        else if(angles[0] > angles[1])
            setDirection(new double[] {0, getDirection()[1] + (turnSpeed * timePassed)});
        else
            setDirection(new double[] {0, getDirection()[1] - (turnSpeed * timePassed)});
        setYRot(getDirection()[1] - Math.PI/2);
    }
    
    public double[] getAngularDistsToTank(Tank t) {
      double angle = getAngleToPoint(new double[] {t.getX(), t.getZ()});
      double clockwiseDist = ((getYRot() + (Math.PI/2)) % (Math.PI * 2)) - angle;
      if(clockwiseDist < 0)
          clockwiseDist += Math.PI * 2;
      return new double[] {clockwiseDist, Math.PI * 2 - clockwiseDist}; 
  }
    
    private void missileLogic(Battlezone battlezone, double timePassed) {
        if(waypoints == null)
            initializeWaypoints(battlezone);       
        if(waypoints.size() != 0)
            checkForWaypointAchieved(timePassed, battlezone);
        if(waypoints.size() == 0)
            trackPlayer(battlezone, timePassed);
                
    }
    
    public boolean getDead() {
        return dead;
    }
    
    public boolean getMidAir() {
        return midAir;
    }
    
    public void setDead(boolean b) {
        dead = b;
    }
    
    public boolean getInitiallyLanded() {
        return initiallyLanded;
    }
    
    public void update(double timePassed, Battlezone battlezone) {
        super.update(timePassed, battlezone);
        missileLogic(battlezone, timePassed);
    }
    
    public void move(double timePassed, Battlezone battlezone) {
        ArrayList<Obstacle> obstacles = battlezone.getObstacles();
        PlayerTank player = battlezone.getPlayer();
        super.move(timePassed, battlezone);
        
        if(!initiallyLanded) {
            midAir = getY() < Obstacle.getObstacleHeight();
            setY(getY() + fallSpeed * timePassed);
            if(getY() > 0) {
                setY(0);
                initiallyLanded = true;
            }
            return;
        }
            
        boolean obstacleCollision = false;
        for(Obstacle ob : obstacles) {
            if(ob.bulletBoxCollision(getCollisionBox())) {
                obstacleCollision = true;
                break;
            }
        }
        if(obstacleCollision) {
            setY(getY() - (timePassed * verticleTraverseSpeed));
            midAir = true;
            if(getY() < Obstacle.getObstacleHeight()) {
                setY(Obstacle.getObstacleHeight());
            }
        }
        else {
            setY(getY() + (timePassed * verticleTraverseSpeed));
            if(getY() > 0) {
                midAir = false;
                setY(0);
            }
        }
        
        
        if(player != null && player.collisionBoxCollision(getCollisionBox())) {
            FreefallingDebris.explode(battlezone, getPosition());
            player.setDead(true);
            battlezone.removeUpdatable((Updatable) player);

            setDead(true);
            battlezone.removeUpdatable((Updatable) this);
        }
    }
}
