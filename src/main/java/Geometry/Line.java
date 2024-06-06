/*
 * File added by Nathan MacLeod 2019
 */
package Geometry;

/**
 *
 * @author macle
 */
public class Line {
    //Two dimensional
        private double[] p1;
        private double[] p2;
        private double[] domain;
        private double[] range;
        private double m;
        private double b;
        private boolean undefined;
        
        public Line(Point p1, Point p2, int whichDimensions) {
            //0 - ignore X dimensions, 1 - ignore Y Dimensions, 2, ignore Z Dimensions
            switch(whichDimensions) {
                case 0:
                    this.p1 = new double[] {p1.getZ(), p1.getY()};
                    this.p2 = new double[] {p2.getZ(), p2.getY()};
                    break;
                case 1:
                    this.p1 = new double[] {p1.getX(), p1.getZ()};
                    this.p2 = new double[] {p2.getX(), p2.getZ()};
                    break;
                default:
                    this.p1 = new double[] {p1.getX(), p1.getY()};
                    this.p2 = new double[] {p2.getX(), p2.getY()};
                    break;
            }
            calculateDomainRange();
            calculateMB();
        }
        
        public Line(double[] p1, double[] p2) {
            this.p1 = p1;
            this.p2 = p2;
            calculateDomainRange();
            calculateMB();
        }
        
        private void calculateDomainRange() {
            domain = new double[2];
            range = new double[2];
            if(p1[0] > p2[0]) {
                domain[0] = p2[0];
                domain[1] = p1[0];
            }
            else {
                domain[0] = p1[0];
                domain[1] = p2[0];
            }
            if(p1[1] > p2[1]) {
                range[0] = p2[1];
                range[1] = p1[1];
            }
            else {
                range[0] = p1[1];
                range[1] = p2[1];
            }
        }
        
        private void calculateMB() {
            if(domain[0] == domain[1]) {
                undefined = true;
                m = -1;
                b = domain[0];
                return;
            }
            m = (p1[1] - p2[1])/(p1[0] - p2[0]);
            b = (m * -p1[0]) + p1[1];
            //System.out.println(m + " " + b);
            
        }
        
        public double getM() {
            return m;
        }
        
        public double getB() {
            return b;
        }
        
        public double[] getDomain() {
            return domain;
        }
        
        public double[] getRange() {
            return range;
        }
        
        public boolean getUndefined() {
            return undefined;
        }
        
        private boolean intersectPossible(Line line) {
            return !(domain[1] < line.getDomain()[0] || domain[0] > line.getDomain()[1] ||
                    range[1] < line.getRange()[0] || range[0] > line.getRange()[1]);
        } 
        
        public boolean canGetIntersectPoint(Line line) {
            return lineIntersect(line) && line.getM() != m;
        }
        
        public double[] getIntersectPoint(Line line) {
            double[] intersection = new double[2];
            if(undefined) {
                intersection[0] = b;
                intersection[1] = (line.getM() * b) + line.getB();
                return intersection;
            }
            if(line.getUndefined()) {
                intersection[0] = line.getB();
                intersection[1] = (m * line.getB()) + b;
                return intersection;
            }
            
            intersection[0] = (line.getB() - b)/(m - line.getM());
            intersection[1] = (m * intersection[0]) + b;
            return intersection;
        }
        
        private boolean pointInDomain(double[] point) {
            return point[0] >= domain[0] && point[0] <= domain[1] && point[1] >= range[0] && point[1] <= range[1];
        }
        
        public boolean lineIntersect(Line line) {
            if(!intersectPossible(line)) {
                return false;
            }
            if((m == line.getM() && b != line.getB()) || (undefined && line.getUndefined() && b != line.getB()))
                return false;
            double[] point = getIntersectPoint(line);
            if(!pointInDomain(point) || !line.pointInDomain(point))
                return false;
            return true;
        }
    }
 
