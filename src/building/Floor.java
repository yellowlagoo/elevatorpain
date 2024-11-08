package building;
// ListIterater can be used to look at the contents of the floor queues for 
// debug/display purposes...
import java.util.ListIterator;

import genericqueue.GenericQueue;
import passengers.Passengers;

// TODO: Auto-generated Javadoc
/**
 * The Class Floor. This class provides the up/down queues to hold
 * Passengers as they wait for the Elevator.
 * 
 * @author Iz
 */
public class Floor {
	/**  Constant for representing direction. */
	private static final int UP = 1;
	
	/** The Constant DOWN. */
	private static final int DOWN = -1;

	/**  The queues to represent Passengers going UP or DOWN. */	
	private GenericQueue<Passengers> down;
	
	/** The up. */
	private GenericQueue<Passengers> up;
	
	/**
	 * Instantiates a new floor.
	 *
	 * @param qSize the q size
	 * Reviewed by: Karina Asanbekova
	 */
	public Floor(int qSize) {
		down = new GenericQueue<Passengers>(qSize);
		up = new GenericQueue<Passengers>(qSize);
	}
	
	/**
	 * Checks if there are no calls in either queue on the current floor.
	 *
	 * @return true if no calls in both up/down queue, false otherwise
	 * Reviewed by: Karina Asanbekova
	 */
	protected boolean noCalls() {
		return (up.size()==0 && down.size()==0);
	}
	
	/**
	 * Checks if the queue for a given direction is empty.
	 *
	 * @param dir the direction
	 * @return true, if queue is empty
	 * Reviewed by: Karina Asanbekova
	 */
	protected boolean isEmpty(int dir) {
		if(dir==UP)
			return up.isEmpty();
		return down.isEmpty();
	}
	
	/**
	 * Returns the size of the queue for a given direction.
	 * 
	 * @param dir the direction
	 * @return the size
	 * Reviewed by: Karina Asanbekova
	 */
	protected int size(int dir) {
		if(dir==UP)
			return up.size();
		else
			return down.size();
	}
	
	/**
	 * Returns group at head of queue in a given direction 
	 * (if possible), returns null if queue is empty.
	 *
	 * @param dir the direction
	 * @return the passenger group
	 * Reviewed by: Karina Asanbekova
	 */
	public Passengers peek(int dir) {
		if(dir==UP)
			return up.peek();
		return down.peek();
	}
	
	/**
	 * Removes group at head of queue in a given direction
	 * (if possible), returns null if queue is empty.
	 *
	 * @param dir the direction
	 * @return the passengers
	 * Reviewed by: Karina Asanbekova
	 */
	protected Passengers poll(int dir) {
		if(dir==UP)
			return up.poll();
		return down.poll();
	}
	
	/**
	 * Adds passenger group to the queue for the 
	 * direction they want to travel.
	 *
	 * @param p the passenger group
	 * @param dir the direction
	 * Reviewed by: Karina Asanbekova
	 */
	protected void add(Passengers p, int dir) {
		if(dir==UP)
			up.add(p);
		else
			down.add(p);
	}
	
	/**
	 * Gets the passengers in the current direction.
	 *
	 * @param dir the dir
	 * @return the list
	 * Reviewed by: Karina Asanbekova
	 */
	protected ListIterator<Passengers> getList(int dir) {
		if(dir == UP)
			return up.getListIterator();
		return down.getListIterator();
	}
	
	/**
	 * Queue string. This method provides visibility into the queue
	 * contents as a string. What exactly you would want to visualize 
	 * is up to you
	 *
	 * @param dir determines which queue to look at
	 * @return the string of queue contents
	 */
	String queueString(int dir) {
		String str = "";
		ListIterator<Passengers> list;
		list = (dir == UP) ?up.getListIterator() : down.getListIterator();
		if (list != null) {
			while (list.hasNext()) {
				// choose what you to add to the str here.
				// Example: str += list.next().getNumPass();
				if (list.hasNext()) str += ",";
			}
		}
		return str;	
	}
	
}