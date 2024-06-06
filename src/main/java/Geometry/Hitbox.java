/*
 * File added by Nathan MacLeod 2019
 */
package Geometry;

/**
 *
 * @author macle
 */
public class Hitbox {
    //Exists only on XZ plane
    
    private Point[] modelPoints;
    //Where the points are relative to the model
    private Point[] worldPoints;
    //Where the points are relative to the world
    private double[] domain;
    //Min and max x values in the world
    private double[] range;
    // min and max z values in the worlds
    private Line[] lines;
    //the lines in the hitbox, used to check for intersections to detect a collision
    private Object3D object;
    //object that has the hitbox
    
    public Hitbox(Object3D object, Point[] modelPoints) {
        this.object = object;
        this.modelPoints = modelPoints;
    }
    
    public void setObject(Object3D object) {
        this.object = object;
    }
    
    private Point[] copyPoints() {
        //Returns a new array of points with the same values
        Point[] copy = new Point[modelPoints.length];
        for(int i = 0; i < modelPoints.length; i++) {
            Point p = modelPoints[i];
            copy[i] = new Point(p.getX(), p.getY(), p.getZ());
        }
        return copy;
    }
    
    public void updateWorldPos() {
        //using model points and the object world position calculates the points poisiton in the world
        if(modelPoints.length == 0) 
            return;
        worldPoints = copyPoints();
        object.transformPointsToWorldPos(worldPoints);
        createLines();
        findDomainRange();
    }
    
    private void findDomainRange() {
        domain = new double[2];
        range = new double[2];
        domain[0] = Double.MAX_VALUE;
        domain[1] = -Double.MAX_VALUE;
        range[0] = domain[0];
        range[1] = domain[1];
        for(Line l : lines) {
            if(l.getDomain()[0] < domain[0])
                domain[0] = l.getDomain()[0];
            if(l.getDomain()[1] > domain[1])
                domain[1] = l.getDomain()[1];
            if(l.getRange()[0] < range[0])
                range[0] = l.getRange()[0];
            if(l.getRange()[1] > range[1])
                range[1] = l.getRange()[1];  
        }
        
    }
    
    public Point[] getWorldPoints() {
        //returns copy of worldpoints
        Point[] points = new Point[worldPoints.length];
        for(int i = 0; i < worldPoints.length; i++) {
            Point p = worldPoints[i];
            points[i] = new Point(p.getX(), p.getY(), p.getZ());
        }
        return points;
    }
    
    public Point[] getModelPoints() {
        return modelPoints;//Returns actual points, will alter the hitbox
    }
    
    public Point[] getCopyModelPoints() {
        Point[] points = new Point[modelPoints.length];
        for(int i = 0; i < modelPoints.length; i++) {
            Point p = modelPoints[i];
            points[i] = new Point(p.getX(), p.getY(), p.getZ());
        }
        return points;
    }
    public double[] getDomain() {
        return domain;
    }
    
    public double[] getRange() {
        return range;
    }
    
    private boolean intersectPossible(Hitbox hitbox) {
        //Returns if an intersetion is possible based on the domain and range of the two hitboxes
        boolean a = !(domain[1] < hitbox.getDomain()[0] || domain[0] > hitbox.getDomain()[1] ||
                    range[1] < hitbox.getRange()[0] || range[0] > hitbox.getRange()[1]);
        //System.out.println(domain[0] + " " + domain[1] + " " + range[0]  + " " + range[1] + " OtherTank " + hitbox.getDomain()[0] + " " + hitbox.getDomain()[1] + " " + hitbox.getRange()[0]  + " " + hitbox.getRange()[1]);
        //System.out.println(a);
        return a;
    }    
    
    private void createLines() {
        //creates the lines given the points
        lines = new Line[worldPoints.length];
        for(int i = 0; i < worldPoints.length; i++) {
            Line line;
            if(i == worldPoints.length - 1)
                line = new Line(worldPoints[i], worldPoints[0], 1);
            else
                line = new Line(worldPoints[i], worldPoints[i+1], 1);
            lines[i] = line;
        }
    }
    
    public Line[] getLines() {
        return lines;
    }
    
    public boolean hitboxIntersect(Hitbox hitbox) {
        //returns if the two hitboxes intersect
        if(!intersectPossible(hitbox))
            return false;
        for(Line thisLine: lines) {
            for(Line thatLine : hitbox.getLines()) {
                if(thisLine.lineIntersect(thatLine))
                    return true;
            }
        }
        return false;
    }
    
}
