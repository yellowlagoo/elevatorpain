package building;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import myfileio.MyFileIO;
import passengers.Passengers;

// TODO: Auto-generated Javadoc
/**
 * The Class Building.
 * 
 * @author Iz
 */
public class Building {
	
	/**  Constants for direction. */
	private final static int UP = 1;
	
	/** The Constant DOWN. */
	private final static int DOWN = -1;
	
	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger(Building.class.getName());
	
	/**  The fh - used by LOGGER to write the log messages to a file. */
	private FileHandler fh;
	
	/**  The fio for writing necessary files for data analysis. */
	private MyFileIO fio;
	
	/**  File that will receive the information for data analysis. */
	private File passDataFile;

	/**  passSuccess holds all Passengers who arrived at their destination floor. */
	private ArrayList<Passengers> passSuccess;
	
	/**  gaveUp holds all Passengers who gave up and did not use the elevator. */
	private ArrayList<Passengers> gaveUp;
	
	/**  The number of floors - must be initialized in constructor. */
	private final int NUM_FLOORS;
	
	/**  The size of the up/down queues on each floor. */
	private final int FLOOR_QSIZE = 10;	
	
	/** The floors. */
	public Floor[] floors;
	
	/** The elevator. */
	private Elevator elevator;
	
	/**  The Call Manager - it tracks calls for the elevator, analyzes them to answer questions and prioritize calls. */
	private CallManager callMgr;
	
	// Add any fields that you think you might need here...

	/**
	 * Instantiates a new building.
	 *
	 * @param numFloors the num floors
	 * @param logfile the logfile
	 * @param capacity the capacity
	 * @param floorTicks the floor ticks
	 * @param doorTicks the door ticks
	 * @param passPerTick the pass per tick
	 * Reviewed by: Madi
	 */
	public Building(int numFloors, String logfile, int capacity, int floorTicks, int doorTicks, int passPerTick) {
		NUM_FLOORS = numFloors;
		passSuccess = new ArrayList<Passengers>();
		gaveUp = new ArrayList<Passengers>();
		Passengers.resetStaticID();
		initializeBuildingLogger(logfile);
		// passDataFile is where you will write all the results for those passengers who successfully
		// arrived at their destination and those who gave up...
		fio = new MyFileIO();
		passDataFile = fio.getFileHandle(logfile.replaceAll(".log","PassData.csv"));
		
		// create the floors, call manager and the elevator arrays
		// note that YOU will need to create and config each specific elevator...
		floors = new Floor[NUM_FLOORS];
		for (int i = 0; i < NUM_FLOORS; i++) {
			floors[i]= new Floor(FLOOR_QSIZE); 
		}
		callMgr = new CallManager(floors,NUM_FLOORS);
		elevator = new Elevator(NUM_FLOORS,capacity, floorTicks, doorTicks, passPerTick);		
	}
	
	/**
	 * returns elevator.
	 *
	 * @return the elevator
	 * Reviewed by: Madi
	 */
	public Elevator getElevator() {
		return this.elevator;
	}
	
	// TODO: Place all of your code HERE - state methods and helpers...
	
	/**
	 * Returns elevator's current state.
	 *
	 * @return the elevator state
	 * Reviewed by: Madi
	 */
	public int getElevatorState() {
		return elevator.getCurrState();
	}
	
	/**
	 * Gets the passengers on the floor.
	 *
	 * @param floor the floor
	 * @return the pass
	 * Reviewed by: Madi
	 */
	public Floor getPass(int floor){
		return floors[floor];
	}
	
	/**
	 * Returns the direction of the elevator.
	 *
	 * @return the elevator direction
	 * Reviewed by: Madi
	 */
	public int getElevatorDirection() {
		return elevator.getDirection();
	}
	
	/**
	 * Gets the list in the desired direction.
	 *
	 * @param f the floor
	 * @param dir the direction
	 * @return the list
	 * Reviewed by: Madi
	 */
	public ListIterator<Passengers> getList(int f, int dir) {
		return floors[f].getList(dir);
	}
	
	/**
	 * Checks for impolite passengers at the head of the queue in the direction the elevator is set to travel in. 
	 * Sets impolite passengers to polite.
	 *
	 * @return true if there are impolite passengers, false otherwise
	 * reviewed by Madi
	 */
	private boolean slyInBuilding() {
		int f = elevator.getCurrFloor();
		int dir = elevator.getDirection();
		if (floors[f].size(dir)!=0 && !floors[f].peek(dir).getPolite()) {
			floors[f].peek(dir).setPolite(true);
			return true;
		}
		return false;
	}
	
	/**
	 * Logs passenger groups that are offloaded and counts the number of passengers offloaded.
	 *
	 * @param passengers the passengers
	 * @param time the time
	 * @return the int
	 * reviewed by Madi
	 */
	private int countLogPassengers(ArrayList<Passengers> passengers, int time) {
		int numOffload = 0;
		
		for(int i = 0; i < passengers.size(); i++) {
			Passengers p = passengers.get(i);
			p.setTimeArrived(time);
			passSuccess.add(p);
			logArrival(time, p.getNumPass(), p.getDestFloor(),p.getId());
			numOffload += p.getNumPass();
		}
		
		return numOffload;
	}
	
	/**
	 * Adds passengers to floor.
	 *
	 * @param p the p
	 * @param time the time
	 * Reviewed by: Madi
	 */
	public void addPassToFloor(Passengers p, int time) {
		logCalls(time, p.getNumPass(), p.getOnFloor(), p.getDir(), p.getId());

		int f = p.getOnFloor();
		int distance = p.getDestFloor() - p.getOnFloor();
		if(distance > 0)
			floors[f].add(p, UP);
		else
			floors[f].add(p, DOWN);
	}
	
	/**
	 * Initialize building logger. Sets formating, file to log to, and
	 * turns the logger OFF by default
	 *
	 * @param logfile the file to log information to
	 * reviewed by Madi
	 */
	void initializeBuildingLogger(String logfile) {
		System.setProperty("java.util.logging.SimpleFormatter.format","%4$-7s %5$s%n");
		LOGGER.setLevel(Level.OFF);
		try {
			fh = new FileHandler(logfile);
			LOGGER.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
	
	/**
	 * Curr state stop.
	 *
	 * @param time the time
	 * @return the int
	 * reviewed by Madi
	 */
	private int currStateStop(int time) {
		if(callMgr.noCallsAnywhere())
			return Elevator.STOP;
		if(!floors[elevator.getCurrFloor()].noCalls()) {
			int dir = callMgr.currFloorCallDir(elevator.getCurrFloor());
			elevator.setMoveToFloor(dir);
			return Elevator.OPENDR;
		}
		else {
			int goFloor = callMgr.prioritizePassengerCalls(elevator.getCurrFloor()).getOnFloor();
			elevator.setDirectionWithDest(goFloor);
			elevator.setMoveToFloor(goFloor);
			return Elevator.MVTOFLR;
		}
	}

	/**
	 * Curr state mv to flr. 
	 *
	 * @param time the time
	 * @return the int
	 * reviewed by Madi
	 */
	private int currStateMvToFlr(int time) {
		elevator.move();
		if(elevator.getCurrFloor() != elevator.getMoveToFloor())
			return Elevator.MVTOFLR;
		return Elevator.OPENDR;
	}
	
	/**
	 * Curr state open dr.
	 *
	 * @param time the time
	 * @return the int
	 * reviewed by Madi
	 */
	private int currStateOpenDr(int time) {
		int f = elevator.getCurrFloor();
		elevator.openDoor();
		
		if(elevator.getPrevState() == Elevator.STOP)
			elevator.setDirection(elevator.getMoveToFloor());
		if(elevator.getDoorState() < elevator.getTicksDoorOpenClose())
			return Elevator.OPENDR;
		else {
			elevator.setPrevFloor(elevator.getCurrFloor());
			if(!elevator.getPassByFloor()[f].isEmpty()) 
				return Elevator.OFFLD;
			else {
				if(floors[f].size(elevator.getDirection())==0)
					elevator.reverseDirection();
				return Elevator.BOARD;
			}
		}
	}
	

	
	/**
	 * Curr state off ld.
	 *
	 * @param time the time
	 * @return the int
	 * reviewed by Madi
	 */
	private int currStateOffLd(int time) {
		int f = elevator.getCurrFloor();
		int dir = elevator.getDirection();
		if(elevator.getPrevState()!=Elevator.OFFLD) {
			int numOffload = countLogPassengers(elevator.off(), time);
			elevator.setOffLoadDelay(numOffload);
		}
		if((elevator.getTimeInState()-1)==elevator.getOffLoadDelay()) {
			if(floors[f].size(dir)!=0)
				return Elevator.BOARD;
			else if(elevator.getPassengers()==0 && !callMgr.callOnOtherFloor(elevator) && floors[f].size(-1*dir)!=0){
				elevator.reverseDirection();
				return Elevator.BOARD;
			}
			return Elevator.CLOSEDR;
		}
		else
			return Elevator.OFFLD;
	}

	/**
	 * Curr state board.
	 *
	 * @param time the time
	 * @return the int
	 * reviewed by Madi
	 */
	private int currStateBoard(int time) {
		int f = elevator.getCurrFloor();
		int dir = elevator.getDirection();
		int numP = floors[f].size(dir);
		Passengers rememberForSkip = new Passengers(0, 0, 0, 0, false, 0);
		
		while(!elevator.isFull() && floors[f].size(dir)!=0) {	
			Passengers p = floors[f].peek(dir);
			if(p.getTimeWillGiveUp() < time) {
				giveUpGroup(time, p);
			}
			else if(numP!=0 && (elevator.getPassengers() + p.getNumPass() > elevator.getCapacity())){
				skipGroup(time, p);
				rememberForSkip = p;
			}
			else if(numP!=0)
				boardGroup(time, p);
		}		
		if(elevator.getCurrPassBoarding() > elevator.getPrevPassBoarding())
			elevator.setBoardDelay();
		
		if(rememberForSkip.getTimeWillGiveUp() < (elevator.getBoardDelay() + time) &&
				elevator.getPassengers() != elevator.getCapacity()) {
			elevator.setFull(false);
		}
		
		elevator.updatePassBoarding();
		if(elevator.getBoardDelay() <= elevator.getTimeInState()-1) {
			elevator.resetBoard();
			return Elevator.CLOSEDR;
		}
		else
			return Elevator.BOARD;
	}
	
	/**
	 * Boards group.
	 *
	 * @param time the time
	 * @param p the passengers
	 * Reviewed by: Madi
	 */
	private void boardGroup(int time, Passengers p) {
		int f = elevator.getCurrFloor();
		int dir = elevator.getDirection();
		
		elevator.addToCurrPassBoarding(p.getNumPass());
		p.setBoardTime(time);
		logBoard(time, p.getNumPass(), f, dir, p.getId());
		floors[f].poll(dir);
		elevator.board(p);
	}
	
	/**
	 * Skips group.
	 *
	 * @param time the time
	 * @param p the passengers
	 * Reviewed by: Madi
	 */
	private void skipGroup(int time, Passengers p) {
		int f = elevator.getCurrFloor();
		int dir = elevator.getDirection();
		
		logSkip(time, p.getNumPass(), f, dir, p.getId());
		p.setPolite(true);
		elevator.setFull(true);
	}
	
	/**
	 * Records group giving up.
	 *
	 * @param time the time
	 * @param p the passengers
	 * Reviewed by: Madi
	 */
	private void giveUpGroup(int time, Passengers p) {
		int f = elevator.getCurrFloor();
		int dir = elevator.getDirection();
		
		logGiveUp(time, p.getNumPass(), f, dir, p.getId());
		gaveUp.add(p);
		floors[f].poll(dir);
	}
	
	/**
	 * Curr state close dr.
	 *
	 * @param time the time
	 * @return the int
	 * reviewed by Madi
	 */
	private int currStateCloseDr(int time) {
		elevator.closeDoor();
		
		int f = elevator.getCurrFloor();
		int state = elevator.getDoorState();
		if(state >= 0 && slyInBuilding())
			return Elevator.OPENDR;
		else if(state > 0)
			return Elevator.CLOSEDR;
		else if(elevator.getPassengers()==0) {
			if(callMgr.noCallsAnywhere())
				return Elevator.STOP;
			else if (callMgr.callOnOtherFloor(elevator))
				return Elevator.MV1FLR;
			else if(floors[f].size(elevator.getDirection())!= 0)
				return Elevator.OPENDR;
			else {
				elevator.reverseDirection();
				if(floors[f].size(elevator.getDirection())!= 0) {
					return Elevator.OPENDR;
				}
				return Elevator.MV1FLR;
			}
		}
		else
			return Elevator.MV1FLR;
	}
	
	/**
	 * Curr state mv 1 flr.
	 *
	 * @param time the time
	 * @return the int
	 * reviewed by Madi
	 */
	private int currStateMv1Flr(int time) {
		elevator.move();
		int f = elevator.getCurrFloor();
		int dir = elevator.getDirection();
		if(f != elevator.getPrevFloor()) {
			if(floors[f].size(dir)!= 0) {
				return Elevator.OPENDR;
			}
			else if(!elevator.getPassByFloor()[f].isEmpty())
				return Elevator.OPENDR;
			else if (elevator.getPassengers()==0 && callMgr.callsAnyTypeCurrDir(dir, f)==0 
					&& floors[f].size(-1*dir)!=0) {
				elevator.reverseDirection();
				return Elevator.OPENDR;
			}
			else
				return Elevator.MV1FLR;
		}
		return Elevator.MV1FLR;
	}
	
	/**
	 * Elevator state or floor changed.
	 *
	 * @return true, if successful
	 * reviewed by Madi
	 */
	private boolean elevatorStateOrFloorChanged() {
		if(elevator.getCurrState() != elevator.getPrevState()) {
			return true;
		}
		return false;
	}
	
	/**
	 * Update elevator - this is called AFTER time has been incremented.
	 * -  Logs any state changes, if the have occurred,
	 * -  Calls appropriate method based upon currState to perform
	 *    any actions and calculate next state...
	 *
	 * @param time the time
	 * Reviewed by: Madi
	 */
	public void updateElevator(int time) {
		elevator.incrementTimeInState();
		if (elevatorStateOrFloorChanged())
			logElevatorStateOrFloorChanged(time,elevator.getPrevState(),elevator.getCurrState(),
					                       elevator.getPrevFloor(),elevator.getCurrFloor());
		
		switch (elevator.getCurrState()) {
		case Elevator.STOP: elevator.updateCurrState(currStateStop(time)); break;
		case Elevator.MVTOFLR: elevator.updateCurrState(currStateMvToFlr(time)); break;
		case Elevator.OPENDR: elevator.updateCurrState(currStateOpenDr(time)); break;
		case Elevator.OFFLD: elevator.updateCurrState(currStateOffLd(time)); break;
		case Elevator.BOARD: elevator.updateCurrState(currStateBoard(time)); break;
		case Elevator.CLOSEDR: elevator.updateCurrState(currStateCloseDr(time)); break;
		case Elevator.MV1FLR: elevator.updateCurrState(currStateMv1Flr(time)); break;
		}

	}

	/**
	 * Process passenger data. Do NOT change this - it simply dumps the 
	 * collected passenger data for successful arrivals and give ups. These are
	 * assumed to be ArrayLists...
	 */
	public void processPassengerData() {
		
		try {
			BufferedWriter out = fio.openBufferedWriter(passDataFile);
			out.write("ID,Number,From,To,WaitToBoard,TotalTime\n");
			for (Passengers p : passSuccess) {
				String str = p.getId()+","+p.getNumPass()+","+(p.getOnFloor()+1)+","+(p.getDestFloor()+1)+","+
				             (p.getBoardTime() - p.getTime())+","+(p.getTimeArrived() - p.getTime())+"\n";
				out.write(str);
			}
			for (Passengers p : gaveUp) {
				String str = p.getId()+","+p.getNumPass()+","+(p.getOnFloor()+1)+","+(p.getDestFloor()+1)+","+
				             p.getWaitTime()+",-1\n";
				out.write(str);
			}
			fio.closeFile(out);
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Enable logging. Prints the initial configuration message.
	 * For testing, logging must be enabled BEFORE the run starts.
	 */
	public void enableLogging() {
		LOGGER.setLevel(Level.INFO);
			logElevatorConfig(elevator.getCapacity(),elevator.getTicksPerFloor(), elevator.getTicksDoorOpenClose(), 
					          elevator.getPassPerTick(), elevator.getCurrState(),elevator.getCurrFloor());
		
	}
	
	/**
	 * Close logs, and pause the timeline in the GUI.
	 *
	 * @param time the time
	 */
	public void closeLogs(int time) {
		if (LOGGER.getLevel() == Level.INFO) {
			logEndSimulation(time);
			fh.flush();
			fh.close();
		}
	}
	
	/**
	 * Prints the state.
	 *
	 * @param state the state
	 * @return the string
	 */
	private String printState(int state) {
		String str = "";
		
		switch (state) {
			case Elevator.STOP: 		str =  "STOP   "; break;
			case Elevator.MVTOFLR: 		str =  "MVTOFLR"; break;
			case Elevator.OPENDR:   	str =  "OPENDR "; break;
			case Elevator.CLOSEDR:		str =  "CLOSEDR"; break;
			case Elevator.BOARD:		str =  "BOARD  "; break;
			case Elevator.OFFLD:		str =  "OFFLD  "; break;
			case Elevator.MV1FLR:		str =  "MV1FLR "; break;
			default:					str =  "UNDEF  "; break;
		}
		return(str);
	}
	
	/**
	 * Log elevator config.
	 *
	 * @param capacity the capacity
	 * @param ticksPerFloor the ticks per floor
	 * @param ticksDoorOpenClose the ticks door open close
	 * @param passPerTick the pass per tick
	 * @param state the state
	 * @param floor the floor
	 */
	private void logElevatorConfig(int capacity, int ticksPerFloor, int ticksDoorOpenClose, 
			                       int passPerTick, int state, int floor) {
		LOGGER.info("CONFIG:   Capacity="+capacity+"   Ticks-Floor="+ticksPerFloor+"   Ticks-Door="+ticksDoorOpenClose+
				    "   Ticks-Passengers="+passPerTick+"   CurrState=" + (printState(state))+"   CurrFloor="+(floor+1));
	}
		
	/**
	 * Log elevator state changed.
	 *
	 * @param time the time
	 * @param prevState the prev state
	 * @param currState the curr state
	 * @param prevFloor the prev floor
	 * @param currFloor the curr floor
	 */
	private void logElevatorStateOrFloorChanged(int time, int prevState, int currState, int prevFloor, int currFloor) {
		LOGGER.info("Time="+time+"   Prev State: " + printState(prevState) + "   Curr State: "+printState(currState)
		            +"   PrevFloor: "+(prevFloor+1) + "   CurrFloor: " + (currFloor+1));
	}
	
	/**
	 * Log arrival.
	 *
	 * @param time the time
	 * @param numPass the num pass
	 * @param floor the floor
	 * @param id the id
	 */
	private void logArrival(int time, int numPass, int floor,int id) {
		LOGGER.info("Time="+time+"   Arrived="+numPass+" Floor="+ (floor+1)
		            +" passID=" + id);						
	}
	
	/**
	 * Log calls.
	 *
	 * @param time the time
	 * @param numPass the num pass
	 * @param floor the floor
	 * @param dir the dir
	 * @param id the id
	 */
	private void logCalls(int time, int numPass, int floor, int dir, int id) {
		LOGGER.info("Time="+time+"   Called="+numPass+" Floor="+ (floor +1)
			 	    +" Dir="+((dir>0)?"Up":"Down")+"   passID=" + id);
	}
	
	/**
	 * Log give up.
	 *
	 * @param time the time
	 * @param numPass the num pass
	 * @param floor the floor
	 * @param dir the dir
	 * @param id the id
	 */
	private void logGiveUp(int time, int numPass, int floor, int dir, int id) {
		LOGGER.info("Time="+time+"   GaveUp="+numPass+" Floor="+ (floor+1) 
				    +" Dir="+((dir>0)?"Up":"Down")+"   passID=" + id);				
	}

	/**
	 * Log skip.
	 *
	 * @param time the time
	 * @param numPass the num pass
	 * @param floor the floor
	 * @param dir the dir
	 * @param id the id
	 */
	private void logSkip(int time, int numPass, int floor, int dir, int id) {
		LOGGER.info("Time="+time+"   Skip="+numPass+" Floor="+ (floor+1) 
			   	    +" Dir="+((dir>0)?"Up":"Down")+"   passID=" + id);				
	}
	
	/**
	 * Log board.
	 *
	 * @param time the time
	 * @param numPass the num pass
	 * @param floor the floor
	 * @param dir the dir
	 * @param id the id
	 */
	private void logBoard(int time, int numPass, int floor, int dir, int id) {
		LOGGER.info("Time="+time+"   Board="+numPass+" Floor="+ (floor+1) 
				    +" Dir="+((dir>0)?"Up":"Down")+"   passID=" + id);				
	}
	
	/**
	 * Log end simulation.
	 *
	 * @param time the time
	 */
	private void logEndSimulation(int time) {
		LOGGER.info("Time="+time+"   Detected End of Simulation");
	}

	public void disableLogging() {
		// TODO Auto-generated method stub
		LOGGER.setLevel(Level.OFF);
	}

}