import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdOut;


public class SeamCarver {

    // NOTE: PIXEL (x,y) IS IN COLUMN X AND ROW Y, THE OPPOSITE OF LINEAR ALGEBRA
    // SO EVERYTHING IS BACKWARDS (FROM MY USUAL PERSPECTIVE)
    // I think this can be avoided though... can I just load it in the weird way
    // and then just handle everything normally?

    // private Picture pic;
    // private double[][] energyMatrix;

    // Since I can't delete from Picture directly....
    // store RGB values in pixelMatrix and seams delete elements here too
    private int[][] pixelMatrix;
    // private double[][] energyMatrix;

    // col version of edgeTo in Dijkstra alg
    // private int[][] colTo;
    private int width;
    private int height;

    // I only want to transpose when I need to,
    // so I need to keep track of what the orientation is
    private boolean isVertical = true;

    private boolean horizOverride = false;


    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) {
            throw new IllegalArgumentException("Picture is null");
        }
        // pic = picture;

        width = picture.width();
        height = picture.height();
        pixelMatrix = new int[height][width];


        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                pixelMatrix[i][j] = picture.getRGB(j, i);
            }
        }

        // Note: I am going to try calculating the energy matrix ONCE instead of each time a seam is removed...

        // energyMatrix = calcEnergyMatrix32();

        // calcEnergyMatrix();
        // calcEnergyMatrix32();


    }

    //  unit testing (optional)
    public static void main(String[] args) {
        // no testing for now...
        StdOut.println("This is just so autograder doesn't yell at me for importing StdOut.");
    }

    // current picture
    public Picture picture() {
        if (!isVertical) {
            pixelMatrix = transpose(pixelMatrix);
            isVertical = true;
        }

        Picture pic = new Picture(width, height);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                pic.setRGB(j, i, pixelMatrix[i][j]);
            }
        }
        return pic;

    }

    // width of current picture
    public int width() {
        return this.width;
    }

    // height of current picture
    public int height() {
        return this.height;
    }

    // Takes in two colors and calculates the added SQUARED differences
    // between their RGB components
    private int colorDiff32(int[] a, int[] b) {
        int rDiff = a[0] - b[0];
        int gDiff = a[1] - b[1];
        int bDiff = a[2] - b[2];
//
        return (rDiff * rDiff + gDiff * gDiff + bDiff * bDiff);
    }

    // converts 32 bit RGB to R G B array
    private int[] rgbComponents(int rgb) {
        int red = (rgb >> 16) & 0x000000FF;
        int green = (rgb >> 8) & 0x000000FF;
        int blue = (rgb) & 0x000000FF;
//
        return new int[]{red, green, blue};
    }

    // I think this is a way more efficient way of calculating the energy since we
    // already are saving the 32 bit color data...
    private double energy32(int x, int y) {
        // int z;
        // if(!isVertical){
        //     z = x;
        //     x = y;
        //     y = z;
        // }

        // defined as 1000 at the edges, check those first
        // if (x < 0 || y < 0 || x > pixelMatrix[0].length || y > pixelMatrix.length) {
        //     // StdOut.printf("x=%d y=%d width=%d height=%d", x, y, pixelMatrix[0].length, pixelMatrix.length);
        //     throw new IllegalArgumentException();
        // }
        if (x >= pixelMatrix[0].length - 1 || y >= pixelMatrix.length - 1) {
            return 1000;
        }
        if (x == 0 || y == 0) {
            return 1000;
        }
//
        int[] left = rgbComponents(pixelMatrix[y][x - 1]);
        int[] right = rgbComponents(pixelMatrix[y][x + 1]);
        int[] above = rgbComponents(pixelMatrix[y + 1][x]);
        int[] below = rgbComponents(pixelMatrix[y - 1][x]);
//
        return Math.sqrt(colorDiff32(left, right) + colorDiff32(above, below));
    }

    private double[][] calcEnergyMatrix32() {
        double[][] eMat = new double[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (i == 0 || j == 0 || i == height - 1 || j == width - 1) {
                    eMat[i][j] = 1000;
                } else {
                    eMat[i][j] = energy32(j, i);
                }
            }
        }
        return eMat;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (isVertical) {
            return energy32(x, y);
        } else {
            return energy32(y, x);
        }


    }

    // I took this out of relaxVertex so I didn't have to redo it 3 times
    // it is a relaxation step going from (i,j1) to (i+1,j2)
    private void relaxStep(int i, int j1, int j2, double[][] dMat, double[][] eMat, int[][] cMat) {
        //// StdOut.printf("relaxStep i: %d, j1: %d, j2: %d\n", i, j1, j2);
        if (dMat[i + 1][j2] > (dMat[i][j1] + eMat[i + 1][j2])) {
            dMat[i + 1][j2] = (dMat[i][j1] + eMat[i + 1][j2]);
            cMat[i + 1][j2] = j1;
        }

    }

    private void relaxVertex(int i, int j, double[][] dMat, double[][] eMat, int[][] cMat) {
        // // StdOut.printf("in relaxVertex, height: %d width: %d", height, width);
        // Ok, we assume that the pixel is connected to the 3 pixels beneath it
        // so (i-1, j-1), (i-1, j) and (i-1, j+1) but will need to check these are not OOB
        int numRows = eMat.length;
        int numCols = eMat[0].length;

        // int numCols = eMat[0].length;

        if (i >= numRows - 1) {
            throw new IllegalArgumentException("Attempting to relax past end of matrix");
        }

        if (j > 0) {
            relaxStep(i, j, j - 1, dMat, eMat, cMat);
        }

        relaxStep(i, j, j, dMat, eMat, cMat);

        if (j < numCols - 1) {
            relaxStep(i, j, j + 1, dMat, eMat, cMat);
        }
    }

    private int[][] transpose(int[][] matrix) {
        int numRows = matrix.length;
        int numCols = matrix[0].length;
        int[][] matT = new int[numCols][numRows];
        int i, j;
        for (i = 0; i < numRows; i++) {
            for (j = 0; j < numCols; j++) {
                matT[j][i] = matrix[i][j];
            }
        }

        return matT;
    }

    // private double[][] transpose(double[][] matrix) {
    //     int numRows = matrix.length;
    //     int numCols = matrix[0].length;
    //     double[][] matT = new double[numCols][numRows];
    //     int i, j;
    //     for (i = 0; i < numRows; i++) {
    //         for (j = 0; j < numCols; j++) {
    //             matT[j][i] = matrix[i][j];
    //         }
    //     }
//
    //     return matT;
    // }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        // if not transposed, then transpose,
        // then get the seam with vertical method...
        //this.horizOverride = true;
        // if (isVertical) {
        //     this.pixelMatrix = transpose(pixelMatrix);
        //     // this.energyMatrix = transpose(energyMatrix);
        //     isVertical = false;
        // }

        // need to temporarily override height and width
        // then calculate the seam with the vertical method.
        // int heightSaved = height();
        // int widthSaved = width();
//
        // this.width = heightSaved;
        // this.height = widthSaved;

        this.pixelMatrix = transpose(pixelMatrix);
        // StdOut.printf("Horiz before findVertical pixelMat: %d,%d \n", pixelMatrix.length, pixelMatrix[0].length);
        int[] horizSeam = findVerticalSeam();
        this.pixelMatrix = transpose(pixelMatrix);
        // StdOut.printf("After findVertical pixelMat: %d,%d \n", pixelMatrix.length, pixelMatrix[0].length);
        // StdOut.printf("Current height() and width(): %d,%d \n", height(), width());

        height = pixelMatrix.length;
        width = pixelMatrix[0].length;


        // StdOut.println();
        // for(double[] row : this.energyMatrix)
        //     System.out.println(Arrays.toString(row));
        // System.out.println();

        // width = widthSaved;
        // height = heightSaved;


        return horizSeam;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        // do need to check current picture orientation and fix if necessary
        // have another boolean to check if we are trying to find a horizontal seam

        height = pixelMatrix.length;
        width = pixelMatrix[0].length;
        // StdOut.printf("Dims in findVertical pixelMat: %d,%d \n", pixelMatrix.length, pixelMatrix[0].length);
        // StdOut.printf("Current height() and width(): %d,%d \n", height(), width());
        double[][] energyMatrix = calcEnergyMatrix32();
        int[][] colTo = new int[height][width];

        int[] verticalSeam = new int[height];
        double[][] distTo = new double[height][width];
        //// StdOut.printf("distTo dims height: %d width: %d\n", distTo.length, distTo[0].length);
        // initialize all values to infinity in distTo
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                distTo[i][j] = Double.POSITIVE_INFINITY;
            }
        }

        for (int j = 0; j < width; j++) {
            distTo[0][j] = energy(0, j);
        }

        // StdOut.printf("Current height and width: %d, %d", height, width);
        // StdOut.println();

        // Go from top to bottom relaxing the matrices
        // Note: can't relax the bottom row since it has no connections below it

        // StdOut.printf("Dim Check emat: %d,%d distTo: %d,%d, colTo %d,%d",
        //         energyMatrix.length, energyMatrix[0].length, distTo.length, distTo[0].length, colTo.length, colTo[0].length);

        // StdOut.println();
        for (int i = 0; i < height - 1; i++) {
            for (int j = 0; j < width; j++) {
                // StdOut.print(i + " " + j);
                relaxVertex(i, j, distTo, energyMatrix, colTo);
            }
        }

        // find shortest distance in bottom row,
        double lowestDistance = Double.POSITIVE_INFINITY;
        for (int j = 0; j < width; j++) {
            //StdOut.print(distTo[height - 1][j] + " ");
            if (lowestDistance > distTo[height - 1][j]) {
                lowestDistance = distTo[height - 1][j];
                verticalSeam[height - 1] = j;
            }
        }
        // StdOut.println();
        // StdOut.print(lowestDistance);
        // StdOut.println();

        // trace back up the elements in colTo to get the seam
        int idx = verticalSeam[height - 1];
        for (int i = height - 1; i > 0; i--) {
            verticalSeam[i - 1] = colTo[i][idx];
            idx = verticalSeam[i - 1];
        }

        this.horizOverride = false;

        return verticalSeam;
    }

    private boolean invalidSeam(int[] seam) {
        boolean isValid = true;

        // can't be further than 1 from eachother
        for (int i = 0; i < seam.length - 1; i++) {
            if (Math.abs(seam[i] - seam[i + 1]) > 1) {
                isValid = false;
                break;
            }
        }


        return !isValid;
    }

    private int[][] horizCut(int[] seam, int[][] matrix) {
        int numRows = matrix.length;
        int numCols = matrix[0].length;
        // StdOut.printf("horizCut dims numRows: %d, width: %d\n", numRows, width);

        if (numCols == 1) {
            throw new IllegalArgumentException("Too small to perform desired cut");
        }

        int[][] newMatrix = new int[numRows][numCols - 1];
        // StdOut.printf("newMatrix dims numRows: %d, width: %d\n", newMatrix.length, newMatrix[0].length);

        // int[] removedEnergies = new int[numRows];
        for (int i = 0; i < numRows; i++) {
            // StdOut.println(matrix[i]);
            // before the seam
            // StdOut.printf("i: %d, matrix[i].length: %d newMatrix[i].length: %d\n", i, matrix[i].length, newMatrix[i].length);
            System.arraycopy(matrix[i], 0, newMatrix[i], 0, seam[i]);
            // after the seam
            System.arraycopy(matrix[i], seam[i] + 1, newMatrix[i], seam[i], numCols - seam[i] - 1);
        }
        return newMatrix;
    }

    // private double[][] horizCut(int[] seam, double[][] matrix) {
    //     int numRows = matrix.length;
    //     int numCols = matrix[0].length;
    //     // StdOut.printf("horizCut dims numRows: %d, width: %d\n", numRows, width);
//
    //     if (numCols == 1) {
    //         throw new IllegalArgumentException("Too small to perform desired cut");
    //     }
//
    //     double[][] newMatrix = new double[numRows][numCols - 1];
    //     // StdOut.printf("newMatrix dims numRows: %d, width: %d\n", newMatrix.length, newMatrix[0].length);
//
    //     // int[] removedEnergies = new int[numRows];
    //     for (int i = 0; i < numRows; i++) {
    //         // StdOut.println(matrix[i]);
    //         // before the seam
    //         // StdOut.printf("i: %d, matrix[i].length: %d newMatrix[i].length: %d\n", i, matrix[i].length, newMatrix[i].length);
    //         System.arraycopy(matrix[i], 0, newMatrix[i], 0, seam[i]);
    //         // after the seam
    //         System.arraycopy(matrix[i], seam[i] + 1, newMatrix[i], seam[i], numCols - seam[i] - 1);
    //     }
    //     return newMatrix;
    // }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        // int height = pixelMatrix.length;
        // int width = pixelMatrix[0].length;

        if (invalidSeam(seam)) {
            throw new IllegalArgumentException("Invalid Seam");
        }

        if (seam.length != width()) {
            throw new IllegalArgumentException("Incorrect seam length");
        }
        if (height <= 1) {
            throw new IllegalArgumentException("Picture too small to remove seam");
        }
        pixelMatrix = transpose(pixelMatrix);
        pixelMatrix = horizCut(seam, pixelMatrix);
        pixelMatrix = transpose(pixelMatrix);
        // energyMatrix = horizCut(seam, energyMatrix);
        this.height = height - 1;

    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        // int height = pixelMatrix.length;
        // int width = pixelMatrix[0].length;

        if (invalidSeam(seam)) {
            throw new IllegalArgumentException("Invalid Seam");
        }
        if (seam.length != height) {
            throw new IllegalArgumentException("Incorrect seam length");
        }


        if (!isVertical) {
            pixelMatrix = transpose(pixelMatrix);
            // energyMatrix = transpose(energyMatrix);
            isVertical = true;
        }

        pixelMatrix = horizCut(seam, pixelMatrix);
        // energyMatrix = horizCut(seam, energyMatrix);
        this.width = width - 1;


    }

}