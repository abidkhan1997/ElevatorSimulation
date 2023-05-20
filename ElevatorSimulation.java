//CS381 Final Group Project
//Elevator Simulation
//Name: Ayesha Manjura, Abid Khan, Khairul Fahim
//Team 11

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
class ElevatorSimulation {

    public static ElevatorManager[] elevators = new ElevatorManager[4];
    public static ArrayList<Passenger> fEvent = new ArrayList<Passenger>(); // future events list
    //collecting simulation data from passengers
    static float[] waitingTime = new float[50]; // passengers waiting time for elevator
    static float[] travelingTime = new float[50]; //  passengers traveling in the elevator
    static float[] arrivalTime = new float[50]; //Passenger total time to the arrival destiantion
    public static void main(final String[] args) {
        // initializes elevators
        for (int i = 0; i < 4; i++) {
            elevators[i] = new ElevatorManager(i);
        }
        Random rand = new Random();

        int rand_int = rand.nextInt(50);

        // Random number of passenger getting on the floor
        for (int i = 1; i <rand_int ; i++) {
            fEvent.add(new Passenger(i));
            //all times initialized to zero
            waitingTime[i] = 0.0f;
            travelingTime[i] = 0.0f;
            arrivalTime[i] = 0.0f;
        }

        Passenger passengers;

        while (!fEvent.isEmpty()) { 
            // Future event list is not empty, Passengers next event
            passengers = fEvent.get(0);

            // Passenger is waiting
            if (passengers.intel == "waiting") {
                waitingTime[passengers.getPassengerId()] += passengers.nextTime;
                // Elevator is not available
                if (passengers.getElevator() == null) {
                    // call an elevator
                    if (passengers.first) {
                        System.out.println("Passengers " + passengers.PassengerId + " arrived on floor " + passengers.getArrivalFloor() + " , going to " + passengers.getDestinationFloor());
                        passengers.first = false;
                    }
                    ElevatorManager Elev = dispatch(passengers);

                    // if no available elevators
                    if (Elev == null) {
                        // try again next time
                        System.out.println("Waiting Time: " + passengers.nextTime +"\n") ;
                        System.out.println("Passengers " + passengers.PassengerId + " cannot call any elevators");
                        passengers.stuck = true;
                        unStuck(passengers);
                        
                    } else {
                        // send elevator to passenger
                        Elev.setDirection(passengers.getArrivalFloor());
                        Elev.traveling = true;
                        passengers.elevator = Elev;
                        Elev.waitPassengers.add(passengers);
                    }
                }
                // If there is an elevator available
                if (passengers.elevator != null) {
                    // While passengers and elevator is on the same floor
                    if (passengers.elevator.currFloor == passengers.getArrivalFloor()) {
                        passengers.intel = "entering";
                    } else {
                        System.out.println("Entering Time: " + passengers.nextTime +"\n") ;
                        System.out.println("Passengers " + passengers.PassengerId + " is waiting for Elevator " + passengers.elevator.Passengerid + "(CurrentFloor " + passengers.elevator.currFloor + " - Going to " + passengers.getArrivalFloor() + ")");
                        passengers.elevator.traveling();
                    }
                }
            }

            // Passengers getting in to elevator
            else if (passengers.intel == "entering") {
                System.out.println("Entering Time: " + passengers.nextTime +"\n") ;
                System.out.println("Passengers " + passengers.PassengerId + " is entering Elevator " + passengers.elevator.Passengerid);
                passengers.elevator.start();
                passengers.elevator.elevPassengers.add(passengers);
                passengers.elevator.setDirection(passengers.getDestinationFloor());

                // Finds the nearest elevator for passengers
                if (passengers.elevator.direction)
                    Collections.sort(passengers.elevator.elevPassengers, Passenger.FloorUpComparator);
                else Collections.sort(passengers.elevator.elevPassengers, Passenger.FloorDownComparator);

                passengers.intel = "traveling";
            }

            // Passengers traveling the elevator
            else if (passengers.intel == "traveling") {
                travelingTime[passengers.getPassengerId()] = passengers.nextTime;
                System.out.println("Traveling Time: " + passengers.nextTime +"\n") ;
                System.out.println("Passengers " + passengers.PassengerId + " is traveling Elevator " + passengers.elevator.Passengerid + "(CurrentFloor "  + passengers.elevator.currFloor + " - Going to " + passengers.getDestinationFloor() + ")");
                passengers.elevator.traveling();

                //Elevator arrives at the destination floor
                while (!passengers.elevator.elevPassengers.isEmpty() && passengers.elevator.elevPassengers.get(0).getDestinationFloor() == passengers.elevator.currFloor) {
                    passengers.elevator.elevPassengers.get(0).intel = "departuring";
                    passengers.elevator.elevPassengers.remove(0);
                }
            }

            // Passengers getting off the elevator
            else if (passengers.intel == "departuring") {
                arrivalTime[passengers.getPassengerId()] = waitingTime[passengers.getPassengerId()] + travelingTime[passengers.getPassengerId()]; // collecting system time of passengers
                System.out.println("Departuring Time: " + passengers.nextTime +"\n") ;
                System.out.println("Passengers " + passengers.PassengerId + " is departuring");
                passengers.elevator.end(passengers);
                System.out.println("Departuring Time: " + passengers.nextTime +"\n") ;
                System.out.println("Passengers " + passengers.PassengerId + " left from floor " + passengers.getArrivalFloor() + " to " + passengers.getDestinationFloor());

                fEvent.remove(0);
            }

            // Order of events are sorted
            Collections.sort(fEvent, Passenger.TimeComparator);

        }

        // Calculating data to get the output
        float totalWaitTime = 0.0f;
        float totalTravelTime = 0.0f;
        float totalArriveTime = 0.0f;

        for (int i = 0; i < 20; i++) {
            totalWaitTime += waitingTime[i];
            totalTravelTime += travelingTime[i];
            totalArriveTime += arrivalTime[i];
        }

        System.out.println("\nA Passengers waited on elevator on average " + totalWaitTime / 20);
        System.out.println("A Passengers traveled on elevator on average " + totalTravelTime / 20);
        System.out.println("A Passengers stayed on elevator on average " + totalArriveTime / 20);
    }

    // Finds the nearest elevator for passengers
    public static ElevatorManager dispatch(Passenger p) {
        ElevatorManager closestElevator = null;
        int minDistance = 6;

        for (int i = 0; i < 4; i++) {
            // Elevator is going the same direction as the passenger
            if (elevators[i].traveling && elevators[i].currFloor == p.getArrivalFloor() && elevators[i].direction == p.getDirection()) {
                closestElevator = elevators[i];
                minDistance = 0;
            }
            // Elevator is not moving and is closest to desired floor
            else if (!elevators[i].traveling) {
                if (Math.abs(elevators[i].currFloor - p.getArrivalFloor()) < minDistance) {
                    closestElevator = elevators[i];
                    minDistance = Math.abs(elevators[i].currFloor - p.getArrivalFloor());
                }
            }
        }

        return closestElevator;
    }

    //Stuck Passenger is pushed back on the line to try again later
    public static void unStuck(Passenger stuckP) {
        int i = 0;
        Passenger passengers = fEvent.get(i);
        while (passengers.stuck && i < fEvent.size() - 1) {
            i++;
            passengers = fEvent.get(i);
        }

        stuckP.nextTime = fEvent.get(i).nextTime;
        shiftDown(stuckP, i);
    }

    public static void shiftDown(Passenger p, int pos) {
        for (int i = 0; i < pos; i++)
            fEvent.set(i, fEvent.get(i + 1));
        fEvent.set(pos, p);
    }

    
}