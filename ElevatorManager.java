//CS381 Final Group Project
//Elevator Simulation
//Name: Ayesha Manjura, Abid Khan, Khairul Fahim
//Team 11

import java.util.ArrayList;

public class ElevatorManager {
    ArrayList<Passenger> waitPassengers;
    ArrayList<Passenger> elevPassengers;
    int currFloor;              // 6 floors
    boolean traveling;          // When passenger on the move
    boolean direction;          // if 0 going down, if 1 going up
    

    final float timeMove = 6;   // Elevator goes from one floor to another every 6 seconds
    final float timeOn = 2;     // Passengers takes 2 seconds to get on elevator
    final float timeOff = 2;    // Passengers takes 2 seconds to get off elevator

    int Passengerid;

    public ElevatorManager(int i) {
        currFloor = 0;
        traveling = false;
        direction = true;
        waitPassengers = new ArrayList<Passenger>();
        elevPassengers = new ArrayList<Passenger>();

        Passengerid = i;
    }

    void traveling() {
        //elevator will go to floors between 0 and 5. 
        if (currFloor == 5) { 
            //Going down from the top floor
            direction = false;
        }
        if (currFloor == 0) { 
            //Going up from the bottom floor
            direction = true;
        } 
        //To prevent an on going loop of passenger waiting for elevator
        if (direction) currFloor++;
        else currFloor--;

        for (int i = 0; i < waitPassengers.size(); i++) {
            waitPassengers.get(i).nextTime += timeMove;
        }
    }

    void start() {
        for (int i = 0; i < waitPassengers.size(); i++) {
            waitPassengers.get(i).nextTime += timeOn;
        }
    }

    void end(Passenger p) {
        for (int i = 0; i < waitPassengers.size(); i++) {
            waitPassengers.get(i).nextTime += timeOff;
            waitPassengers.remove(p);
        }

        if (elevPassengers.isEmpty()) traveling = false;
    }

    void setDirection(int DestinationFloor) {
        if (currFloor < DestinationFloor) direction = true;
        else direction = false;
    }
}
