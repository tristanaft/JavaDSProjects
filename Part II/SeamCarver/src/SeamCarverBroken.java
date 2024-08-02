import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdOut;



public class SeamCarverBroken {

    private Picture pic;
    // private double[][] energyMatrix;

    // store RGB values in pixelMatrix and seams delete elements here too
    private int[][] pixelMatrix;

    // col version of edgeTo in Dijkstra alg
    private int[][] colTo;

    // I only want to transpose when I need to,
    // so I need to keep track of what the orientation is
    private boolean isVertical = true;

    private boolean horizOverride = false;


    // create a seam carver object based on the given picture
    public SeamCarverBroken(Picture picture) {
        if (picture == null) {
            throw new IllegalArgumentException("Picture is null");
        }
        pic = picture;

        int width = picture.width();
        int height = picture.height();
        pixelMatrix = new int[height][width];
        colTo = new int[height][width];


        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                pixelMatrix[i][j] = picture.getRGB(j, i);
            }
        }


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
        int height = pixelMatrix.length;
        int width = pixelMatrix[0].length;

        pic = new Picture(width, height);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                pic.setRGB(j, i, pixelMatrix[i][j]);
            }
        }
        return pic;

    }

    // width of current picture
    public int width() {
        if (isVertical) {
            return pixelMatrix[0].length;
        } else {
            return pixelMatrix.length;
        }

    }

    // height of current picture
    public int height() {
        if (isVertical) {
            return pixelMatrix.length;
        } else {
            return pixelMatrix[0].length;
        }
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

        return new int[]{red, green, blue};
    }

    // I think this is a way more efficient way of calculating the energy since we
    // already are saving the 32 bit color data...
    private double energy32(int x, int y) {
        if (x >= pixelMatrix[0].length - 1 || y >= pixelMatrix.length - 1) {
            return 1000;
        }
        if (x == 0 || y == 0) {
            return 1000;
        }

        int[] left = rgbComponents(pixelMatrix[y][x - 1]);
        int[] right = rgbComponents(pixelMatrix[y][x + 1]);
        int[] above = rgbComponents(pixelMatrix[y + 1][x]);
        int[] below = rgbComponents(pixelMatrix[y - 1][x]);

        return Math.sqrt(colorDiff32(left, right) + colorDiff32(above, below));
    }

    private double[][] calcEnergyMatrix32() {
        int height = pixelMatrix.length;
        int width = pixelMatrix[0].length;
        double[][] energyMatrix = new double[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                energyMatrix[i][j] = energy32(j, i);
            }
        }
        return energyMatrix;
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
    private void relaxStep(int i, int j1, int j2, double[][] dMat, double[][] eMat) {
        // StdOut.printf("relaxStep i: %d, j1: %d, j2: %d\n", i, j1, j2);
        if (dMat[i + 1][j2] > (dMat[i][j1] + eMat[i + 1][j2])) {
            dMat[i + 1][j2] = (dMat[i][j1] + eMat[i + 1][j2]);
            colTo[i + 1][j2] = j1;
        }

    }

    private void relaxVertex(int i, int j, double[][] dMat, double[][] eMat) {
        // StdOut.printf("in relaxVertex, height: %d width: %d\n", height(), width());
        // Ok, we assume that the pixel is connected to the 3 pixels beneath it
        // so (i-1, j-1), (i-1, j) and (i-1, j+1) but will need to check these are not OOB
        // if (i >= height() - 1) {
        //     throw new IllegalArgumentException("Attempting to relax past end of matrix");
        // }

        if (j > 0) {
            relaxStep(i, j, j - 1, dMat, eMat);
        }

        relaxStep(i, j, j, dMat, eMat);

        if (j < width() - 1) {
            relaxStep(i, j, j + 1, dMat, eMat);
        }
    }

    private int[][] transpose(int[][] matrix) {
        int width = matrix.length;
        int height = matrix[0].length;
        int[][] matT = new int[height][width];
        int i, j;
        for (i = 0; i < width; i++) {
            for (j = 0; j < height; j++) {
                matT[j][i] = matrix[i][j];
            }
        }

        return matT;
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        // if not transposed, then transpose,
        // then get the seam with vertical method...
        this.horizOverride = true;
        if (isVertical) {
            this.pixelMatrix = transpose(pixelMatrix);
            this.colTo = transpose(colTo);
            isVertical = false;
        }

        return findVerticalSeam();


    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        // do need to check current picture orientation and fix if necessary
        // have another boolean to check if we are trying to find a horizontal seam
        if (!isVertical && !horizOverride) {
            this.pixelMatrix = transpose(pixelMatrix);
            this.colTo = transpose(colTo);
            this.pixelMatrix = transpose(pixelMatrix);
            isVertical = true;
        }
        int height = pixelMatrix.length;
        int width = pixelMatrix[0].length;
        // this.energyMatrix = new double[height][width];
        double[][] energyMatrix = calcEnergyMatrix32();
        // StdOut.println();
        // for (double[] row : energyMatrix) {
        //     for (double item : row) {
        //         StdOut.printf("%7.1f ", item);
        //     }
        //     StdOut.println();
        // }
        // StdOut.printf("pixelMatrix dims height: %d width: %d\n", height, width);

        int[] verticalSeam = new int[height];
        double[][] distTo = new double[height][width];
        //// StdOut.printf("distTo dims height: %d width: %d\n", distTo.length, distTo[0].length);
        // initialize all values to infinity in distTo
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                distTo[i][j] = Double.POSITIVE_INFINITY;
            }
        }

        // init distTo distances to be infinite
        for (int j = 0; j < width; j++) {
            distTo[0][j] = energy(0, j);
        }

        // Go from top to bottom relaxing the matrices
        // Note: can't relax the bottom row since it has no connections below it
        // StdOut.printf("height: %d, width: %d\n", height, width);
        for (int i = 0; i < height - 1; i++) {
            for (int j = 0; j < width - 1; j++) {
                //// StdOut.print(i + " " + j);
                relaxVertex(i, j, distTo, energyMatrix);
            }
        }

        // find shortest distance in bottom row,
        double lowestDistance = Double.POSITIVE_INFINITY;
        for (int j = 0; j < width; j++) {
            // StdOut.print(distTo[height - 1][j] + " ");
            if (lowestDistance > distTo[height - 1][j]) {
                lowestDistance = distTo[height - 1][j];
                verticalSeam[height - 1] = j;
            }
        }
        // StdOut.println(lowestDistance);
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
        int height = matrix.length;
        int width = matrix[0].length;

        // StdOut.printf("horizCut dims height: %d, width: %d\n", height, width);

        if (width == 1) {
            throw new IllegalArgumentException("Too small to perform desired cut");
        }

        int[][] newMatrix = new int[height][width - 1];
        StdOut.printf("newMatrix dims height: %d, width: %d\n", newMatrix.length, newMatrix[0].length);

        // int[] removedEnergies = new int[height];
        for (int i = 0; i < height; i++) {
            // StdOut.println(matrix[i]);
            // before the seam
            // StdOut.printf("i: %d, matrix[i].length: %d newMatrix[i].length: %d\n", i, matrix[i].length, newMatrix[i].length);
            System.arraycopy(matrix[i], 0, newMatrix[i], 0, seam[i]);

            // after the seam
            System.arraycopy(matrix[i], seam[i] + 1, newMatrix[i], seam[i], width - seam[i] - 1);


        }

        return newMatrix;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        int height = pixelMatrix.length;
        // int width = pixelMatrix[0].length;

        if (invalidSeam(seam)) {
            throw new IllegalArgumentException("Invalid Seam");
        }


        if (isVertical) {
            pixelMatrix = transpose(pixelMatrix);
            colTo = transpose(colTo);
            isVertical = true;
        }

        if (seam.length != width()) {
            throw new IllegalArgumentException("Incorrect seam length");
        }
        if (height <= 1) {
            throw new IllegalArgumentException("Picture too small to remove seam");
        }

        pixelMatrix = horizCut(seam, pixelMatrix);

    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        int height = pixelMatrix.length;
        // int width = pixelMatrix[0].length;

        if (invalidSeam(seam)) {
            throw new IllegalArgumentException("Invalid Seam");
        }
        if (seam.length != height) {
            throw new IllegalArgumentException("Incorrect seam length");
        }


        if (!isVertical) {
            pixelMatrix = transpose(pixelMatrix);
            colTo = transpose(colTo);
            isVertical = false;
        }

        pixelMatrix = horizCut(seam, pixelMatrix);


    }

}