package cs455.scaling.util;

import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * A custom synchronized queue for uses when java.util.concurrent.SynchronousQueue is not allowed
 * in assignment.
 * @param <T> The type of Object being stored in the queue
 */
public class SynchronizedQueue<T> {

  /**
   * A link list to store the objects. I used a LinkedList because it is faster than a arraylist
   * when removing the first element
   */
  private final LinkedList<T> queue;

  /**
   * Default Constructor that initializes the linkedList
   */
  public SynchronizedQueue(){
    this.queue = new LinkedList<>();
  }

  /**
   * Returns the size of the linked list
   * @return The size of the list
   */
  public synchronized int size() {
    return queue.size();
  }

  /**
   * Checks to see if the list is empty or not
   * @return is the list empty
   */
  public synchronized boolean isEmpty() {
    return queue.isEmpty();
  }

  /**
   * Adds an object to the end the linked list
   * @param o The object to add
   * @return Was the object added successfully
   */
  public synchronized boolean add(T o) {
    return queue.add(o);
  }


  /**
   * Returns the first object in the queue
   * @return The object
   * @throws NoSuchElementException Thrown when called on an empty queue
   */
  public synchronized T remove() throws NoSuchElementException {
    return queue.remove();
  }

}
