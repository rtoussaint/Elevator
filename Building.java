import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author rtoussaint
 * @author Lyndsay Kerwin
 * 
 *
 *There is only one building for program.  It knows which floors have their 'up' and 'down' buttons pushed.  If a 
 *floor has its up button pushed, then this floor is added to upBarriers -- same for the 'down' button pushed.
 */

public class Building extends AbstractBuilding{

	private Set<EventBarrier> upBarriers; //list of floors that have the 'up' button pushed
	private Set<EventBarrier> downBarriers;
	private Set<EventBarrier> onBarriers;

	private ElevatorController myElevatorController;

	public Building(int numFloors, int numElevators) {
		super(numFloors, numElevators);
		
		upBarriers = new HashSet<EventBarrier>();
		downBarriers = new HashSet<EventBarrier>();
		onBarriers = new HashSet<EventBarrier>();
		
		// TODO Auto-generated constructor stub
	}

	@Override
	public Elevator CallUp(EventBarrier eb) {
		// TODO Auto-generated method stub
		addUpBarrier(eb);
		Elevator reRoutedElevator = myElevatorController.checkUpElevators(eb.getFloor());
		return reRoutedElevator;
	}

	@Override
	public Elevator CallDown(EventBarrier eb) {
		// TODO Auto-generated method stub
		addDownBarrier(eb);
		Elevator reRoutedElevator = myElevatorController.checkDownElevators(eb.getFloor());
		return reRoutedElevator;
	}

	/**
	 * Using the floor level, create an event barrier and add it to the UP list.
	 * @param curFloor 
	 */
	public void addUpBarrier(EventBarrier eb){
		upBarriers.add(eb);
		
	}
	
	
	public void addOnBarrier(EventBarrier eb){
		onBarriers.add(eb);
	}
	
	public void removeOnBarrier(EventBarrier eb){
		onBarriers.remove(eb);
	}

	/**
	 * Using the floor level, create an event barrier and add it to the DOWN list.
	 * @param curFloor 
	 */
	public void addDownBarrier(EventBarrier eb){
		downBarriers.add(eb);
	}

	/**
	 * Once the elevator comes and picks up the riders, remove this floor from the UpBarrier list.
	 * @param floor
	 */
	public void removeUpBarrier(EventBarrier eb){
		upBarriers.remove(eb);		
	}

	/**
	 * Once the elevator comes and picks up the riders, remove this floor from the DownBarrier list.
	 * @param floor
	 */
	public void removeDownBarrier(EventBarrier eb){
		downBarriers.remove(eb);
	}

	/**
	 * TODO: Make elevator run
	 */
	public void runElevatorLoop () {
		int i = 0;
		while (i < 1000) {
			Elevator elevator = myElevatorController.chooseElevator();
			//TODO: max or min floors
			if(onBarriers.isEmpty()) {
				elevator.calculateDirection(upBarriers, downBarriers);
			}
			else if(elevator.getDirectionStatus() != Direction.DOWN) {
				int rerouteFloor = numFloors;
				for(EventBarrier eb : upBarriers){
					if(eb.getFloor() < rerouteFloor && eb.getFloor() > elevator.getCurrentFloor()){
						rerouteFloor = eb.getFloor();
						elevator.setDestinationFloorAndChangeDirection(rerouteFloor);
					}
				}
				myElevatorController.checkUpElevators(rerouteFloor);
			}
			
			else if(elevator.getDirectionStatus() != Direction.UP) {
				int rerouteFloor =0;
				for(EventBarrier eb : downBarriers){
					if(eb.getFloor() > rerouteFloor && eb.getFloor() < elevator.getCurrentFloor()){
						rerouteFloor = eb.getFloor();
						elevator.setDestinationFloorAndChangeDirection(rerouteFloor);
					}
				}
				myElevatorController.checkDownElevators(rerouteFloor);
			}
			
			elevator.arriveAtFloor(elevator.getDestinationFloor());
			i++;
		}
	}

	protected Set<EventBarrier> getUpBarriers(){
		return upBarriers;
	}

	protected Set<EventBarrier> getDownBarriers(){
		return downBarriers;
	}

	protected Set<EventBarrier> getOnBarriers(){
		return onBarriers;
	}

	protected ElevatorController getElevatorController(){
		return myElevatorController;
	}

	public void setElevatorController(ElevatorController ec) {
		this.myElevatorController = ec;
		
	}

}
