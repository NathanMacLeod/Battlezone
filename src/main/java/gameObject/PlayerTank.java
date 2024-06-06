/*
 * File added by Nathan MacLeod 2019
 */
package gameObject;

import Geometry.Hitbox;
import Geometry.Point;
import java.awt.Graphics;
import Matrix.Matrix;

/**
 *
 * @author macle
 */
public class PlayerTank extends Tank {
    
    public PlayerTank(double[] position, double scale, double bulletHeight) {
        super(position, scale, bulletHeight + 0.15, 10, 0.14);
    }
    
    public void draw(Graphics g, Matrix transformationMatrix, double fov, int[] screenDimensions) {
        
    }
    
}
