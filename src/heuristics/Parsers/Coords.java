package heuristics.Parsers;

/**
 * This class represents a cartesian 2D point using its x and y coordinates
 */
public class Coords {
    private double x, y;
    private int id;

    public Coords(int id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public int getId() {
        return this.id;
    }

    public double getDistance(Coords c2) {
        double X = c2.getX() - this.getX();
        double Y = c2.getY() - this.getY();

        return Math.sqrt(Math.pow(X, 2) + Math.pow(Y, 2));
    }


}