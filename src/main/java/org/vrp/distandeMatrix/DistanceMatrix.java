package org.vrp.distandeMatrix;
import java.util.Random;

public class DistanceMatrix {

    public static long[][] createDistanceMatrix(int size, int maxDistance) {
        long[][] distanceMatrix = new long[size][size];
        Random random = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = i; j < size; j++) {
                if (i == j) {
                    distanceMatrix[i][j] = 0;
                } else {
                    long distance = random.nextInt(maxDistance) + 1;
                    distanceMatrix[i][j] = distance;
                    distanceMatrix[j][i] = distance;
                }
            }
        }

        return distanceMatrix;
    }

    public static void printDistanceMatrix(long[][] distanceMatrix) {
        for (long[] row : distanceMatrix) {
            for (long value : row) {
                System.out.print(value + " ");
            }
            System.out.println();
        }
    }
}
