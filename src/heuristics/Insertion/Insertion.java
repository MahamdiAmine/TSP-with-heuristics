package heuristics.Insertion;

import java.util.ArrayList;

public class Insertion {
    int[] tour;
    double Lenght_path;
    double[][] distance;
    int numberOfTown;

    public Insertion(double[][] distance) {
        this.distance = distance;
        numberOfTown = distance[0].length;
        insertion();

    }

    public int[] getTour() {
        return tour;
    }

    public double getLength_path() {
        return Lenght_path;
    }

    private void insertion() {
        // The V\V' and V' set
        ArrayList<Integer> v = new ArrayList<>();
        ArrayList<Integer> vprim = new ArrayList<>();
        // The set E(T)
        ArrayList<Arc> eoft = new ArrayList<>();
        // initialisation of intermediate values
        int i;
        Double localmin;
        // initialise the set V' to all available elements
        for (i = 0; i < numberOfTown; i++) {
            vprim.add(i);
        }
        vprim.remove((Integer) 0);
        v.add(0);
        // Step2 //
        // search the v' that satisfy d(v, v') = min{d(v, z) : z ∈ V '}.
        int townIndex = -1;
        localmin = Double.MAX_VALUE;
        for (i = 1; i < numberOfTown; i++) {

            if (vprim.indexOf(i) != -1 && (distance[0][i] < localmin)) {
                townIndex = i;
                localmin = distance[0][i];
            }
        }
        Arc firstArc = new Arc(0, townIndex);
        Arc secondArc = new Arc(townIndex, 0);
        eoft.add(firstArc);
        eoft.add(secondArc);
        vprim.remove((Integer) townIndex);
        v.add(townIndex);
        int townIndexOne = -1, townIndexTwo = -1;
        int[] returnValue = new int[numberOfTown];
        // Step 4 //
        // While V' is not empty //
        while (vprim.size() != 0) {
            localmin = Double.MAX_VALUE;
            // Step 5 //
            // Search a node who is the nearest from one node of v (the node in the partial tour) //
            for (Integer e : v
            ) {
                for (Integer f : vprim
                ) {
                    if (distance[e][f] < localmin) {
                        localmin = distance[e][f];
                        townIndexOne = e;
                        townIndexTwo = f;
                    }
                }
            }

            localmin = Double.MAX_VALUE;
            int townIndexThree = -1;
            Arc toDelete = null;

            for (Arc e : eoft
            ) {
                if (e.getSide1() == townIndexOne && (distance[townIndexOne][townIndexTwo] + distance[townIndexTwo][e.getSide2()] - distance[townIndexOne][e.getSide2()] < localmin)) {
                    localmin = distance[townIndexOne][townIndexTwo] + distance[townIndexTwo][e.getSide2()] - distance[townIndexOne][e.getSide2()];
                    townIndexThree = e.getSide2();
                    toDelete = e;
                }
            }
            if (toDelete != null) eoft.remove(toDelete);
            // Insert the two edge after inserting the node between the two nodes after deleting their edge //
            if (townIndexOne != -1 && townIndexThree != -1) {
                Arc tempArc = new Arc(townIndexOne, townIndexTwo);
                eoft.add(tempArc);
                tempArc = new Arc(townIndexTwo, townIndexThree);
                eoft.add(tempArc);
                vprim.remove((Integer) townIndexTwo);
                v.add(townIndexTwo);
            }
            townIndexOne = -1;
            townIndexTwo = -1;
            // Loop on step 4 while the V' set is not empty //
        }
        // FInd the complete path and calculater the distance //
        int ll = 0;
        while (ll < eoft.size() && (eoft.get(ll).getSide1() != 0)) {
            ll++;
        }
        double totaldistance = distance[eoft.get(ll).getSide1()][eoft.get(ll).getSide2()];
        returnValue[0] = eoft.get(ll).getSide1();
        returnValue[1] = eoft.get(ll).getSide2();
        for (i = 2; i < numberOfTown; i++) {
            int l = 0;
            while (l < eoft.size() && (eoft.get(l).getSide1() != returnValue[i - 1])) {
                l++;
            }
            totaldistance += distance[eoft.get(l).getSide1()][eoft.get(l).getSide2()];
            returnValue[i] = eoft.get(l).getSide2();
        }
        totaldistance += distance[0][returnValue[numberOfTown - 1]];
        this.Lenght_path = totaldistance;
        this.tour = returnValue;
    }

    @Override
    public String toString() {
        String finalPath = "";
        for (int counter = 0; counter < tour.length; counter++) {
            finalPath += tour[counter] + " => ";
        }
        finalPath += tour[0];
        return finalPath;
    }
}
