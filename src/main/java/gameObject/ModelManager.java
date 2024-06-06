/*
 * File added by Nathan MacLeod 2019
 */
package gameObject;
import Geometry.*;
import java.util.ArrayList;
/**
 *
 * @author macle
 */
public class ModelManager {
    private static double[][] missileModel = new double[][] {
        {3, 0, 3}, {3, 0, -2}, {-3, 0, -2}, {-3, 0, 3},
        {1, -3.375, 1}, {1, -3, -1}, {-1, -3, -1}, {-1, -3.375, 1},
        {0, -6, 15},
        {4, -3, -1}, {6, -6, -1}, {4, -9, -1}, {-4, -9, -1}, {-6, -6, -1}, {-4, -3, -1},
        {1.5, -5, -4}, {2, -6, -4}, {1.5, -7, -4}, {-1.5, -7, -4}, {-2, -6, -4}, {-1.5, -5, -4},
        {0, -8.812, 0}, {0.85, -7.5, 7}, {-0.85, -7.5, 7},
        {0, -9.937, 2}
    };
    
    private static int[][] missileModelLines = new int[][] {
        {0, 1}, {1, 2}, {2, 3}, {3, 0},
        {0, 4}, {1, 5}, {2, 6}, {3, 7},
        {4, 5}, {5, 6}, {6, 7}, {7, 4},
        {9, 10}, {10, 11}, {11, 12}, {12, 13}, {13, 14}, {14, 9},
        {15, 16}, {16, 17}, {17, 18}, {18, 19}, {19, 20}, {20, 15},
        {9, 15}, {10, 16}, {11, 17}, {12, 18}, {13, 19}, {14, 20},
        {9, 8}, {10, 8}, {11, 8}, {12, 8}, {13, 8}, {14, 8},
        {21, 22}, {22, 23}, {23, 21},
        {21, 24}, {22, 24}, {23, 24}
    };
        
    private static double missileModelScale = 9;
    
    private static double[][] tankModel = new double[][] {
        {4, 0, 6.5}, {4, 0, -5.5}, {-4, 0, -5.5}, {-4, 0, 6.5},
        {4.5, -1.5, 7.5}, {4.5, -1.5, -6.5}, {-4.5, -1.5, -6.5}, {-4.5, -1.5, 7.5},
        {2.5, -3, 4.5}, {2.5, -3, -5}, {-2.5, -3, -5}, {-2.5, -3, 4.5},
        {1.5, -5, -4.5}, {-1.5, -5, -4.5},
        {.25, -4.5, -1.875}, {-.25, -4.5, -1.875}, {.25, -4.5, 5.5}, {-.25, -4.5, 5.5}, {.25, -4, 5.5}, {-.25, -4, 5.5}, {.25, -4, 0}, {-.25, -4, 0},
        {0, -4.25, 5.5}    
    };
    
    private static double tankModelScale = 13;
    
    private static int[][] tankModelLines = new int[][] {
        {0, 1}, {1, 2}, {2, 3}, {3, 0},
        {0, 4}, {1, 5}, {2, 6}, {3, 7},
        {4, 5}, {5, 6}, {6, 7}, {7, 4},
        {4, 8}, {5, 9}, {6, 10}, {7, 11},
        {8, 9}, {9, 10}, {10, 11}, {11, 8},
        {8, 12}, {9, 12}, {10, 13}, {11, 13},
        {12, 13},
        {14, 15}, {15, 17}, {14, 16}, {16, 17}, {16, 18}, {17, 19}, {18, 19}, {18, 20}, {19, 21}, {14, 20}, {15, 21}, {20, 21}
    };
    
    private static double[][] tankShellModel = new double[][] {
        {2, -2, 0}, {2, 2, 0}, {-2, 2, 0}, {-2, -2, 0},
        {0, 0, 5}
    };
    
    private static double tankShellScale = 4;
    
    private static int[][] tankShellModelLines = new int[][] {
        {0, 1}, {1, 2}, {2, 3}, {3, 0},
        {0, 4}, {1, 4}, {2, 4}, {3, 4}
    };
    
    private static double[][] pyramidModel = new double[][] {
        {0.5, 0, 0.5}, {0.5, 0, -0.5}, {-0.5, 0, -0.5}, {-0.5, 0, 0.5},
        {0, -1, 0}
    };
    
    private static int[][] pyramidModelLines = new int[][] {
        {0, 1}, {1, 2}, {2, 3}, {3, 0},
        {0, 4}, {1, 4}, {2, 4}, {3, 4}
    };
    
    private static double pyramidScale = 1;
    
    private static double[][] prisimModel = new double[][] {
        {0.5, 0, 0.5}, {0.5, 0, -0.5}, {-0.5, 0, -0.5}, {-0.5, 0, 0.5},
        {0.5, -1, 0.5}, {0.5, -1, -0.5}, {-0.5, -1, -0.5}, {-0.5, -1, 0.5}
    };
    
    private static int[][] prisimModelLines = new int[][] {
        {0, 1}, {1, 2}, {2, 3}, {3, 0},
        {4, 5}, {5, 6}, {6, 7}, {7, 4},
        {0, 4}, {1, 5}, {2, 6}, {3, 7},  
    };
    
    private static double prisimScale = 1.3;
    
    private static double[][] halfPrisimModel = new double[][] {
        {0.5, 0, 0.5}, {0.5, 0, -0.5}, {-0.5, 0, -0.5}, {-0.5, 0, 0.5},
        {0.5, -0.3, 0.5}, {0.5, -0.3, -0.5}, {-0.5, -0.3, -0.5}, {-0.5, -0.3, 0.5}
    };
    
    private static double debrisScale = 1;
    
    private static double[][] debrisModel1 = new double[][] {
        {0, 1.3, 0}, {0, 0, 1}, {0, -1, -0.25}, {0, -0.5, -0.7},
        {1, 0, 0}, {-1, 0, -0.1}
    };
    
    private static int[][] debrisModel1Lines = new int[][] {
        {0, 1}, {1, 2}, {2, 3}, {3, 0},
        {0, 4}, {1, 4}, {2, 4}, {3, 4},
        {0, 5}, {1, 5}, {2, 5}, {3, 5}
    };
    
    private static double[][] debrisModel2 = new double[][] {
        {0, 1, 3}, {0.7, 1, 2.5}, {0.5, 1, 0}, {0.2, 1, - 0.25}, {-0.4, 1, 2},
        {0, 0, 0.5}, {0, -1.5, 0}
    };
    
    private static int[][] debrisModel2Lines = new int[][] {
        {0, 1}, {1, 2}, {2, 3}, {3, 4}, {4, 0},
        {0, 5}, {5, 6}, {6, 3}, {6, 2}
    };
    
    private static double[][] debrisModel3 = new double[][] {
        {0, -1, 4}, {0, -1, -1},
        {0.5, 0, 3.7}, {0.5, 0, 0}, {0, 0, -1}, {-0.5, 0, 0}, {-0.5, 0, 3.5},
        {0, 1, 3}, {0, 1, 0},
        {0, 2, 0}, {0.5, 2, 0}, {0, 2, -1}, {-0.5, 2, 0}
    };
    
    private static int[][] debrisModel3Lines = new int[][] {
        {0, 1},
        {2, 3}, {3, 4}, {4, 5}, {5, 6},
        {7, 8},
        {0, 7}, {0, 2}, {0, 6}, {2, 7}, {6, 7},
        {8, 9},
        {9, 10}, {10, 11}, {11, 12}, {12, 9},
        {10, 3}, {12, 5}, {11, 4},
        {3, 8}, {5, 8},
        {1, 4}, {1, 3}, {1, 5}
           
    };
    
    
    private static Model getScaledModel(double[][] points, int[][] lines, double scaleFactor, double modelScale) {
        Point[] pointCopy = new Point[points.length];
        double s = scaleFactor/modelScale;
        for(int i = 0; i < points.length; i++) {
            for(int j = 0; j < points[i].length; j++) {
                pointCopy[i] = new Point(s * points[i][0], s * points[i][1], s * points[i][2]);
            }
        }
        int[][] lineCopy = new int[lines.length][2];
        for(int i = 0; i < lines.length; i++) {
            for(int j = 0; j < lines[i].length; j++) {
                lineCopy[i][j] = lines[i][j];
            }
        }
        return new Model(null, null, pointCopy, lineCopy, lineCopy);
    }
    
    private static void createBulletBox(Model m, double bulletHeight) {
        ArrayList<Point> intersections = new ArrayList<Point>();
        for(int[] lineConnections : m.nonCosmeticLines) {
            Line xLine = new Line(m.points[lineConnections[0]], m.points[lineConnections[1]], 0);
            Line zLine = new Line(m.points[lineConnections[0]], m.points[lineConnections[1]], 2);
            Line cuttingPlane = new Line(new double[] {-Double.MAX_VALUE, bulletHeight}, new double[] {Double.MAX_VALUE, bulletHeight});
            
            if(xLine.canGetIntersectPoint(cuttingPlane) && zLine.canGetIntersectPoint(cuttingPlane)) 
                intersections.add(new Point(zLine.getIntersectPoint(cuttingPlane)[0], bulletHeight, xLine.getIntersectPoint(cuttingPlane)[0]));
            
        }
        Point[] boxPoints = new Point[intersections.size()];
        ArrayList<Object[]> points = new ArrayList<Object[]>();
        double[] centerPoint = new double[2];
        for(Point p : intersections) {
            centerPoint[0] += p.getX();
            centerPoint[1] += p.getZ();
        }
        centerPoint[0] /= intersections.size();
        centerPoint[1] /= intersections.size();
        for(Point p : intersections) {
            double relativeX = p.getX() - centerPoint[0];
            double relativeZ = p.getZ() - centerPoint[1];
            double angle = Math.atan(relativeZ/relativeX);
            if(relativeX <= 0 && relativeZ >= 0) {
                angle += Math.PI;
            }
            if(relativeX >= 0 && relativeZ <= 0) {
                angle += Math.PI * 2;
            }
            if(relativeX <= 0 && relativeZ <= 0) {
                angle += Math.PI;
            }
            for(int i = 0; i <= points.size(); i++) {
                
                if(i == points.size()) {
                    points.add(new Object[] {p, angle});
                    break;
                }
                else if((double) points.get(i)[1] < angle) {
                    points.add(i, new Object[] {p, angle});
                    break;
                }
            }
        }
        for(int i = 0; i < boxPoints.length; i++) {
            boxPoints[i] = (Point)points.get(i)[0];
        }
        m.bulletBox = new Hitbox(null, boxPoints);
    }
    
    public static double getModelBulletHeight(double scale) {
        return tankModel[22][1] * scale/tankModelScale;
    }
    
    public static double getObstacleHeight(double scale) {
        return prisimModel[5][1] * prisimScale * scale;
    }
    
    public static Model getShellModel(double scaleFactor) {
        Model model = getScaledModel(tankShellModel, tankShellModelLines, scaleFactor, tankShellScale);
        Point[] collisionBox = new Point[3];
        Point p = model.points[0];
        collisionBox[0] = new Point(p.getX(), p.getY(), p.getZ());
        p = model.points[3];
        collisionBox[1] = new Point(p.getX(), p.getY(), p.getZ());
        p = model.points[4];
        collisionBox[2] = new Point(p.getX(), p.getY(), p.getZ());
        model.collisionBox = new Hitbox(null, collisionBox);
        model.bulletBox = new Hitbox(null, collisionBox);
        return model;
    }
    
    public static Model getMissileModel(double scaleFactor) {
        Model model = getScaledModel(missileModel, missileModelLines, scaleFactor, missileModelScale);
        Point[] collisionBox = new Point[5];
        
        Point p = model.points[10];
        collisionBox[0] = new Point(p.getX(), p.getY(), p.getZ());
        
        p = model.points[13];
        collisionBox[1] = new Point(p.getX(), p.getY(), p.getZ());
        
        p = model.points[16];
        collisionBox[2] = new Point(p.getX(), p.getY(), p.getZ());
        
        p = model.points[19];
        collisionBox[3] = new Point(p.getX(), p.getY(), p.getZ());
        
        p = model.points[8];
        collisionBox[4] = new Point(p.getX(), p.getY(), p.getZ());
        
        model.collisionBox = new Hitbox(null, collisionBox);
        model.bulletBox = new Hitbox(null, collisionBox);
        return model;
    }
    
    public static Model getTankModel(double scaleFactor, double bulletHeight) {
        Model model = getScaledModel(tankModel, tankModelLines, scaleFactor, tankModelScale);
        model.nonCosmeticLines = new int[model.lines.length - 12][2];
        for(int i = 0; i < model.lines.length - 12; i++) {
            model.nonCosmeticLines[i] = model.lines[i];
        }
        Point[] collisionBox = new Point[4];
        for(int i = 4; i < 8; i++) {
            Point p = model.points[i];
            collisionBox[i - 4] = new Point(p.getX(), p.getY(), p.getZ());
        }
        model.collisionBox = new Hitbox(null, collisionBox);
        createBulletBox(model, bulletHeight);
        return model;
    }
    
    public static Model getRandomObstacleModel(double scaleFactor, double bulletHeight) {
        int random = (int)(Math.random() * 8);
        Model model;
        switch(random) {
            case 0:
            case 1:
            case 2:
                model = getScaledModel(pyramidModel, pyramidModelLines, scaleFactor, pyramidScale);
                break;
            case 3:
            case 4:
            case 5:
                model = getScaledModel(prisimModel, prisimModelLines, scaleFactor, prisimScale);
                break;
            default:
                model = getScaledModel(halfPrisimModel, prisimModelLines, scaleFactor, prisimScale);
                break;
        }
        Point[] collisionBox = new Point[4];
        for(int i = 0; i < 4; i++) {
            Point p = model.points[i];
            collisionBox[i] = new Point(p.getX(), p.getY(), p.getZ());
        }
        model.collisionBox = new Hitbox(null, collisionBox);
        createBulletBox(model, bulletHeight);
        return model;
    }
    
    public static Model getRandomDebrisModel(double scaleFactor) {
        int random = (int)(Math.random() * 3);
        Model model;
        switch(random) {
            case 0:
                model = getScaledModel(debrisModel1, debrisModel1Lines, scaleFactor, debrisScale);
                break;
            case 1:
                model = getScaledModel(debrisModel2, debrisModel2Lines, scaleFactor, debrisScale);
                break;
            default:
                model = getScaledModel(debrisModel3, debrisModel3Lines, scaleFactor, debrisScale);
                break;
        }
        return model;
    }
    
}
