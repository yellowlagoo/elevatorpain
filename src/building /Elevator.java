package building;

import java.util.ArrayList;

import passengers.Passengers;


// TODO: Auto-generated Javadoc
/**
 * The Class Elevator.
 *
 * @author Madi 
 * This class will represent an elevator, and will contain
 * configuration information (capacity, speed, etc) as well
 * as state information - such as stopped, direction, and count
 * of passengers targeting each floor...
 */
public class Elevator {
	
	/**  Elevator State Variables - These are visible publicly. */
	public final static int STOP = 0;
	
	/** The Constant MVTOFLR. */
	public final static int MVTOFLR = 1;
	
	/** The Constant OPENDR. */
	public final static int OPENDR = 2;
	
	/** The Constant OFFLD. */
	public final static int OFFLD = 3;
	
	/** The Constant BOARD. */
	public final static int BOARD = 4;
	
	/** The Constant CLOSEDR. */
	public final static int CLOSEDR = 5;
	
	/** The Constant MV1FLR. */
	public final static int MV1FLR = 6;

	/** Default configuration parameters for the elevator. These should be
	 *  updated in the constructor.
	 */
	private int capacity = 15;				// The number of PEOPLE the elevator can hold
	
	/**  if elevator is at full capacity. */
	private boolean capacityFull = false;			// true if elevator is at full capacity
	
	/** The ticks per floor. */
	private int ticksPerFloor = 5;			// The time it takes the elevator to move between floors
	
	/** The ticks door open close. */
	private int ticksDoorOpenClose = 2;  	// The time it takes for doors to go from OPEN <=> CLOSED
	
	/** The pass per tick. */
	private int passPerTick = 3;            // The number of PEOPLE that can enter/exit the elevator per tick
	
	/**  Finite State Machine State Variables. */
	private int currState;		// current state
	
	/** The prev state. */
	private int prevState;      // prior state
	
	/** The prev floor. */
	private int prevFloor;      // prior floor
	
	/** The curr floor. */
	private int currFloor;      // current floor
	
	/** The direction. */
	private int direction;      // direction the Elevator is traveling in.

	/** The time in state. */
	private int timeInState = 1;    // represents the time in a given state
	                            // reset on state entry, used to determine if
	                            // state has completed or if floor has changed
	                            // *not* used in all states 

	/** The door state. */
                            	private int doorState;      // used to model the state of the doors - OPEN, CLOSED
	                            // or moving

                            	/** The curr pass boarding. */
                            	private int currPassBoarding;

                            	/** The prev pass boarding. */
                            	private int prevPassBoarding;

                            	/** The board delay. */
                            	private int boardDelay;

                            	/** The offload delay. */
                            	private int offloadDelay;
	/** The passengers. */
                            	private int passengers;  	// the number of people in the elevator
	
	/** The pass by floor. */
	private ArrayList<Passengers>[] passByFloor;  // Passengers to exit on the corresponding floor

	/** The move to floor. */
	private int moveToFloor;	// When exiting the STOP state, this is the floor to move to without
	                            // stopping.

	/** The post move to floor dir. */
                            	private int postMoveToFloorDir; 

	/**
                                	 * Instantiates a new elevator.
                                	 *
                                	 * @param numFloors the num floors
                                	 * @param capacity the capacity
                                	 * @param floorTicks the floor ticks
                                	 * @param doorTicks the door ticks
                                	 * @param passPerTick the pass per tick
                                	 * 
                                	 * Reviewed by: Iz
                                	 */
                                	@SuppressWarnings("unchecked")
	public Elevator(int numFloors,int capacity, int floorTicks, int doorTicks, int passPerTick) {		
		this.prevState = STOP;
		this.currState = STOP;
		this.timeInState = 0;
		this.currFloor = 0;
		this.doorState = 0;
		passByFloor = new ArrayList[numFloors];
		
		for (int i = 0; i < numFloors; i++) 
			passByFloor[i] = new ArrayList<Passengers>(); 

		this.capacity = capacity;
		this.ticksPerFloor = floorTicks;
		this.ticksDoorOpenClose = doorTicks;
		this.passPerTick = passPerTick;
	}
	
	/**
	 * Gets the capacity.
	 *
	 * @return the capacity
	 * 
	 * Reviewed by: Iz
	 */
	protected int getCapacity() {
		return capacity;
	}

	/**
	 * Gets the ticks per floor.
	 *
	 * @return the ticks per floor
	 * 
	 * Reviewed by: Iz
	 */
	protected int getTicksPerFloor() {
		return ticksPerFloor;
	}

	/**
	 * Gets the ticks door open close.
	 *
	 * @return the ticks door open close
	 * 
	 * Reviewed by: Iz
	 */
	protected int getTicksDoorOpenClose() {
		return ticksDoorOpenClose;
	}

	/**
	 * Gets the pass per tick.
	 *
	 * @return the pass per tick
	 * 
	 * Reviewed by: Iz
	 */
	protected int getPassPerTick() {
		return passPerTick;
	}

	/**
	 * Gets the current state.
	 *
	 * @return the current state
	 * 
	 * Reviewed by: Iz
	 */
	public int getCurrState() {
		return currState;
	}

	/**
	 * Gets the previous state.
	 *
	 * @return the previous state
	 * 
	 * Reviewed by: Iz
	 */
	protected int getPrevState() {
		return prevState;
	}

	/**
	 * Gets the previous floor.
	 *
	 * @return the previous floor
	 * 
	 * Reviewed by: Iz
	 */
	protected int getPrevFloor() {
		return prevFloor;
	}
	
	/**
	 * Sets the previous floor.
	 *
	 * @param floor the new previous floor
	 * 
	 * Reviewed by: Iz
	 */
	protected void setPrevFloor(int floor) {
		this.prevFloor = floor;
	}

	/**
	 * Gets the current floor.
	 *
	 * @return the current floor
	 * 
	 * Reviewed by: Iz
	 */
	public int getCurrFloor() {
		return currFloor;
	}

	/**
	 * Updates the current state.
	 *
	 * @param nextState the next state
	 * 
	 * Reviewed by: Iz
	 */
	protected void updateCurrState(int nextState) {
		if(nextState != currState) timeInState = 1;
		prevState = currState;
		currState = nextState;
	}
	
	/**
	 * Increments time in state.
	 * 
	 * Reviewed by: Iz
	 */
	protected void incrementTimeInState() {
		timeInState++;
	}
	
	/**
	 * Gets the time in state.
	 *
	 * @return time in state
	 * 
	 * Reviewed by: Iz
	 */
	protected int getTimeInState() {
		return timeInState;
	}
	
	/**
	 * Updates floor to move to.
	 *
	 * @param moveToFloor the new move to floor
	 * 
	 * Reviewed by: Iz
	 */
	protected void setMoveToFloor(int moveToFloor) {
		this.moveToFloor = moveToFloor;
	}
	
	/**
	 * Gets the move to floor.
	 *
	 * @return floor to move to
	 * 
	 * Reviewed by: Iz
	 */
	protected int getMoveToFloor() {
		return moveToFloor;
	}
	
	/**
	 * Gets the post move to floor dir.
	 *
	 * @return direction to go in after moving to moveToFloor
	 * 
	 * Reviewed by: Iz
	 */
	protected int getPostMoveToFloorDir() {
		return postMoveToFloorDir;
	}
	
	/**
	 * Updates direction to go in after moving to floor.
	 *
	 * @param postMoveToFloorDir the new post move to floor dir
	 * 
	 * Reviewed by: Iz
	 */
	protected void setPostMoveToFloorDir(int postMoveToFloorDir) {
		this.postMoveToFloorDir = postMoveToFloorDir;
	}
	
	/**
	 * Sets direction to pos/neg 1 if destination floor is greater/less than current floor.
	 *
	 * @param destFloor the new direction with dest
	 * 
	 * Reviewed by: Iz
	 */ 
	protected void setDirectionWithDest(int destFloor) {
		if(destFloor > currFloor) direction = 1;
		else direction = -1;
	}
	
	/**
	 * Sets direction given direction.
	 *
	 * @param direction the new direction
	 * 
	 * Reviewed by: Iz
	 */
	protected void setDirection(int direction) {
		this.direction = direction;
	}
	
	/**
	 * Returns direction of elevator.
	 *
	 * @return direction of elevator
	 * 
	 * Reviewed by: Iz
	 */
	protected int getDirection() {
		return direction;
	}
	
	/**
	 * Returns number of passengers in elevator.
	 *
	 * @return number of passengers in elevator
	 * 
	 * Reviewed by: Iz
	 */
	public int getPassengers() {
		return passengers;
	}
	
	/**
	 * Reverses direction of elevator without parameters.
	 * 
	 * Reviewed by: Iz
	 */
	protected void reverseDirection() {
		direction = direction*-1;
	}
	
	/**
	 * Closes the doors, increments time in state, increments door state by by -1.
	 * 
	 * Reviewed by: Iz
	 */
	protected void closeDoor() {
		doorState--;
	}
	
	/**
	 * Opens the doors, increments time in state, increments door state by +1.
	 * 
	 * Reviewed by: Iz
	 */
	protected void openDoor() {
		doorState++;
	}
	
	/**
	 * Gets the door state.
	 *
	 * @return the door state
	 * 
	 * Reviewed by: Iz
	 */
	protected int getDoorState() {
		return doorState;
	}
	
	/**
	 * Moves elevator to next floor in the current direction.
	 * 
	 * Reviewed by: Iz
	 */
	protected void move() {
		prevFloor = currFloor;
		if((timeInState-1) % ticksPerFloor == 0) currFloor += direction;
	}
	
	/**
	 * Gets the pass by floor.
	 *
	 * @return the pass by floor
	 * 
	 * Reviewed by: Iz
	 */
	protected ArrayList<Passengers>[] getPassByFloor(){
		return passByFloor;
	}
	
	/**
	 * On boards new passengers, updates passenger count, .
	 *
	 * @param newPass the new pass
	 * 
	 * Reviewed by: Iz
	 */
	protected void board(Passengers newPass) {
		passByFloor[newPass.getDestFloor()].add(newPass);
		passengers += newPass.getNumPass();
	}
	
	/**
	 * Off boards passengers at current floor,.
	 *
	 * @return arraylist of passengers who have left
	 * 
	 * Reviewed by: Iz
	 */
	protected ArrayList<Passengers> off(){
		for(Passengers p : passByFloor[currFloor]) {
			passengers -= p.getNumPass();
		}
		ArrayList<Passengers> temp = passByFloor[currFloor];
		passByFloor[currFloor] = new ArrayList<Passengers>();
		return temp;
	}
	
	/**
	 * Returns whether or not elevator is at max capacity,.
	 *
	 * @return whether or not elevator is at max capacity
	 * 
	 * Reviewed by: Iz
	 */
	protected boolean isFull() {
		return capacityFull;
	}
	
	/**
	 * Sets capacity of elevator to full.
	 *
	 * @param capacityFull the new full
	 * 
	 * Reviewed by: Iz
	 */
	protected void setFull(boolean capacityFull) {
		this.capacityFull = capacityFull;
	}
	
	/**
	 * Returns delay depending on number of passengers.
	 *
	 * @param numPass the num pass
	 * @return delay depending on number of passengers
	 * 
	 * Reviewed by: Iz
	 */
	protected int calculateDelay(int numPass) {
		return (numPass + (passPerTick - 1))/passPerTick;
	}
	
	/**
	 * Reset board.
	 * 
	 * Reviewed by: Iz
	 */
	protected void resetBoard() {
		setFull(false);
		currPassBoarding = 0;
		prevPassBoarding = 0;
		boardDelay = 0;
	}
	
	/**
	 * Calculates and sets offload delay.
	 *
	 * @param numPass the new off load delay
	 * 
	 * Reviewed by: Iz
	 */
	protected void setOffLoadDelay(int numPass) {
		offloadDelay = calculateDelay(numPass);
	} 
	
	/**
	 * Gets the off load delay.
	 *
	 * @return offload delay
	 * 
	 * Reviewed by: Iz
	 */
	protected int getOffLoadDelay() {
		return offloadDelay;
	}
	
	/**
	 * adds passengers to current passengers boarding.
	 *
	 * @param numPass the num pass
	 * 
	 * Reviewed by: Iz
	 */
	protected void addToCurrPassBoarding(int numPass) {
		currPassBoarding += numPass;
	}
	
	/**
	 * Gets the curr pass boarding.
	 *
	 * @return amount of current passengers boarding
	 * 
	 * Reviewed by: Iz
	 */
	protected int getCurrPassBoarding() {
		return currPassBoarding;
	}
	
	/**
	 * Gets the board delay.
	 *
	 * @return board delay
	 * 
	 * Reviewed by: Iz
	 */
	protected int getBoardDelay() {
		return boardDelay;
	}
	
	/**
	 * calculates and sets board delay.
	 * 
	 * Reviewed by: Iz
	 */
	protected void setBoardDelay() {
		boardDelay = calculateDelay(currPassBoarding);
	}
	
	/**
	 * Gets the prev pass boarding.
	 *
	 * @return amount of previous passengers boarding
	 * 
	 * Reviewed by: Iz
	 */
	protected int getPrevPassBoarding() {
		return prevPassBoarding;
	}
	
	/**
	 * Sets previous passenger boarding to current passenger boarding.
	 * 
	 * Reviewed by: Iz
	 */
	protected void updatePassBoarding() {
		prevPassBoarding = currPassBoarding;
	}
}