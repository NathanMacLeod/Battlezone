/*
 * File added by Nathan MacLeod 2019
 */
package Geometry;
import Matrix.Matrix;
import java.util.ArrayList;
import java.awt.*;
/**
 *
 * @author macle
 */
public class Object3D {
    private Point[] points;
    private int[][] lines;
    
    private double x;
    private double y;
    private double z;
    
    private double xRot;
    private double yRot;
    private double zRot;
    
    protected boolean positionUpdate = false;
    
    public Object3D(Point[] points, int[][] lines, double[] position) {
        this.points = points;
        this.lines = lines;
        this.x = position[0];
        this.y = position[1];
        this.z = position[2];
        this.xRot = position[3];
        this.yRot = position[4];
        this.zRot = position[5];
    }
    
    public void setPosition(double[] position) {
        positionUpdate = true;
        this.x = position[0];
        this.y = position[1];
        this.z = position[2];
        this.xRot = position[3];
        this.yRot = position[4];
        this.zRot = position[5];
    }
    
    public double[] getPosition() {
        return new double[] {x, y, z, xRot, yRot, zRot};
    }
    
    public void setX(double d) {
        positionUpdate = true;
        x = d;
    }
    
    public void setY(double d) {
        positionUpdate = true;
        y = d;
    }
    
    public void setZ(double d) {
        positionUpdate = true;
        z = d;
    }
    
    public void setXRot(double d) {
        positionUpdate = true;
        xRot = d;
        //keeps the angle within 0 to 2PI
        while(xRot < 0) 
            xRot += Math.PI * 2;
        while(xRot > Math.PI * 2)
            xRot -= Math.PI * 2;
    }
    
    public void setYRot(double d) {
        positionUpdate = true;
        yRot = d;
        while(yRot < 0) 
            yRot += Math.PI * 2;
        while(yRot > Math.PI * 2)
            yRot -= Math.PI * 2;
        
    }
    
    public void setZRot(double d) {
        positionUpdate = true;
        zRot = d;
        while(zRot < 0) 
            zRot += Math.PI * 2;
        while(zRot > Math.PI * 2)
            zRot -= Math.PI * 2;
    }
    
    public double getX() {
        return x;
    }
    
    public double getY() {
        return y;
    }
    
    public double getZ() {
        return z;
    }
    
    public double getXRot() {
        return xRot;
    }
    
    public double getYRot() {
        return yRot;
    }
    
    public double getZRot() {
        return zRot;
    }
    
    public Point[] getCopyOfPoints() {
        Point[] copy = new Point[points.length];
        for(int i = 0; i < points.length; i++) {
            copy[i] = new Point(points[i].getX(),points[i].getY(),points[i].getZ());
        }
        return copy;
    }
    
    public Point[] getPoints() {
        return points;
    }
    
    public void transformPoints(Matrix transformationMatrix) {
        //transforms all the points using a matrix
        Matrix newPoints = toMatrix(points).multiply(transformationMatrix);
        for(int i = 0; i < points.length; i++) {
            Point p = points[i];
            p.set(newPoints.getRow(i));
        }
    }
    
    public void transformPoints(Matrix transformationMatrix, Point[] points) {
        //transforms the parameter array of points
        Matrix newPoints = toMatrix(points).multiply(transformationMatrix);
        for(int i = 0; i < points.length; i++) {
            Point p = points[i];
            p.set(newPoints.getRow(i));
        }
    }
    
    public void transformPointsToWorldPos(Point[] points) {
        //gives the world position of the points given the model position
        Matrix translationMatrix = new Matrix(new double[][] {{1, 0, 0, 0}, {0, 1, 0, 0}, {0, 0, 1, 0}, {x, y, z, 1}});
        Matrix rotationXMatrix = new Matrix(new double[][] {{1, 0, 0, 0}, {0, Math.cos(xRot), -Math.sin(xRot), 0}, {0, Math.sin(xRot), Math.cos(xRot), 0}, {0, 0, 0, 1}});
        Matrix rotationYMatrix = new Matrix(new double[][] {{Math.cos(yRot), 0, Math.sin(yRot), 0}, {0, 1, 0, 0}, {-Math.sin(yRot), 0, Math.cos(yRot), 0}, {0, 0, 0, 1}});
        Matrix rotationZMatrix = new Matrix(new double[][] {{Math.cos(zRot), -Math.sin(zRot), 0, 0}, {Math.sin(zRot), Math.cos(zRot), 0, 0}, {0, 0, 1, 0}, {0, 0, 0, 1}});
        Matrix transformationMatrix = rotationZMatrix.multiply(rotationXMatrix).multiply(rotationYMatrix).multiply(translationMatrix);
        transformPoints(transformationMatrix, points);
    }
    
    public Matrix toMatrix(Point[] points) {
        //Turns a two dimensional array of points into a matrix
        double[][] content = new double[points.length][4];
        for(int i = 0; i < points.length; i++) {
            Point p = points[i];
            content[i] = new double[] {p.getX(), p.getY(), p.getZ(), 1};
        }
        return new Matrix(content);
    }
    
    public void drawLine(int[] line, Point[] points, Graphics g, double fov, int[] screenDimensions) {
        g.setColor(Color.green);
        Point p1 = points[line[0]];
        Point p2 = points[line[1]];
        if(p1.getZ() > 0 || p2.getZ() > 0) {
            if(p1.getZ() <= 0 || p2.getZ() <= 0) {
                double xzM = (p1.getZ() - p2.getZ())/(p1.getX() - p2.getX());
                double yzM = (p1.getY() - p2.getY())/(p1.getZ() - p2.getZ());
                double z = 0.01;
                Point newPoint = new Point(((z -p1.getZ())/xzM) + p1.getX(), (yzM *(z -p1.getZ())) + p1.getY(), z);
                if(p1.getZ() <= 0) 
                    p1 = newPoint;
                else
                    p2 = newPoint;
            }
            int[] drawP1 = new int[] {(int) (p1.getX() * fov/p1.getZ()) + screenDimensions[0]/2, (int) (p1.getY() * fov/p1.getZ()) + screenDimensions[1]/2};
            int[] drawP2 = new int[] {(int) (p2.getX() * fov/p2.getZ()) + screenDimensions[0]/2, (int) (p2.getY() * fov/p2.getZ()) + screenDimensions[1]/2};
            g.drawLine(drawP1[0], drawP1[1], drawP2[0], drawP2[1]);
        }
    }
    
    public void drawPoint(Point p1, Graphics g, int radius, double fov, int[] screenDimensions) {
        if(p1.getZ() <= 0)
            return;
        int[] drawP1 = new int[] {(int) (p1.getX() * fov/p1.getZ()) + screenDimensions[0]/2, (int) (p1.getY() * fov/p1.getZ()) + screenDimensions[1]/2};
        g.setColor(Color.GREEN);
        g.fillOval(drawP1[0] - radius, drawP1[1] - radius, radius * 2, radius * 2);
    }
    
    public void draw(Graphics g, Matrix transformationMatrix, double fov, int[] screenDimensions) {
        Point[] copyOfPoints = getCopyOfPoints();
        //gets points relative to model 
        transformPointsToWorldPos(copyOfPoints);
        //makes points relative to world
        transformPoints(transformationMatrix, copyOfPoints);
        //makes points relative to player
        
        //draws the lines with the points
        for(int[] line : lines)
            drawLine(line, copyOfPoints, g, fov, screenDimensions);
    }
    
}
