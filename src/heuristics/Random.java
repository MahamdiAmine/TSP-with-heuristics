package heuristics;

import java.util.*;

public class Random {
    private final List<Integer> randomTour;
    private double distance;
    private int size;
    private double[][] distanceTable;

    public Random(double[][] adj) {
        randomTour = new ArrayList<>();
        this.distanceTable = adj;
        this.size = distanceTable[0].length;
        for (int i = 0; i < size; i++) {
            randomTour.add(i);
        }
        Collections.shuffle(randomTour);
        this.distance = calculateDistance();

    }

    public List<Integer> getRandomTour() {
        return randomTour;
    }

    public double calculateDistance() {
        double sum = 0;

        for (int i = 0; i < this.size; i++) {
            int a = randomTour.get(i);
            int b = randomTour.get((i + 1) % size);
            sum += distanceTable[a][b];
        }
        return sum;
    }

    public double getDistance() {
        return distance;
    }

    public String toString() {
        String tour = "";
        for (Integer node : randomTour) {
            tour += node.toString() + " => ";
        }
        tour += randomTour.get(0);
        return tour;
    }
}
