
/**
 * 
 * @author rtoussaint
 * @author Lyndsay Kerwin
 * @author Ryan Fishel
 * @author Carlos Reyes
 * This class takes care of the functionality that is associated with Riders.  Each rider knows what floor it is
 * on, which floor it is on, whether or not it's on an elevator, and its building and event barrier.  In order to
 * get on an elevator, the rider has to signal but calling buttonUp or buttonDown.
 */

public class Rider extends Thread{

	private int currentFloor;
	private int destFloor;
	private boolean onElevator;
	private boolean goingUp;
	private EventBarrier myBarrier;
	private Building myBuilding;
	private BadlyBehaved carlos;
	private int myID;

	public Rider(Building building, int presentFloor, int destination, EventBarrier eventBarrier, int id){
		this.myBuilding = building;
		this.myBarrier = eventBarrier;
		currentFloor = presentFloor;
		destFloor = destination;
		setDirection();
		carlos = BadlyBehaved.WELL_BEHAVED;
		this.myID = id;

	}

	public Rider(Building building, int presentFloor, EventBarrier eventBarrier, BadlyBehaved dumbRider, int id) {
		this.myBuilding = building;
		this.myBarrier = eventBarrier;
		currentFloor = presentFloor;
		if(currentFloor == 0) {
			destFloor = myBuilding.numFloors+2;
		}
		else{
			destFloor = -2;
		}
		setDirection();
		carlos = dumbRider;
		this.myID = id;
	}

	/**
	 * Signal to the program that this rider wants to take an elevator up.  It will wait() until the Elevator wakes
	 * it up, once it arrives on its floor.
	 */
	public void buttonUp(){
		myBuilding.CallUp(myBarrier);
		myBarrier.arrive();


		for(Elevator elevator : myBuilding.getElevatorController().getElevators()){
			if(carlos == BadlyBehaved.PRESS_BUTTON_DONT_GET_ON) {
				myBuilding.removeUpBarrier(currentFloor);
				myBarrier.complete();
			}
			else{
				if(currentFloor == elevator.getCurrentFloor() && elevator.getMaxOccupancy() > elevator.getNumPassengers() && elevator.getDirectionStatus() != Direction.DOWN){

					if(destFloor == myBuilding.numFloors+2) {
						destFloor = 1;
					}
					EventBarrier onBarrier = new EventBarrier();
					onBarrier.setFloor(destFloor);
					myBuilding.removeUpBarrier(currentFloor);
					myBuilding.addOnBarriers(onBarrier);

					elevator.addPassenger(this);
					onElevator = true;
					myBarrier.complete();
					updateEventBarrier(onBarrier);

				}
				else if (currentFloor == elevator.getCurrentFloor() && elevator.getMaxOccupancy() <= elevator.getNumPassengers()  && elevator.getDirectionStatus() != Direction.DOWN){
					myBarrier.complete();
					this.buttonUp();
				}
			}

		}
	}

	/**
	 * Signal to the program that this rider wants to take an elevator down.  It will wait() until the Elevator wakes
	 * it up, once it arrives on its floor.
	 */
	public void buttonDown(){
		myBuilding.CallDown(myBarrier);
		myBarrier.arrive();

		//TODO: Ryan Thought: Do we need this or can we use elevator id??

		for(Elevator elevator : myBuilding.getElevatorController().getElevators()){
			if(carlos == BadlyBehaved.PRESS_BUTTON_DONT_GET_ON) {
				myBuilding.removeDownBarrier(currentFloor);
				myBarrier.complete();
			}
			else {
				if(currentFloor == elevator.getCurrentFloor() && elevator.getMaxOccupancy() > elevator.getNumPassengers() && elevator.getDirectionStatus() != Direction.UP){


					if(destFloor == -2) {
						destFloor = 0;
					}
					EventBarrier onBarrier = new EventBarrier();
					onBarrier.setFloor(destFloor);
					myBuilding.removeDownBarrier(currentFloor);
					myBuilding.addOnBarriers(onBarrier);


					elevator.addPassenger(this);
					onElevator = true;
					myBarrier.complete();
					updateEventBarrier(onBarrier);
				}
				else if (currentFloor == elevator.getCurrentFloor() && elevator.getMaxOccupancy() < elevator.getNumPassengers()  && elevator.getDirectionStatus() != Direction.UP){
					myBarrier.complete();
					this.buttonDown();
				}
			}
		}

	}

	/**
	 * Get rider id
	 * @return rider id
	 */
	public int getRiderId(){
		return (int)getId();
	}

	/**
	 * Update the rider's event barrier based on the floor that it is on.  This method is called when the rider
	 * changes floors and needs to update.
	 * @param newBarrier
	 */
	public void updateEventBarrier(EventBarrier newBarrier){
		myBarrier = newBarrier;
		currentFloor = myBarrier.getFloor();
	}

	/**
	 * The destination floor
	 * @return the floor the rider wants to go to
	 */
	public int getDestinationFloor(){
		return destFloor;
	}


	public void setDestinationFloor(int level){
		//TODO: implement this method so that a rider can change destination once it rides the elevator
		destFloor = level;
	}

	public int getCurrentFloor() {
		return currentFloor;
	}

	@Override
	public void run() {
		if(goingUp) {
			buttonUp();
		}
		else{
			buttonDown();
		}
	}

	private void setDirection() {
		// TODO Auto-generated method stub
		if(currentFloor > destFloor){
			goingUp = false;
		}
		else {
			goingUp = true;
		}
	}

	public void setCurrentFloor(int currentFloor2) {
		currentFloor = 	currentFloor2;	
	}

	public EventBarrier getEventBarrier() {
		return this.myBarrier;
	}

	public boolean getIsGoingUp() {
		return goingUp;
	}

	public int getMyID() {
		return myID;
	}

	public void setMyID(int myID) {
		this.myID = myID;
	}


}
