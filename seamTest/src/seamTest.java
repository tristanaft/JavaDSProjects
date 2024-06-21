import edu.princeton.cs.algs4.StdOut;
public class seamTest {
    public int[][] savedMatrix;

    public seamTest(int height, int width){
        savedMatrix = new int[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                savedMatrix[i][j] = 10*i + j ;
            }
        }
    }

    public void slicedMatrix(int[] seam){
        int height = savedMatrix.length;
        int width = savedMatrix[0].length;
        int[][] newSavedMatrix = new int[height][width-1];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width - 1; j++) {
                if (j < seam[i]) {
                    newSavedMatrix[i][j] = savedMatrix[i][j];

                }
                if(j == seam[i]){
                    savedMatrix[i][j] = 0;
                }
                else { // skip i == seam[j]
                    newSavedMatrix[i][j] = savedMatrix[i][j+1];

                }
            }
        }
        savedMatrix = newSavedMatrix;
    }

    public void display(){
        for(int[] row : savedMatrix){
            for(int element : row){
                StdOut.print()
            }
        }
    }

    public static void main(String args){}
}
