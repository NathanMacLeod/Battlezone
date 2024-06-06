/*
 * File added by Nathan MacLeod 2019
 */
package Matrix;

/**
 *
 * @author macle
 */
public class Matrix {
    private double[][] content;
       
    public static boolean checkContentValid(double[][] content) {
        if(content.length == 0)
            return false;
        int rowLength = content[0].length;
        for(double[] i : content) {
            if(i.length != rowLength)
                return false;
        }
        return true;
    }
    
    public Matrix(double[][] content) {
        if(!Matrix.checkContentValid(content))
            throw new IllegalArgumentException("Content either does not have even rows or is empty");
        this.content = content;
    }
    
    public Matrix multiply(Matrix in) {
        //in is matrix on the right
        if(content[0].length != in.getHeight()) 
            throw new IllegalArgumentException("Tried to multiply a [" + getHeight() + ", "  + getWidth() + "] by a [" + in.getHeight() + ", " + in.getWidth() + "]");
        double[][] product = new double[getHeight()][in.getWidth()];
        for(int i = 0; i < product.length; i++) {
            for(int j = 0; j < product[0].length; j++) {
                for(int k = 0; k < content[0].length; k++) {
                    product[i][j] += content[i][k] * in.get(k, j);
                }
            }
        }
        return new Matrix(product);
    }
    
    public void printMatrix() {
        for(double[] row : content) {
            System.out.println();
            for(double val : row) {
                System.out.print(val + " ");
            }
        }
            
    }
    
    public double get(int row, int col) {
        if(row < 0 || row >= content.length
           || col < 0 || col >= content[row].length)
           throw new IndexOutOfBoundsException("Tried to acess (" + row + ", " + col + ") of a [" + getHeight() + ", "  + getWidth() + "] matrix");
        return content[row][col];
    }
    
    public double[] getRow(int row) {
        if(row < 0 || row >= content.length) 
            throw new IndexOutOfBoundsException("Tried to acess row " + row + " of a [" + getHeight() + ", "  + getWidth() + "] matrix");
        return content[row];
    }
    
    public int getHeight() {
        return content.length;
    }
    
    public int getWidth() {
        if(content.length != 0)
            return content[0].length;
        return 0;
    }
    
}
