
import java.util.ArrayList;

import building.Elevator;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.stage.Stage;
//import javax.imageio.ImageIO;
import javafx.util.Duration;
import passengers.Passengers;


// TODO: Auto-generated Javadoc
/**
 * The Class ElevatorSimulation. @author Karina Asanbekova
 */
public class ElevatorSimulation extends Application {
	
	/**  Instantiate the GUI fields. */
	private ElevatorSimController controller;
	
	/** The num floors. */
	private final int NUM_FLOORS;
	
	/**  Constants for direction. */
	private final static int UP = 1;
	
	/** The Constant DOWN. */
	private final static int DOWN = -1;
	
	/** The curr floor. */
	private int currFloor;
	
	/** The direction. */
	private int direction;
	
	/** The elevator state. */
	private int elevatorState;
	
	/** The passengers. */
	private int passengers;
	
	/** The time. */
	private int time;
	
	/**  you MUST use millisPerTick as the duration for your timeline. */
	private static int millisPerTick = 250;

	/**  Local copies of the states for tracking purposes. */
	private final int STOP = Elevator.STOP;
	
	/** The mvtoflr. */
	private final int MVTOFLR = Elevator.MVTOFLR;
	
	/** The opendr. */
	private final int OPENDR = Elevator.OPENDR;
	
	/** The offld. */
	private final int OFFLD = Elevator.OFFLD;
	
	/** The board. */
	private final int BOARD = Elevator.BOARD;
	
	/** The closedr. */
	private final int CLOSEDR = Elevator.CLOSEDR;
	
	/** The mv1flr. */
	private final int MV1FLR = Elevator.MV1FLR;
	
	/** The open elevator. */
	private ImageView openElevator, up, down, stop, mostRecentEl;
	
	/** The cats. */
	private String[] catsPath;
	
	/** The sly path. */
	private String slyPath;
	
	/** The t. */
	private Timeline t;
	
	/** The log. */
	private Button run, stopButton, step, log;
	   
	/** The down L. */
	private Label speed, totalTime, numPass, elFloor, state, stepN, upL, downL;
	
	/** The main. */
	private BorderPane main;
	
	/** The init Y. */
	private int initY;
	
	/** The step N text. */
	private TextField stepNText;
	
	/** The overall floors. */
	private VBox overallFloors;
	
	/** The floors. */
	private floors[] floors;
	
	/** The up arrow. */
	private Polygon upArrow = new Polygon();  //Up
	
	/** The down arrow. */
	private Polygon downArrow = new Polygon();
	
	/** The elevator Y. */
	private double elevatorY;
	
	private boolean isLogOn;
	
	/**
	 * Instantiates a new elevator simulation.
	 */
	public ElevatorSimulation() {
		controller = new ElevatorSimController(this);	
		NUM_FLOORS = controller.getNumFloors();	
		isLogOn = false;
	}

	/**
	 * Start.
	 *
	 * @param primaryStage the primary stage
	 * @throws Exception the exception
	 * Reviewed by: Madi
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		// You need to design the GUI. Note that the test name should
		// appear in the Title of the window!!
		primaryStage.setTitle("Elevator Simulation - "+ controller.getTestName());
		primaryStage.show();
		primaryStage.setResizable(false);
		//TODO: Complete your GUI, including adding any helper methods.
		//      Meet the 30 line limit...
		main = new BorderPane();
		Scene scene = new Scene(main, 500, 640);
		primaryStage.setScene(scene);
		setUpAll();
	}
	
	/**
	 * The floors class is a VBox that has two HBoxs, one for upQ and downQ.
	 * Reviewed by: Madi
	 */
	private class floors extends VBox {
		
		/** The up Q. */
		private HBox upQ;
		
		/** The down Q. */
		private HBox downQ;
		
		/** The name. */
		private String name;
		
		/** The up Q list. */
		private ArrayList<Passengers> upQList; 
		
		/** The down Q list. */
		private ArrayList<Passengers> downQList;
		
		/**
		 * Instantiates a new floors.
		 *
		 * @param name the name
		 * Reviewed by Madi
		 */
		public floors(String name) {
			this.name = name;
			upQList = new ArrayList<Passengers>();
			downQList = new ArrayList<Passengers>();
			upQ = new HBox();
			this.getChildren().add(upQ);
			//overallFloors.getChildren().add(upQ);
			downQ = new HBox();
			this.getChildren().add(downQ);
			upQ.prefHeightProperty().bind(this.heightProperty().divide(2));
			downQ.prefHeightProperty().bind(this.heightProperty().divide(2));
		}
		
		/**
		 * Adds to the up Q.
		 *
		 * @param c the cat(s)
		 * Reviewed by Madi
		 */
		protected void addUpQ(VBox c) {
			upQ.getChildren().add(c);
			
		}
		
		/**
		 * Adds to the down Q.
		 *
		 * @param c the cat(s)
		 * Reviewed by Madi
		 */
		protected void addDownQ(VBox c) {
			downQ.getChildren().add(c);
		}
		
		/**
		 * Removes the front passenger group in appropriate direction.
		 *
		 * @param dir the direction of the passenger group
		 * Reviewed by Madi
		 */
		protected void removeFront(int dir) {
			if(dir == UP) {
				upQ.getChildren().remove(0, 1);
				upQList.remove(0);
			} else {
				downQ.getChildren().remove(0, 1);
				downQList.remove(0);
			}
		}
		
		/**
		 * Gets the passenger arraylist in appropriate direction.
		 *
		 * @param dir the direction of requested passenger group
		 * @return the appropriate passenger group arraylist
		 * Reviewed by Madi
		 */
		protected ArrayList<Passengers> getPass(int dir) {
			if(dir == UP) return upQList;
			return downQList;
		}		
	}
	
	
	/**
	 * Sets up GUI components.
	 * Reviewed by Madi
	 */
	private void setUpAll() { 
		setUpBottomButtons();
		setUpTopInfoLabels();
		setUpBackground();
		setUpElevators();
		setUpCats();
		setUpFloors(); 
		setUpArrows();
	}
	
	
	/**
	 * Sets up the arrows used to demonstrate elevator direction.
	 * Reviewed by Madi
	 */
	private void setUpArrows() {
		// TODO Auto-generated method stub
		upArrow.getPoints().addAll(2.5, 10.0, 12.5, 10.0, 7.5, 10 - 5*Math.pow(3, 0.5));
		upArrow.setStroke(Color.BLACK);
		upArrow.setStrokeWidth(2);
		upArrow.setFill(Color.BLUE);
		downArrow.getPoints().addAll(upArrow.getPoints());
		downArrow.setRotate(180);
		downArrow.setStroke(Color.BLACK);
		downArrow.setStrokeWidth(2);
		downArrow.setFill(Color.RED);
	}

	/**
	 * Sets up the background.
	 * Reviewed by Madi
	 */
	private void setUpBackground() {
		// TODO Auto-generated method stub
		ImageView bckGrnd = new ImageView("file:src/ElevatorPain_Building.png");
		main.getChildren().add(bckGrnd);
		bckGrnd.setY(20);
	}

	/**
	 * Sets up the floors.
	 * Reviewed by Madi
	 */
	private void setUpFloors() {
		overallFloors = new VBox();
		main.setCenter(overallFloors);
		floors = new floors[6];
		for(int i = 5; i >= 0; i--) {
			floors[i] = new floors("Floor " + (i + 1));
			floors curr = floors[i];
			overallFloors.getChildren().add(floors[i]);
			curr.prefHeightProperty().bind(overallFloors.heightProperty().divide(NUM_FLOORS));
		}
	}



	/**
	 * Sets up the elevators.
	 * Reviewed by Madi
	 */
	private void setUpElevators() {
		// TODO Auto-generated method stub
		initY = 500;
		up = new ImageView("file:src/ElevatorPain_Elevator-UP.png");
		main.getChildren().add(up);
		up.setY(initY);
		up.setVisible(false);
		down = new ImageView("file:src/ElevatorPain_Elevator-DOWN.png");
		main.getChildren().add(down);
		down.setY(initY);
		down.setVisible(false);
		stop = new ImageView("file:src/ElevatorPain_Elevator-STOP.png");
		main.getChildren().add(stop);
		stop.setY(initY);
		openElevator = new ImageView("file:src/ElevatorPain_Elevator-OPEN.png");
		main.getChildren().add(openElevator);
		openElevator.setY(initY);
		openElevator.setVisible(false);
		mostRecentEl = stop;
		
		Pane elevatorPane = new Pane();
		elevatorPane.prefWidthProperty().bind(main.widthProperty().divide(6));
		main.setLeft(elevatorPane);
	}

	/**
	 * Starts the timeline.
	 * Reviewed by Madi
	 */
	private void initTimeline() {
		//TODO: Code this method
		t = new Timeline(new KeyFrame(Duration.millis(millisPerTick), ae -> controller.stepSim()));
		t.setCycleCount(Animation.INDEFINITE);
		t.play();
		speed.setText("Speed: " + t.getCycleDuration());
	}

	/**
	 * Sets up the cats.
	 * Reviewed by Madi
	 */
	private void setUpCats() {
		catsPath = new String[9];
		for(int i = 0; i < 9; i++) {
			catsPath[i] = "file:src/resources/cats/cat" + i + ".png";
		}
		slyPath = "file:src/resources/cats/slycat.png";
	}
	
	/**
	 * Sets up the info displayed at top of window.
	 * Reviewed by Madi
	 */
	private void setUpTopInfoLabels() {
		// TODO Auto-generated method stub
		totalTime = new Label("Time: " + controller.getStepCnt());
		numPass = new Label("Passengers in elevator: " + controller.getBuilding().getElevator().getPassengers());
		elFloor = new Label("Current floor: " + (controller.getElevatorFloor() + 1));
		speed = new Label("Speed: " + millisPerTick + ".0 ms");
		state = new Label("State: " + controller.getElevatorState());
		upL = new Label ("Elevator going up");
		downL = new Label ("Elevator going down");
		HBox info = new HBox(20, totalTime, numPass, elFloor, speed);
		HBox infoCont = new HBox(20, state, upArrow, upL, downArrow, downL);
		VBox top = new VBox(info, infoCont);
		main.setTop(top);
	}

	/**
	 * Sets up the bottom buttons.
	 * Reviewed by Madi
	 */
	private void setUpBottomButtons() {
		// TODO Auto-generated method stub
		run = new Button("Run");
		run.setOnAction(e -> initTimeline());
		stopButton = new Button("Stop");
		stopButton.setOnAction(e -> t.pause());
		step = new Button("Step");
		step.setOnAction(e -> stepMethod());
		stepN = new Label("Step By: ");
		stepNText = new TextField("1");
		log = new Button("Log");
		log.setOnAction(e -> {
			isLogOn = !isLogOn;
			if(isLogOn) controller.enableLogging();
			else controller.disableLogging();
		});
		HBox buttons = new HBox(20, run, stopButton, step, log, stepN, stepNText);
		main.setBottom(buttons);	
	}

	/**
	 * Updates GUI by step amount.
	 * Reviewed by Madi
	 */
	private void stepMethod() {
		// TODO Auto-generated method stub
		int stepAmount = 0;
		try{
			stepAmount = Integer.parseInt(stepNText.getText());
		} 
		catch (Exception e) {
			sendNumAlert("Must be an integer");
			return;
		}
		if(stepAmount <= 0) sendNumAlert("Integer must be positive (not including 0)");
		else {
			for(int i = 0; i < stepAmount; i++) {
				controller.stepSim();
			}
		}
	}

	/**
	 * Sends an invalid number alert.
	 *
	 * @param message the warning message
	 * Reviewed by Madi
	 */
	private void sendNumAlert(String message) {
		// TODO Auto-generated method stub
		Alert alert = new Alert(AlertType.WARNING);
		alert.setHeaderText("Invalid Number");
		alert.setContentText(message);
		alert.showAndWait();
	}

	/** 
	 * The main method. Allows command line to modulate the speed of the simulation.
	 *
	 * @param args the arguments
	 * Reviewed by Madi
	 */
	public static void main (String[] args) {
		if (args.length>0) {
			for (int i = 0; i < args.length-1; i++) {
				if ("-m".equals(args[i])) {
					try {
						ElevatorSimulation.millisPerTick = Integer.parseInt(args[i+1]);
					} catch (NumberFormatException e) {
						System.out.println("Unable to update millisPerTick to "+args[i+1]);
					}
				}
			}
		}
		Application.launch(args);
	}
	
		
	/**
	 * Stops timeline when simulation ended.
	 * Reviewed by Madi
	 */
	public void stopTicks() {
		t.stop();
		step.setDisable(true);
	}
	

	/**
	 * Updates currently visible passengers.
	 *
	 * @param dir the direction of the passenger group
	 * Reviewed by Madi
	 */
	protected void updateCurrPass(int dir) {
		//get the list of passenegers on the floor, go by floor
		ArrayList<Passengers> list;
		Passengers pass;
		for(int i = 0; i < NUM_FLOORS; i++) {
			list = floors[i].getPass(dir);
			for(int j = 0; j < list.size(); j++) {
				pass = list.get(j);
				if(pass.getBoardTime() >= time || pass.getTimeWillGiveUp() <= time) {
					floors[i].removeFront(dir);
					j--;
				}
				else break;
			}
			controller.getList(i, dir).forEachRemaining(p -> addAppropriately(p));
		}
		
	}

	/**
	 * Adds the passenger group appropriately to GUI.
	 *
	 * @param pass the passenger group to be added
	 * Reviewed by Madi
	 */
	private void addAppropriately(Passengers pass) {
		// TODO Auto-generated method stub
		if(pass != null && !floors[pass.getOnFloor()].getPass(pass.getDir()).contains(pass)) {
			int initPassFloor = pass.getOnFloor();
			floors[initPassFloor].getPass(pass.getDir()).add(pass);
			VBox addCat = createCatImage(pass);
			if(pass.getDir() == UP) floors[initPassFloor].addUpQ(addCat);
			else floors[initPassFloor].addDownQ(addCat);
		}
	}
	
	/**
	 * Creates the cat image.
	 *
	 * @param pass the passenger group
	 * @return the v box of the cats
	 * Reviewed by Madi
	 */
	private VBox createCatImage(Passengers pass) {
		VBox catBox = new VBox();
		ImageView addCat = pass.getPolite()? new ImageView(catsPath[pass.getNumPass() % 9]): new ImageView(slyPath); 
		Label catID = new Label("   ID: " + pass.getId());
		catID.setMinHeight(5);
		catID.setTextFill(Color.WHITE);
		catID.setFont(new Font(8.0));
		Label numCats = new Label("   #Pass: " + pass.getNumPass());
		numCats.setMinHeight(5);
		numCats.setTextFill(Color.WHITE);
		numCats.setFont(new Font(8.0));
		catBox.getChildren().addAll(catID, numCats, addCat);
		return catBox;
	}

	/**
	 * Shows appropriate elevator and updates it.
	 * Reviewed by Madi
	 */
	protected void updateElevator() { //figure out how many pixels to move by per time
		// TODO Auto-generated method stub
		elevatorY = mostRecentEl.getY();
		boolean isMoving = (elevatorState == MVTOFLR || elevatorState == MV1FLR)? true: false;
		mostRecentEl = isMoving? ((controller.getElevatorDirection() > 0)? 
				setVisible("up"): setVisible("down")): setVisible("neither");
		
	}

	/**
	 * Sets the appropriate elevator visible.
	 *
	 * @param dir the direction of the elevator
	 * @return the visible elevator
	 * Reviewed by Madi
	 */
	private ImageView setVisible(String dir) {
		// TODO Auto-generated method stub
		ImageView visElevator = "neither".equals(dir)? stop: ("up".equals(dir)? up: down);
		visElevator.setVisible(true);
		visElevator.setY(elevatorY + (visElevator.equals(stop)? 0: (visElevator.equals(up)? -19: 19)));
		boolean isStop = visElevator.equals(stop);
		(isStop? down: stop).setVisible(false);
		(isStop? up: stop).setVisible(false);
		openElevator.setY(visElevator.getY());
		openElevator.setVisible((elevatorState == OPENDR || elevatorState == BOARD || elevatorState == OFFLD)
				? true: false);
		return visElevator;
	}

	/**
	 * Changes the label according to the direction of the elevator.
	 *
	 * @param dir the direction of the elevator
	 * Reviewed by Madi
	 */
	private void lDirChange(int dir) {
		// TODO Auto-generated method stub
		boolean dirUp = (dir == UP);
		(dirUp? upL: downL).setText("Elevator going " + (dirUp? "UP": "DOWN"));
		(dirUp? downL: upL).setText("Elevator going " + (dirUp? "down": "up"));
		(dirUp? upArrow: downArrow).setStroke(dirUp? Color.BLUE: Color.RED);
		(dirUp? downArrow: upArrow).setStroke(Color.BLACK);
	}

	/**
	 * Appropriately updates labels.
	 *
	 * @param stepCnt time used to update time label
	 * Reviewed by Madi
	 */
	protected void updateLabels(int stepCnt) {
		// TODO Auto-generated method stub
		time = stepCnt;
		currFloor = controller.getElevatorFloor();
		passengers = controller.getPassInEl();
		elevatorState = controller.getElevatorState();
		speed.setText("Speed: " + millisPerTick + ".0 ms");
		totalTime.setText("Time: " + time);
		elFloor.setText("Current Floor: " + (currFloor + 1));
		numPass.setText("Passengers in elevator: " + passengers);
		state.setText("State: " + stringState());
		if(controller.getElevatorDirection() != 0) lDirChange(controller.getElevatorDirection());
		
	}

	/**
	 * Converts the elevator state to a string.
	 *
	 * @return the string version of the elevator state
	 * Reviewed by Madi
	 */
	private String stringState() {
		// TODO Auto-generated method stub
		switch(elevatorState){
		case(STOP):
			return "Stop";
		case(MVTOFLR):
			return "Moving to Floor";
		case(OPENDR):
			return "Doors Opening";
		case(OFFLD):
			return "Off-loading";
		case(BOARD):
			return "Boarding";
		case(CLOSEDR):
			return "Doors Closing";
		case(MV1FLR):
			return "Moving 1 Floor";
		}
		return "";
	}
}
