package MVC;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ListIterator;

import building.Building;
import building.Floor;
import genericqueue.GenericQueue;
import myfileio.MyFileIO;
import passengers.Passengers;

// TODO: Auto-generated Javadoc
/**
 * The Class ElevatorSimController. @author Karina Asanbekova
 */
// TODO: Auto-generated Javadoc
public class ElevatorSimController {
	
	/**  Constant to specify the configuration file for the simulation. */
	private static final String SIM_CONFIG = "ElevatorSimConfig.csv";
	
	/**  Constant to make the Passenger queue contents visible after initialization. */
	private boolean PASSQ_DEBUG=false;
	
	/** The gui. */
	private ElevatorSimulation gui;
	
	/** The building. */ 
	private Building building;
	
	/** The fio. */
	private MyFileIO fio;

	/** The num floors. */
	private final int NUM_FLOORS;
	
	/** The num floors. */
	private int numFloors;
	
	/** The capacity. */
	private int capacity;
	
	/** The floor ticks. */
	private int floorTicks;
	
	/** The door ticks. */
	private int doorTicks;
	
	/** The pass per tick. */
	private int passPerTick;
	
	/** The testfile. */
	private String testfile;
	
	/** The logfile. */
	private String logfile;
	
	/** The step cnt. */
	private int stepCnt = 0;
	
	/** passQ holds the time-ordered queue of Passengers, initialized at the start 
	 *  of the simulation. At the end of the simulation, the queue will be empty.
	 */
	private GenericQueue<Passengers> passQ;

	/**  The size of the queue to store Passengers at the start of the simulation. */
	private final int PASSENGERS_QSIZE = 1000;	
	
	
	/**
	 * Instantiates a new elevator sim controller. 
	 * Reads the configuration file to configure the building and
	 * the elevator characteristics and also select the test
	 * to run. Reads the passenger data for the test to run to
	 * initialize the passenger queue in building...
	 *
	 * @param gui the gui
	 * Reviewed by Madi
	 */
	public ElevatorSimController(ElevatorSimulation gui) {
		this.gui = gui;
		fio = new MyFileIO();
		// IMPORTANT: DO NOT CHANGE THE NEXT LINE!!! Update the config file itself
		// (ElevatorSimConfig.csv) to change the configuration or test being run.
		configSimulation(SIM_CONFIG);
		NUM_FLOORS = numFloors;
		logfile = testfile.replaceAll(".csv", ".log");
		building = new Building(NUM_FLOORS,logfile, capacity, floorTicks, doorTicks, passPerTick);
		passQ = new GenericQueue<>(PASSENGERS_QSIZE);
		//TODO: YOU still need to configure the elevator in the building here....
		initializePassengerData(testfile);	
		
	}
	
	/**
	 * Config simulation. Reads the filename, and parses the
	 * parameters.
	 *
	 * @param filename the filename
	 * Reviewed by Madi
	 */
	private void configSimulation(String filename) {
		File configFile = fio.getFileHandle(filename);
		try ( BufferedReader br = fio.openBufferedReader(configFile)) {
			String line;
			while ((line = br.readLine())!= null) {
				parseElevatorConfigData(line);
			}
			fio.closeFile(br);
		} catch (IOException e) { 
			System.err.println("Error in reading file: "+filename);
			e.printStackTrace();
		}
	}
	
	/**
	 * Parses the elevator simulation config file to configure the simulation:
	 * number of floors and elevators, the actual test file to run, and the
	 * elevator characteristics.
	 *
	 * @param line the line
	 * @throws IOException Signals that an I/O exception has occurred.
	 * Reviewed by Madi
	 */
	private void parseElevatorConfigData(String line) throws IOException {
		String[] values = line.split(",");
		if (values[0].equals("numFloors")) {
			numFloors = Integer.parseInt(values[1]);
		} else if (values[0].equals("passCSV")) {
			testfile = values[1];
		} else if (values[0].equals("capacity")) {
			capacity = Integer.parseInt(values[1]);
		} else if (values[0].equals("floorTicks")) {
			floorTicks = Integer.parseInt(values[1]);
		} else if (values[0].equals("doorTicks")) {
			doorTicks = Integer.parseInt(values[1]);
		} else if (values[0].equals("passPerTick")) {
			passPerTick = Integer.parseInt(values[1]);
		}
	}
	
	/**
	 * Initialize passenger data. Reads the supplied filename,
	 * and for each passenger group, identifies the pertinent information
	 * and adds it to the passengers queue in Building...
	 *
	 * @param filename the filename
	 * Reviewed by Madi
	 */
	private void initializePassengerData(String filename) {
		boolean firstLine = true;
		File passInput = fio.getFileHandle(filename);
		try (BufferedReader br = fio.openBufferedReader(passInput)) {
			String line;
			while ((line = br.readLine())!= null) {
				if (firstLine) {
					firstLine = false;
					continue;
				}
				parsePassengerData(line);
			}
			fio.closeFile(br);
		} catch (IOException e) { 
			System.err.println("Error in reading file: "+filename);
			e.printStackTrace();
		}
		if (PASSQ_DEBUG) dumpPassQ();
	}	
	
	/**
	 * Parses the line of passenger data into tokens, and 
	 * passes those values to the building to be added to the
	 * passenger queue.
	 *
	 * @param line the line of passenger input data
	 * Reviewed by Madi
	 */
	private void parsePassengerData(String line) {
		int time=0, numPass=0,fromFloor=0, toFloor=0;
		boolean polite = true;
		int wait = 1000;
		String[] values = line.split(",");
		for (int i = 0; i < values.length; i++) {
			switch (i) {
				case 0 : time      = Integer.parseInt(values[i]); break;
				case 1 : numPass   = Integer.parseInt(values[i]); break;
				case 2 : fromFloor   = Integer.parseInt(values[i]); break;
				case 3 : toFloor  = Integer.parseInt(values[i]); break;
				case 5 : wait      = Integer.parseInt(values[i]); break;
				case 4 : polite = "TRUE".equalsIgnoreCase(values[i]); break;
			}
		}
		passQ.add(new Passengers(time,numPass,fromFloor,toFloor,polite,wait));	
	}
	
	/**
	 * Gets the number of floors in the building.
	 *
	 * @return the num floors
	 * Reviewed by Madi
	 */
	protected int getNumFloors() {
		return NUM_FLOORS;
	}
	
	/**
	 * Gets the elevator state.
	 *
	 * @return the elevator state
	 * Reviewed by Madi
	 */
	protected int getElevatorState() {
		return building.getElevatorState();
	}
	/**
	 * Gets the test name.
	 *
	 * @return the test name
	 * Reviewed by Madi
	 */
	public String getTestName() {
		return (testfile.replaceAll(".csv", ""));
	}

	/**
	 * Enable logging. A pass-through from the GUI to building
	 * Reviewed by Madi
	 */
	protected void enableLogging() {
		building.enableLogging();
	}
	
	/**
	 * Gets the elevator floor.
	 *
	 * @return the elevator floor
	 * Reviewed by Madi
	 */
	protected int getElevatorFloor() {
		return building.getElevator().getCurrFloor();
	}
	
	/**
	 * Gets the passengers in the elevator.
	 *
	 * @return the num passengers in elevator
	 * Reviewed by Madi
	 */
	protected int getPassInEl() {
		return building.getElevator().getPassengers();
	}
	
	/**
	 * Gets the num floors.
	 *
	 * @return the num floors
	 * Reviewed by Madi
	 */
	protected int getNUM_FLOORS() {
		return NUM_FLOORS;
	}

	/**
	 * Gets the capacity.
	 *
	 * @return the capacity
	 * Reviewed by Madi
	 */
	protected int getCapacity() {
		return capacity;
	}

	/**
	 * Gets the floor ticks.
	 *
	 * @return the floor ticks
	 * Reviewed by Madi
	 */
	protected int getFloorTicks() {
		return floorTicks;
	}

	/**
	 * Gets the door ticks.
	 *
	 * @return the door ticks
	 * Reviewed by Madi
	 */ 
	protected int getDoorTicks() {
		return doorTicks;
	}

	/**
	 * Gets the pass per tick.
	 *
	 * @return the pass per tick
	 * Reviewed by Madi
	 */
	protected int getPassPerTick() {
		return passPerTick;
	}

	/**
	 * Gets the step count.
	 *
	 * @return the step count
	 * Reviewed by Madi
	 */
	protected int getStepCnt() {
		return stepCnt;
	}

	/**
	 * Gets the passenger queue.
	 *
	 * @return the pass Q
	 * Reviewed by Madi
	 */
	protected GenericQueue<Passengers> getPassQ() {
		return passQ;
	}

	/**
	 * Gets the size of the passenger queue.
	 *
	 * @return the passengers qsize
	 * Reviewed by Madi
	 */
	protected int getPASSENGERS_QSIZE() {
		return PASSENGERS_QSIZE;
	}

	/**
	 * Gets the elevator direction.
	 *
	 * @return the elevator direction
	 * Reviewed by Madi
	 */
	protected int getElevatorDirection() {
		// TODO Auto-generated method stub
		return building.getElevatorDirection();
	}
	
	/**
	 * Gets the down list.
	 *
	 * @param i the i
	 * @return the down list
	 * Reviewed by Madi
	 */
	protected ListIterator<Passengers> getList(int f, int dir) {
		// TODO Auto-generated method stub
		return building.getList(f, dir);
	}
	
 	/**
	 * Step sim. See the comments below for the functionality you
	 * must implement......
	 * Reviewed by Madi
	 */
	protected void stepSim() {
 		// DO NOT MOVE THIS - YOU MUST INCREMENT TIME FIRST!
		stepCnt++;
		if(!passQ.isEmpty() || building.getElevatorState() != 0 || stepCnt == 128) {	
			checkPassengers();
			building.updateElevator(stepCnt);
			if(gui!=null) updateGUI(); 
		} else {
			building.updateElevator(stepCnt);
			building.closeLogs(stepCnt);
			building.processPassengerData();
			if(gui!=null) gui.stopTicks();
		}  
	}
	
	/**
	 * Update GUI.
	 * Reviewed by Madi
	 */
	private void updateGUI() {
		gui.updateLabels(stepCnt);
		gui.updateElevator();
		gui.updateCurrPass(1);
		gui.updateCurrPass(-1);
	}

	/**
	 * Check passengers and add or remove depending on time.
	 * Reviewed by Madi
	 */
	protected void checkPassengers() {
		while(true) {
			if(passQ.isEmpty())
				break;
			Passengers p = passQ.peek();
			if(p.getTime()==stepCnt) {
				building.addPassToFloor(p, stepCnt);
				p.setTimeArrived(stepCnt);
				passQ.remove();
			}
			else break;
		}
	}
	
	/**
	 * Gets the passengers on a floor.
	 *
	 * @param floor the desired floor
	 * @return the passengers on desired floor
	 * Reviewed by Madi
	 */
	protected Floor getPassOnFloor(int floor){
		return building.getPass(floor);
	}

	/**
	 * Dump passQ contents. Debug hook to view the contents of the passenger queue...
	 */
	public void dumpPassQ() {
		ListIterator<Passengers> passengers = passQ.getListIterator();
		if (passengers != null) {
			System.out.println("Passengers Queue:");
			while (passengers.hasNext()) {
				Passengers p = passengers.next();
				System.out.println(p);
			}
		}
	}


	/**
	 * Gets the building. ONLY USED FOR JUNIT TESTING - YOUR GUI SHOULD NOT ACCESS THIS!.
	 *
	 * @return the building
	 */
	Building getBuilding() {
		return building;
	}

	protected void disableLogging() {
		// TODO Auto-generated method stub
		building.disableLogging();
	}

	
}