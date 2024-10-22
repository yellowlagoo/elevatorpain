package passengers;

/**
 * The Class Passengers. Represents a GROUP of passengers that are 
 * traveling together from one floor to another. Tracks information that 
 * can be used to analyze Elevator performance.
 * 
 * @author Iz
 */
public class Passengers {
	
	/**  Constant for representing direction. */
	private static final int UP = 1;
	
	/** The Constant DOWN. */
	private static final int DOWN = -1;
	
	/**  ID represents the NEXT available id for the passenger group. */
	private static int ID=0;

	/** id is the unique ID assigned to each Passenger during construction.
	 *  After assignment, static ID must be incremented.
	 */
	private int id;
	
	/** These fields will be passed into the constructor by the Building.
	 *  This data will come from the .csv file read by the SimController
	 */
	private int time;         // the time that the Passenger will call the elevator
	
	/** The num pass. */
	private int numPass;      // the number of passengers in this group
	
	/** The on floor. */
	private int onFloor;      // the floor that the Passenger will appear on
	
	/** The dest floor. */
	private int destFloor;	  // the floor that the Passenger will get off on
	
	/** The polite. */
	private boolean polite;   // will the Passenger let the doors close?
	
	/** The wait time. */
	private int waitTime;     // the amount of time that the Passenger will wait for the
	                          // Elevator
	
	/** These values will be calculated during construction.
	 */
	private int direction;      // The direction that the Passenger is going
	
	/** The time will give up. */
	private int timeWillGiveUp; // The calculated time when the Passenger will give up
	
	/** These values will actually be set during execution. Initialized to -1 */
	private int boardTime=-1;
	
	/** The time arrived. */
	private int timeArrived=-1;

	/**
	 * Instantiates a new passengers.
	 *
	 * @param time the time
	 * @param numPass the number of people in this Passenger
	 * @param on the floor that the Passenger calls the elevator from
	 * @param dest the floor that the Passenger is going to
	 * @param polite - are the passengers polite?
	 * @param waitTime the amount of time that the passenger will wait before giving up
	 * Reviewed by Karina Asanbekova
	 */
	public Passengers(int time, int numPass, int on, int dest, boolean polite, int waitTime) {
		this.time = time;
		this.numPass = numPass;
		this.onFloor = on - 1;
		this.destFloor = dest - 1;
		this.polite = polite;
		this.waitTime = waitTime;

		timeWillGiveUp = time + waitTime;
		if(destFloor - onFloor > 0)
			direction = UP;
		else
			direction = DOWN;
		
		id = ID++;
	}

	/**
	 * Returns the passenger group ID.
	 *
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Returns the passenger group call time.
	 *
	 * @return the time
	 */
	public int getTime() {
		return time;
	}

	/**
	 * Returns the number of passengers in the group.
	 *
	 * @return the num pass
	 */
	public int getNumPass() {
		return numPass;
	}

	/**
	 * Returns the floor the passenger group makes the call on.
	 *
	 * @return the on floor
	 */
	public int getOnFloor() {
		return onFloor;
	}

	/**
	 * Returns the floor the passenger group wants to go to.
	 *
	 * @return the dest floor
	 */
	public int getDestFloor() {
		return destFloor;
	}

	/**
	 * Returns the time the group will wait for the elevator.
	 *
	 * @return the wait time
	 */
	public int getWaitTime() {
		return waitTime;
	}

	/**
	 * Returns the time the group boards the elevator.
	 *
	 * @return the board time
	 */
	public int getBoardTime() {
		return boardTime;
	}

	/**
	 * Returns the time the group arrives.
	 *
	 * @return the time arrived
	 */
	public int getTimeArrived() {
		return timeArrived;
	}
	
	/**
	 * Returns the direction the passenger group wants to travel in.
	 *
	 * @return the dir
	 */
	public int getDir() {
		return direction;
	}
	
	/**
	 * Returns the time the passenger group will give up.
	 *
	 * @return the time
	 */
	public int getTimeWillGiveUp() {
		return timeWillGiveUp;
	}
	
	/**
	 * Returns whether the group is polite or not.
	 *
	 * @return the polite
	 */
	public boolean getPolite() {
		return polite;
	}
	
	/**
	 * Sets the board time.
	 *
	 * @param boardTime the new board time
	 */
	public void setBoardTime(int boardTime) {
		this.boardTime = boardTime;
	}
	
	/**
	 * Sets the time arrived.
	 *
	 * @param timeArrived the new time arrived
	 */
	public void setTimeArrived(int timeArrived) {
		this.timeArrived = timeArrived;
	}
	
	/**
	 * Sets whether the passenger group is polite or not.
	 *
	 * @param polite the new polite
	 */
	public void setPolite(boolean polite) {
		this.polite = polite;
	}

	/**
	 * Reset static ID. 
	 * This method MUST be called during the building constructor BEFORE
	 * reading the configuration files. This is to provide consistency in the
	 * Passenger ID's during JUnit testing.
	 * Reviewed by Karina Asanbekova
	 */
	public static void resetStaticID() {
		ID = 0;
	}

	/**
	 * toString - returns the formatted string for this class.
	 *
	 * @return the
	 */
	@Override
	public String toString() {
		return("ID="+id+"   Time="+time+"   NumPass="+numPass+"   From="+(onFloor+1)+""
				+ "   To="+(destFloor+1)+"   Polite="+polite+"   Wait="+waitTime);
	}

}