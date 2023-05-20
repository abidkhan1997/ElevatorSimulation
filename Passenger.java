//CS381 Final Group Project
//Elevator Simulation
//Name: Ayesha Manjura, Abid Khan, Khairul Fahim
//Team 11

import java.util.Random;
import java.util.Comparator;

public class Passenger {
    private int arrivalFloor;                   // Total 6 floors 0-5
    private int DestinationFloor;
    private boolean direction;                  // Passengers desired floor
    String intel;                               // What the passenger is doing
    boolean first;
    
    boolean stuck;
    ElevatorManager elevator;

    static float simulationTime = 0;            // Total Arrival time
    float arrivalTime;                      // Passengers arrival time
    float nextTime;                         // Time tracker for next passenger event

    int PassengerId;

    public Passenger(int i) {
        Random random = new Random();
        if (random.nextFloat() < 0.5) arrivalFloor = 0;
        else arrivalFloor = random.nextInt(5) + 1;

        if (arrivalFloor != 0 && random.nextFloat() < 0.5) DestinationFloor = 0;
        else {
            DestinationFloor = random.nextInt(5) + 1;
            while (DestinationFloor == arrivalFloor) DestinationFloor = random.nextInt(5) + 1;
        }

        direction = DestinationFloor - arrivalFloor > 0;
        intel = "waiting";
        first = true;
        stuck = false;
        elevator = null;
        arrivalTime = -6 * (float) Math.log(1 - random.nextFloat());
        simulationTime += arrivalTime;
        nextTime = simulationTime;

        PassengerId = i;
    }

    
    // Comparator for sorting people by time
    public static Comparator<Passenger> TimeComparator = new Comparator<Passenger>() {
        public int compare(Passenger a, Passenger b) {
            return Float.compare(a.nextTime, b.nextTime);
        }
    };

    // Comparator for sorting people by floor ascending
    public static Comparator<Passenger> FloorUpComparator = new Comparator<Passenger>() {
        public int compare(Passenger a, Passenger b) {
            return Integer.compare(a.getDestinationFloor(), b.getDestinationFloor());
        }
    };

    // Comparator for sorting people by floor descending
    public static Comparator<Passenger> FloorDownComparator = new Comparator<Passenger>() {
        public int compare(Passenger a, Passenger b) {
            return Integer.compare(b.getDestinationFloor(), a.getDestinationFloor());
        }
    };
   
    public int getPassengerId() {
        return PassengerId;
    }
    public ElevatorManager getElevator() {
        return elevator;
    }
    public int getDestinationFloor() {
        return DestinationFloor;
    }
    public boolean getDirection() {
        return direction;
    }
    public float getNextTime() {
        return nextTime;
    }
    public int getArrivalFloor() {
        return arrivalFloor;
    }
    public float getArrivalTime() {
        return arrivalTime;
    }
}