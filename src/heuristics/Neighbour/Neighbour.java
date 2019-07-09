package heuristics.Neighbour;/*
run a nearest neighbour on the passed in ArrayList
return the nearest neighbour result.
 */

import heuristics.Parsers.Coords;

import java.util.ArrayList;

public class Neighbour {
    private ArrayList<Coords> tour;

    public Neighbour(ArrayList<Coords> cities) {
        tour = nearest1(cities);
    }

    public ArrayList<Coords> getTour() {
        return tour;
    }


    public static ArrayList<Coords> nearest1(ArrayList<Coords> cities) {

        ArrayList<Coords> result = new ArrayList<>(); //holds final result.
        Coords currentCity = cities.remove(0); //set current city to first array item.
        Coords closestCity = cities.get(0); //set closest city to new first array item.
        Coords possible; //for holding possible city.
        double dist; //hold current node distance.

        result.add(currentCity);

        //outside loop to iterate through array
        while (cities.size() > 0) {

            dist = Double.MAX_VALUE; //reset dist to max.

            //inner loop checks distance between current city and possible.
            for (int count = 0; count < cities.size(); count++) {
                possible = cities.get(count);
                if (currentCity.getDistance(possible) < dist) {
                    dist = currentCity.getDistance(possible);
                    closestCity = possible;
                }
            }
            /*
            once inner loop finds closest node
            set current city to closest, remove closest from cities
            and add current city to result.
             */
            currentCity = new Coords(closestCity.getId(), closestCity.getX(), closestCity.getY());
            cities.remove(closestCity);
            result.add(currentCity);
        }
        return result;
    }

    @Override
    public String toString() {
        String finalPath = "";
        for (int counter = 0; counter < tour.size(); counter++) {
            finalPath += tour.get(counter).getId() + " => ";
        }
        finalPath += tour.get(0).getId();
        return finalPath;
    }
}

