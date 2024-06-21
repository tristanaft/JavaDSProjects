import edu.princeton.cs.algs4.StdOut;

public class CircularSuffixArray {

    private final String inputString;
    private final int length;
    private final int[] index;


    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) throw new IllegalArgumentException();
        this.inputString = s;
        this.length = s.length();
        this.index = new int[length];
        for (int i = 0; i < length; i++) {
            index[i] = i;
        }

        sort(0, length - 1, 0);


    }

    ;

    // A utility function to swap two elements
    // private static void swap(int[] arr, int i, int j) {
    //     int temp = arr[i];
    //     arr[i] = arr[j];
    //     arr[j] = temp;
    // }

    private void swap(int i, int j) {
        int temp = index[i];
        index[i] = index[j];
        index[j] = temp;
    }

    // unit testing (required)
    public static void main(String[] args) {
        String test = "ABRACADABRA!";
        CircularSuffixArray csa = new CircularSuffixArray(test);
        for (int i = 0; i < test.length(); i++) {
            StdOut.println(i + " - " + csa.index(i));
        }


    }

    // length of s
    public int length() {
        return length;
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || i >= length) {
            throw new IllegalArgumentException();
        }
        return index[i];
    }

    private int charAt(int idx, int d) {
        if (index[idx] + d < length)
            return inputString.charAt(index[idx] + d);
        return inputString.charAt(index[idx] + d - length);
    }

    // 3 way string quicksort based on lecture
    private void sort(int left, int right, int idx) {
        if (right <= left || idx > length) return;
        int lt = left;
        int rt = right;
        int leftChar = charAt(left, idx);
        int i = left + 1;
        while (i <= rt) {
            int rightChar = charAt(i, idx);
            // had to switch this to front... not sure why
            if(rightChar == leftChar)
                i++;
            else if (rightChar < leftChar)
                swap(lt++, i++);
            else
                swap(i, rt--);
        }
        sort(left, lt - 1, idx);
        sort(lt, rt, idx + 1);
        sort(rt + 1, right, idx);
    }





}