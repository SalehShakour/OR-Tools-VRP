package org.vrp.distandeMatrix;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        int size = 5;
        int maxDistance = 50;
        long[][] distanceMatrix = DistanceMatrix.createDistanceMatrix(size, maxDistance);

        System.out.println("Distance Matrix:");
        DistanceMatrix.printDistanceMatrix(distanceMatrix);

        SwingUtilities.invokeLater(() -> new JGraphXVisualizer(distanceMatrix));
    }
}
