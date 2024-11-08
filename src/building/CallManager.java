package building;

import passengers.Passengers;

/**
 * @author Madi
 * The Class CallManager. This class models all of the calls on each floor,
 * and then provides methods that allow the building to determine what needs
 * to happen (ie, state transitions).
 */
public class CallManager {
	
	/** The floors. */
	private Floor[] floors;
	
	/** The num floors. */
	private final int NUM_FLOORS;
	
	/** The Constant UP. */
	private final static int UP = 1;
	
	/** The Constant DOWN. */
	private final static int DOWN = -1; 
	
	/**
	 * Instantiates a new call manager.
	 *
	 * @param floors the floors
	 * @param numFloors the num floors
	 * 
	 * Reviewed by: Iz
	 */
	public CallManager(Floor[] floors, int numFloors) {
		this.floors = floors;
		NUM_FLOORS = numFloors;
	}

	/**
	 * Prioritize passenger calls from STOP STATE.
	 *
	 * @param floor the floor
	 * @return the passengers
	 * 
	 * Reviewed by: Iz
	 */
	protected Passengers prioritizePassengerCalls(int floor) {
		int upCalls = callsInDir(UP);
		int downCalls = callsInDir(DOWN);
		int upFloor = upCall();
		int downFloor = downCall();
		if(upCalls > downCalls)
			return floors[upFloor].peek(UP);
		else if(upCalls < downCalls)
			return floors[downFloor].peek(DOWN);
		else {
			if (Math.abs(downFloor - floor) < Math.abs(upFloor - floor))
				return floors[downFloor].peek(DOWN);
			return floors[upFloor].peek(UP);
		}
	}

	/**
	 * Returns the total number of calls in a given direction on all floors.
	 *
	 * @param dir the direction
	 * @return the number of calls
	 * 
	 * Reviewed by: Iz
	 */
	private int callsInDir(int dir) {
		int calls = 0;
		for (int f = 0; f < NUM_FLOORS; f++) {
			if(dir==UP && !floors[f].isEmpty(UP)) {
				calls++;
			}
			if(dir==DOWN && !floors[f].isEmpty(DOWN)) {
				calls++;
			}
		}
		return calls;
	}
	
	/**
	 * Returns the lowest floor with an up call.
	 *
	 * @return the floor
	 * 
	 * Reviewed by: Iz
	 */
	private int upCall() {
		int f = 0;
		while(f < NUM_FLOORS) {
			if(!floors[f].isEmpty(UP)) {
				return f;
			}
			f++;
		}
		return 0;
	}
	
	/**
	 * Returns the highest floor with a down call.
	 *
	 * @return the floor
	 * 
	 * Reviewed by: Iz
	 */
	private int downCall() {
		int f = NUM_FLOORS-1;
		while(f >= 0) {
			if(!floors[f].isEmpty(DOWN)) {
				return f;
			}
			f--;
		}
		return 0;
	}
	
	/**
	 * Checks if there are calls in any direction on any floor.
	 *
	 * @return true if no calls anywhere, false otherwise
	 * 
	 * Reviewed by: Iz
	 */
	protected boolean noCallsAnywhere() {
		return (callsInDir(UP)==0 && callsInDir(DOWN)==0);
	}
	
	/**
	 * Curr floor call dir.
	 *
	 * @param currFloor the curr floor
	 * @return the int
	 * 
	 * Reviewed by: Iz
	 */
	protected int currFloorCallDir(int currFloor) {
		if(!floors[currFloor].isEmpty(UP) && floors[currFloor].isEmpty(DOWN))
			return UP;
		else if(floors[currFloor].isEmpty(UP) && !floors[currFloor].isEmpty(DOWN))
			return DOWN;
		else if(callsAnyTypeCurrDir(UP, currFloor) >= callsAnyTypeCurrDir(DOWN, currFloor)) {
			return UP;
		}
		return DOWN;
	}
	
	/**
	 * Calls any type curr dir.
	 *
	 * @param dir the direction
	 * @param currFloor the current floor
	 * @return the int
	 * 
	 * Reviewed by: Iz
	 */
	protected int callsAnyTypeCurrDir(int dir, int currFloor) {
		int high = currFloor;
		int low = 0;
		int calls = 0;
		if(dir == UP) {
			low = currFloor + 1;
			high = NUM_FLOORS;
		}
		for(int i = low; i < high; i++) {
			if(!floors[i].noCalls()) calls++;
		}
		return calls;
	}
	
	/**
	 * Checks if there is a call on another floor.
	 *
	 * @param elevator the elevator
	 * @return true if call exists, false otherwise
	 * 
	 * Reviewed by: Iz
	 */
	protected boolean callOnOtherFloor(Elevator elevator) {
		int dir = elevator.getDirection();
		int f = elevator.getCurrFloor();
		int lowBound;
		int upBound;
		
		if(dir == DOWN) {
			lowBound = 0;
			upBound = f;
		}
		else {
			lowBound = f+1;
			upBound = NUM_FLOORS;
		}
		
		for (int i = lowBound; i < upBound; i++) {
			if(!floors[i].isEmpty(UP) || !floors[i].isEmpty(DOWN))
				return true;
		}
		return false;
	}
	
}