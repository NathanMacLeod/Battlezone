/*
 * File added by Nathan MacLeod 2019
 */
package gameObject;
import battlezone.Battlezone;
/**
 * @author macle
 */
public class EnemyTank extends Tank implements Enemy {
    private double remainingAngle;
    private double remainingDist;
    
    public EnemyTank(double[] position, double scale, double bulletHeight) {
        super(position, scale, bulletHeight, 8, 0.1);
    }
    
    protected void bumpedObstacle(Obstacle o) {
        double relativeX = o.getX() - getX();
        double relativeZ = o.getZ() - getZ();
        double angleToObstacle = Math.atan(relativeZ/relativeX);
        if(relativeX <= 0 && relativeZ >= 0) {
            angleToObstacle += Math.PI;
        }
        if(relativeX >= 0 && relativeZ <= 0) {
            angleToObstacle += Math.PI * 2;
        }
        if(relativeX <= 0 && relativeZ <= 0) {
            angleToObstacle += Math.PI;
        }
        double counterDist = ((getYRot() + (Math.PI/2)) % (Math.PI * 2)) - angleToObstacle;
        if(counterDist < 0) 
            counterDist += Math.PI * 2;
        double clockwiseDist = (Math.PI * 2) - counterDist;
        remainingAngle = Math.PI/8;
        if(counterDist < clockwiseDist) {
            setTrack1(0);
            setTrack2(-1);
        }
        else {
            setTrack2(0);
            setTrack1(-1);
        }
    }
    
    private void maneuverAfterBump(double timePassed, Battlezone battlezone) {
        if(remainingAngle > 0) {
            remainingAngle -= getTrackTurnValue() * timePassed;
            if(remainingAngle <= 0) {
                double lineSlope = Math.tan(getYRot());
                double tankB = (lineSlope * -getX()) + getZ();
                PlayerTank p = battlezone.getPlayer();
                if(p == null)
                    remainingDist = 0;
                else {
                    double playerB = (lineSlope * -p.getX()) + p.getZ();
                    remainingDist = Math.abs(tankB - playerB) * Math.sin((Math.PI/2) - Math.atan(lineSlope));
                }
                setTrack1(1);
                setTrack2(1); 
            }
        }
        else if(remainingDist > 0) {
            remainingDist -= getTrackMoveValue() * 2 * timePassed;
        }
    }
    
    private void maneuverAndAttack(Battlezone battlezone, double timePassed) {
        
        PlayerTank player = battlezone.getPlayer();
        if(player == null) {
            setTrack1(1);
            setTrack2(1);
            return;
        }
        
        
        if(player.getDead())
            return;
        double[] angles = this.getAngularDistsToTank(player);
        double clockwiseDist = angles[0];
        double counterDist = angles[1];
        double relativeX = player.getX() - getX();
        double relativeZ = player.getZ() - getZ();
        
        double playerDist = Math.sqrt(Math.pow(relativeX, 2) + Math.pow(relativeZ, 2));
        
        double acceptableFireAngle = Math.atan(player.getMinCrossection()/playerDist);
        
        if(clockwiseDist <= acceptableFireAngle/2 || counterDist <= acceptableFireAngle/2) {
            tryToFire(battlezone);
        }
        
        if(clockwiseDist <= getTrackTurnValue() * timePassed || counterDist <= getTrackTurnValue() * timePassed) {
            setTrack1(1);
            setTrack2(1);
        } 
        else if(counterDist <= acceptableFireAngle/2) {
            setTrack1(1);
            setTrack2(0);
        }
        else if(clockwiseDist <= acceptableFireAngle/2) {
            setTrack2(1);
            setTrack1(0);
        }
        else if(counterDist < clockwiseDist) {
            setTrack1(1);
            setTrack2(-1);
        }
        else {
            setTrack2(1);
            setTrack1(-1);
        }
        
        
    }
    
    public void update(double timePassed, Battlezone battlezone) {
        if(remainingDist > 0 || remainingAngle > 0) {
            maneuverAfterBump(timePassed, battlezone);
        }
        else {
            maneuverAndAttack(battlezone, timePassed);
        }
        super.update(timePassed, battlezone);
    }
    
}
