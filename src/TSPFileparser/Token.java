package TSPFileparser;

/**
 * This class allows us to take a line from a file and parse it
 * to extract the intended variables
 */
class Token {
    private int id;
    private Coordinates coordinates;

    public Token(int id, double x, double y) {
        this.id = id;
        this.coordinates = new Coordinates(x, y);
    }

    public int getId() {
        return this.id;
    }

    public Coordinates getCoordinates() {
        return this.coordinates;
    }

}