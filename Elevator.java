import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.List;

/**
 * 
 * @author rtoussaint
 * @author Lyndsay Kerwin
 *
 */

public class Elevator extends AbstractElevator{

	private boolean doorsOpen;
	private boolean atFloor; //at the current floor
	private Direction direction;

	private TreeSet<Integer> upRequests;
	private TreeSet<Integer> downRequests;
	
	private int numOfRiders;
	
	List<Rider> passengers;
	List<Rider> peopleBoarding;
	
	private int currentFloor;
	
	private int destinationFloor;
	
	private ElevatorController controller;
	
	
	public Elevator(int numFloors, int elevatorId, int maxOccupancyThreshold) {
		super(numFloors, elevatorId, maxOccupancyThreshold);

			
	}

	@Override
	public void OpenDoors() {
		//Not used
		//people get out and new members get in --> close doors
	}

	@Override
	public void ClosedDoors() {
		// TODO Auto-generated method stub
		//choose floor to go to
		//loop through people in elevator to see where to go to
		int val = numFloors;
		for(Rider currentRider : passengers){
			if(direction == Direction.UP && currentRider.getDestinationFloor() < val){
				val = currentRider.getDestinationFloor();
			}
			else if(direction == Direction.DOWN){
				if(val == numFloors){
					val = 0;
				}
				
				if(currentRider.getDestinationFloor() > val){
					val = currentRider.getDestinationFloor();
				}
			}	
		}
		destinationFloor = val;
		//TODO: go to this Floor
		
	}

	/**
	 * TODO: Check race conditions (doors can't open in transit, rider can't
	 * leave during transit
	 */
	@Override
	public void VisitFloor(int floor) {
		//set currentFloor to the correctValue
		atFloor = true;
		currentFloor = floor;
		//wake everyone up on the elevator
		for(EventBarrier x : controller.getBuilding().getOnBarriers()){
			if(floor == x.getFloor()){
				x.raise();
				subtractRiders();
			}
		}
				
		//raise the event so riders know to wake up
		if(direction == Direction.UP){
			for(EventBarrier curBarrier : controller.getBuilding().getUpBarriers()){
				if(curBarrier.getFloor() == floor){
					//tell that floor to wake up and get on the elevator
					curBarrier.raise();
					//TODO: add riders to list for the elevator to know who to add.
					
					
				}
			}
		}
		if(direction == Direction.DOWN){
			for(EventBarrier curBarrier : controller.getBuilding().getDownBarriers()){
				if(curBarrier.getFloor() == floor){
					//tell that floor to wake up and get on the elevator
					curBarrier.raise();
					
				}
			}
		}
			
		
		ClosedDoors();
	}

	@Override
	public boolean Enter() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void Exit() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void RequestFloor(int floor) {
		// TODO Auto-generated method stub
		
	}
	
	
	private void subtractRiders(){
		for(Rider x : passengers){
			if(x.getDestinationFloor() == currentFloor){
				numOfRiders--;
				x.setCurrentFloor(currentFloor);
				controller.getBuilding().getOnBarriers().remove(x);
				/*now this 'rider' is off the elevator but just staying on the floor -- it will need to 'callUp' 
				 * or 'callDown' in order to get back on.
				 */
			}
		}
	}
	
	public Direction getDirectionStatus(){
		return direction;
	}
	
	public int getCurrentFloor(){
		return currentFloor;
	}

	public void setDestinationFloor(int floor){
		destinationFloor = floor;
	}
	
	//TODO: change elevator direction
	public void calculateDirection(HashSet<EventBarrier> up, HashSet<EventBarrier> down){
		if(up.isEmpty() && down.isEmpty()){
			direction = Direction.STAGNANT;
		}
		else if(direction == Direction.UP && up.isEmpty()){
			direction = Direction.DOWN;
		}
		else if(direction == Direction.DOWN && down.isEmpty()){
			direction = Direction.UP;
		}
		
	}
	
	public int getMaxOccupancy(){
		return maxOccupancyThreshold;
	}
	
	public int getNumPassengers(){
		return passengers.size();
	}

	public void addPassenger(Rider x) {
		passengers.add(x);	
	}

	public int getDestinationFloor() {
		return destinationFloor;
	}
	
}
