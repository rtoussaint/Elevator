import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

public class Parser {
	public static PrintWriter writer;
	
	private int myNumFloors;
	private int myNumElevators;
	private int myNumRiders;
	private int myElevatorCapacity;

	private List<Rider> myRiderList = new CopyOnWriteArrayList<Rider>();
	private List<Elevator> myElevatorList = new CopyOnWriteArrayList<Elevator>();

	Parser() {
		try {
			writer = new PrintWriter("output.txt", "UTF-8");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
	}
	
	public void readFile(String fileName) {

		Scanner in = null;

		try {
			in = new Scanner(new FileReader(fileName));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// This gets the building numfloors and numelevators
		String firstLine = in.nextLine();
		String[] aboutBuilding = firstLine.split(" ");

		myNumFloors = Integer.parseInt(aboutBuilding[0]);
		myNumElevators = Integer.parseInt(aboutBuilding[1]);
		myNumRiders = Integer.parseInt(aboutBuilding[2]);
		myElevatorCapacity = Integer.parseInt(aboutBuilding[3]);

		Building building = new Building(myNumFloors, myNumElevators);
		ElevatorController elevatorController = new ElevatorController(building);
		building.setElevatorController(elevatorController);
		// This gets all the riders
		while (in.hasNext()) {
			String line = in.nextLine();
			String[] floors = line.split(" ");
			int riderId = Integer.parseInt(floors[0]); // Not Used
			int sourceFloor = Integer.parseInt(floors[1]);
			int destinationFloor = Integer.parseInt(floors[2]);
			
			EventBarrier barrier = new EventBarrier();
			barrier.setFloor(sourceFloor);
			Rider rider;
			if (destinationFloor == -1) {
				rider = new Rider(building, sourceFloor, barrier, BadlyBehaved.NO_FLOOR_REQUEST, riderId);
			}
			else if (destinationFloor == -2) {
				rider = new Rider(building, sourceFloor, barrier, BadlyBehaved.PRESS_BUTTON_DONT_GET_ON, riderId);
			}
			else {
				rider = new Rider(building, sourceFloor, destinationFloor,
						barrier, riderId);
			}

			myRiderList.add(rider);
		}

		int elevatorCounter = 0;
		int floorCounter = 0;
		int floorRanges = (int) Math.floor(myNumFloors / myNumElevators);
		while (floorCounter <= myNumFloors) {
			if (floorCounter+floorRanges <= myNumFloors) {
				Elevator e = new Elevator(myNumFloors, elevatorCounter,
						myElevatorCapacity, floorCounter, floorCounter
								+ floorRanges);
				elevatorController.addElevator(e);
				e.setController(elevatorController);
				myElevatorList.add(e);
			}
			else {
				Elevator e = new Elevator(myNumFloors, elevatorCounter,
						myElevatorCapacity, floorCounter, myNumFloors);
				elevatorController.addElevator(e);
				e.setController(elevatorController);
				myElevatorList.add(e);
			}
			elevatorCounter++;
			floorCounter = floorCounter + floorRanges + 1;
		}
	}
	
	public int getMyNumFloors() {
		return myNumFloors;
	}

	public int getMyNumElevators() {
		return myNumElevators;
	}

	public int getMyNumRiders() {
		return myNumRiders;
	}

	public int getMyElevatorCapacity() {
		return myElevatorCapacity;
	}

	public List<Rider> getMyRiderList() {
		return myRiderList;
	}

	public List<Elevator> getMyElevatorList() {
		return myElevatorList;
	}

//	public static void main(String[] args) {
//		Parser parser = new Parser();
//		parser.readFile("basicElevator.txt");
//		System.out.println("BREAK1");
//		System.out.println(parser.getMyNumFloors());
//		System.out.println(parser.getMyNumElevators());
//		System.out.println(parser.getMyNumRiders());
//		System.out.println(parser.getMyElevatorCapacity());
//		for (Elevator e : parser.getMyElevatorList()) {
//			System.out.println(e.getDestinationFloor() + " " + e.getCurrentFloor());
//		}
//		System.out.println("BREAK2");
//		for (Rider r : parser.getMyRiderList()) {
//			System.out.println(r);
//		}
//
//	}
}
