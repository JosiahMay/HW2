package cs455.scaling.Containers;

import java.util.HashSet;
/**
 * A custom synchronized set for uses when java.util.concurrent.SynchronousSet is not allowed
 * in assignment.
 * @param <T> The type of Object being stored in the queue
 */
public class SynchronizedSet<T> {

  /**
   * A Set of all objects
   */
  private final HashSet<T> set;

  /**
   * Default Constructor that initializes the HashSet
   */
  public SynchronizedSet(){
    this.set = new HashSet<>();
  }

  /**
   * Returns the size of the HashSet
   * @return The size of the set
   */
  public synchronized int size() {
    return set.size();
  }

  /**
   * Checks to see if the set is empty or not
   * @return is the set empty
   */
  public synchronized boolean isEmpty() {
    return set.isEmpty();
  }

  /**
   * Adds an object to the end the HashSet
   * @param o The object to add
   * @return Was the object added successfully
   */
  public synchronized boolean add(T o) {
    return set.add(o);
  }


  /**
   * Removes the object from the set
   * @return if the object was removed from the set
   */
  public synchronized boolean remove(T o) {
    return set.remove(o);
  }

  /**
   * Clears the set of all objects
   */
  public synchronized void clear(){
    set.clear();
  }

  public synchronized void  printContents(){
    for (T item: this.set) {
      System.out.println("<" + item + ">");
    }
  }
}
