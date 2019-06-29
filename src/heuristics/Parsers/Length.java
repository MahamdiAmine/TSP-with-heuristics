package heuristics.Parsers;
import java.util.ArrayList;

public class Length {

    public static double routeLength1(ArrayList<Coords> cities) {

        //Calculate the length of a TSP route held in an ArrayList as a set of Points
        double result = 0; //Holds the route length
        Coords prev = cities.get(cities.size() - 1);

        //Set the previous city to the last city in the ArrayList as we need to measure the length of the entire loop
        for (Coords city : cities) {

            //Go through each city in turn
            result += city.getDistance(prev);

            //get distance from the previous city
//            prev = city;
            prev = new Coords(city.getId(), city.getX(), city.getY());

            //current city will be the previous city next time
        }
        return result;
    }
}
