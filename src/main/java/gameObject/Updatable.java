/*
 * File added by Nathan MacLeod 2019
 */
package gameObject;

import java.util.ArrayList;
import Geometry.Object3D;
import battlezone.Battlezone;
/**
 *
 * @author macle
 */
public interface Updatable {
    
    public void update(double time, Battlezone battlezone);
    
}
