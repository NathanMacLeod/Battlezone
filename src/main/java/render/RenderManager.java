/*
 * File added by Nathan MacLeod 2019
 */
package render;
import java.awt.image.BufferedImage;
import java.awt.*;
import Geometry.Object3D;
import Matrix.Matrix;
import java.util.ArrayList;
/**
 *
 * @author macle
 */
public class RenderManager {
    
    private static Matrix getTransformationMatrix(double[] location, double[] rotation) {
        Matrix translationMatrix = new Matrix(new double[][] {{1, 0, 0, 0}, {0, 1, 0, 0}, {0, 0, 1, 0}, {-location[0], -location[1], -location[2], 1}});
        Matrix rotationXMatrix = new Matrix(new double[][] {{1, 0, 0, 0}, {0, Math.cos(-rotation[0]), -Math.sin(-rotation[0]), 0}, {0, Math.sin(-rotation[0]), Math.cos(-rotation[0]), 0}, {0, 0, 0, 1}});
        Matrix rotationYMatrix = new Matrix(new double[][] {{Math.cos(-rotation[1]), 0, Math.sin(-rotation[1]), 0}, {0, 1, 0, 0}, {-Math.sin(-rotation[1]), 0, Math.cos(-rotation[1]), 0}, {0, 0, 0, 1}});
        return (translationMatrix.multiply(rotationYMatrix)).multiply(rotationXMatrix);
    }
     
    public static BufferedImage createRender(ArrayList<Object3D> objects, double[] location, double[] rotation, double fov, int[] imageDimensions) {
        BufferedImage image = new BufferedImage(imageDimensions[0], imageDimensions[1], BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        Matrix transformationMatrix = getTransformationMatrix(location, rotation);
        for(Object3D obj : objects)
            obj.draw(g, transformationMatrix, fov, imageDimensions);
        return image;
    }
    
}
