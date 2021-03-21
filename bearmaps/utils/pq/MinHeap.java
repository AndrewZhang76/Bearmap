package bearmaps.utils.pq;

import javax.swing.text.Element;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;

/* A MinHeap class of Comparable elements backed by an ArrayList. */
public class MinHeap<E extends Comparable<E>> {

    /* An ArrayList that stores the elements in this MinHeap. */
    private int size;
    private HashMap<E,Integer> track;
    private ArrayList<E> contents;
    // TODO: YOUR CODE HERE (no code should be needed here if not
    // implementing the more optimized version)

    /* Initializes an empty MinHeap. */
    public MinHeap() {
        contents = new ArrayList<>();
        size=0;
        track = new HashMap<>();
    }

    /* Returns the element at index INDEX, and null if it is out of bounds. */
    private E getElement(int index) {
        if (index >= contents.size()) {
            return null;
        } else {
            return contents.get(index);
        }
    }

    /* Sets the element at index INDEX to ELEMENT. If the ArrayList is not big
       enough, add elements until it is the right size. */
    private void setElement(int index, E element) {
        while (index >= contents.size()) {
            contents.add(null);
        }
        contents.set(index, element);
        track.remove(element, index);
        track.put(element,index);
    }

    /* Swaps the elements at the two indices. */
    private void swap(int index1, int index2) {
        E element1 = getElement(index1);
        E element2 = getElement(index2);
        setElement(index2, element1);
        setElement(index1, element2);
    }

    /* Prints out the underlying heap sideways. Use for debugging. */
    @Override
    public String toString() {
        return toStringHelper(1, "");
    }

    /* Recursive helper method for toString. */
    private String toStringHelper(int index, String soFar) {
        if (getElement(index) == null) {
            return "";
        } else {
            String toReturn = "";
            int rightChild = getRightOf(index);
            toReturn += toStringHelper(rightChild, "        " + soFar);
            if (getElement(rightChild) != null) {
                toReturn += soFar + "    /";
            }
            toReturn += "\n" + soFar + getElement(index) + "\n";
            int leftChild = getLeftOf(index);
            if (getElement(leftChild) != null) {
                toReturn += soFar + "    \\";
            }
            toReturn += toStringHelper(leftChild, "        " + soFar);
            return toReturn;
        }
    }

    /* Returns the index of the left child of the element at index INDEX. */
    private int getLeftOf(int index) {
        // TODO: YOUR CODE HERE
        return index*2;
    }

    /* Returns the index of the right child of the element at index INDEX. */
    private int getRightOf(int index) {
        // TODO: YOUR CODE HERE
        return index*2+1;
    }

    /* Returns the index of the parent of the element at index INDEX. */
    private int getParentOf(int index) {
        // TODO: YOUR CODE HERE
        return index/2;
    }

    /* Returns the index of the smaller element. At least one index has a
       non-null element. If the elements are equal, return either index. */
    private int min(int index1, int index2) {
        // TODO: YOUR CODE HERE
        E e1 = getElement(index1);
        E e2 = getElement(index2);
        if (e2 == null) {
            return index1;
        }
        if (e1 == null) {
            return index2;
        }
        if (e1.compareTo(e2) > 0) {
            return index2;
        }
        return index1;

    }

    /* Returns but does not remove the smallest element in the MinHeap. */
    public E findMin() {
        // TODO: YOUR CODE HERE
        return getElement(1);

    }

    /* Bubbles up the element currently at index INDEX. */
    private void bubbleUp(int index) {
        if(index<=1){
            return;
        }
        E e1 = getElement(index);
        E e1_parent = getElement(getParentOf(index));
        if (e1.compareTo(e1_parent) < 0) {
            swap(index, getParentOf(index));
            bubbleUp(getParentOf(index));
        }

    }

    /* Bubbles down the element currently at index INDEX. */
    private void bubbleDown(int index) {
        if (getElement(index) == null) {
            return;
        }
        int leftindex  = getLeftOf(index);
        int rightindex = getRightOf(index);
        int swap_index = min(min(index, leftindex), rightindex);
        if (swap_index != index) {
            swap(swap_index, index);
            bubbleDown(swap_index);
        }
    }

    /* Returns the number of elements in the MinHeap. */
    public int size() {
        // TODO: YOUR CODE HERE
        return size;
    }

    /* Inserts ELEMENT into the MinHeap. If ELEMENT is already in the MinHeap,
       throw an IllegalArgumentException.*/
    public void insert(E element) {
        // TODO: YOUR CODE HERE
        if (contains(element)) {
            throw new IllegalArgumentException();
        }
        size++;
        setElement(size, element);
        bubbleUp(size);

    }

    /* Returns and removes the smallest element in the MinHeap. */
    public E removeMin() {
        // TODO: YOUR CODE HERE
        E e = getElement(1);
        swap(1,size);
        contents.remove(size);
        size--;
        track.remove(e);
        bubbleDown(1);
        return e;
    }

    /* Replaces and updates the position of ELEMENT inside the MinHeap, which
       may have been mutated since the initial insert. If a copy of ELEMENT does
       not exist in the MinHeap, throw a NoSuchElementException. Item equality
       should be checked using .equals(), not ==. */
    public void update(E element) {
        // TODO: YOUR CODE HERE
        if(!contains(element)){
            throw new NoSuchElementException();
        }
        int idx = track.get(element);
        E curr = getElement(idx);
        setElement(idx, element);
        int i = curr.compareTo(element);
        if (i>0){
            bubbleUp(idx);
        } else if (i<0) {
            bubbleDown(idx);
        }
    }

    /* Returns true if ELEMENT is contained in the MinHeap. Item equality should
       be checked using .equals(), not ==. */
    public boolean contains(E element) {
        // TODO: YOUR CODE HERE
        return track.containsKey(element);

    }
}
