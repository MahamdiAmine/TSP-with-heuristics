package TSPFileparser;

import java.io.*;
import java.util.*;

public class Parser {
    private ArrayList<Integer> id;
    private ArrayList<Coordinates> coordinates;

    public Parser(File file) {
        this.id = new ArrayList<>();
        this.coordinates = new ArrayList<>();
        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            String line;
            while ((line = in.readLine()) != null) {
                try {
                    Token tokens = getTokens(line);
                    addId(tokens.getId());
                    addPoint(tokens.getCoordinates());
                } catch (IllegalArgumentException e) {
                }
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Token getTokens(String line) throws IllegalArgumentException {
        StringTokenizer tokenizer = new StringTokenizer(line);
        try {
            int id = Integer.parseInt(tokenizer.nextToken());
            double x = Double.parseDouble(tokenizer.nextToken());
            double y = Double.parseDouble(tokenizer.nextToken());

            if (!tokenizer.hasMoreTokens()) {
                return new Token(id, x, y);
            }

        } catch (Exception e) {
        }
        throw new IllegalArgumentException();
    }

    private void addId(int id) {
        this.id.add(id);
    }

    private void addPoint(Coordinates pt) {
        this.coordinates.add(pt);
    }

    public ArrayList<Integer> getIds() {
        return this.id;
    }

    public ArrayList<Coordinates> getCoordinates() {
        return this.coordinates;
    }

    public double[][] getAdjacencyMatrix() {
        int dimension = this.id.size();
        double[][] adjacencyMatrix = new double[dimension][dimension];

        for (int i = 0; i < dimension - 1; ++i) {
            for (int j = i + 1; j < dimension; ++j) {
                Coordinates coordinates1 = this.coordinates.get(i);
                Coordinates coordinates2 = this.coordinates.get(j);

                double X = coordinates2.getX() - coordinates1.getX();
                double Y = coordinates2.getY() - coordinates1.getY();

                adjacencyMatrix[i][j] = Math.sqrt(Math.pow(X, 2) + Math.pow(Y, 2));

                adjacencyMatrix[j][i] = adjacencyMatrix[i][j];
            }
        }
        return adjacencyMatrix;
    }

    public void PrintAdjMatrixToFile(File file) throws FileNotFoundException {
        PrintWriter out = new PrintWriter(file);
        int dimension = this.id.size();
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                out.print(getAdjacencyMatrix()[i][j]);
            }
            out.println();
        }
        out.close();
    }

}