/*
 * File added by Nathan MacLeod 2019
 */
package gameObject;
import Geometry.Hitbox;
import Geometry.Object3D;
import Geometry.Point;
import Matrix.Matrix;
import java.awt.Graphics;
import java.util.ArrayList;
import battlezone.Battlezone;
/**
 *
 * @author macle
 */
public class CollideableObject extends Object3D implements Updatable {
    private Hitbox bulletBox;
    private Hitbox collisionBox;
    
    public CollideableObject(Point[] points, int[][] lines, double[] position, Point[] bulletHitBox, Point[] collisionBox) {
        super(points, lines, position);
        if(bulletHitBox != null && collisionBox != null) {
            this.bulletBox = new Hitbox(this, bulletHitBox);
            this.collisionBox = new Hitbox(this, collisionBox);
            updateHitboxes();
        }
    }
    
    public CollideableObject(double[] position, Model m) {
        super(m.points, m.lines, position);
        if(m.bulletBox != null)
            m.bulletBox.setObject(this);
        if(m.collisionBox != null)
            m.collisionBox.setObject(this);
        this.bulletBox = m.bulletBox;
        this.collisionBox = m.collisionBox;
        updateHitboxes();
    }
    
    public Hitbox getBulletBox() {
        return bulletBox;
    }
    
    public Hitbox getCollisionBox() {
        return collisionBox;
    }
    
    public boolean collisionBoxCollision(Hitbox newCollisionBox) {
        return collisionBox.hitboxIntersect(newCollisionBox);
    }
    
    public boolean bulletBoxCollision(Hitbox newBulletBox) {
        return bulletBox.getModelPoints().length > 0 && bulletBox.hitboxIntersect(newBulletBox);
    }
    
    public void updateHitboxes() {
        positionUpdate = false;
        if(bulletBox != null && collisionBox != null) {
            bulletBox.updateWorldPos();
            collisionBox.updateWorldPos();
        }
    }
    
    public void update(double time, Battlezone battlezone) {
        if(positionUpdate) {
            updateHitboxes();
        }
    }
    
    public void draw(Graphics g, Matrix transformationMatrix, double fov, int[] screenDimensions) {
        super.draw(g, transformationMatrix, fov, screenDimensions);
    }
    
}
