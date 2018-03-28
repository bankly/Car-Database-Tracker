/**
 * IndexMinPQ Modified
 *
 * Heap indirection uses a tree map data structure
 * This is for logarithmic runtimes of retrieval, update, and removal functions
 *
 * There are 2 heaps and 2 indirection tables
 * One pair is for Price, the other is for Mileage
 *
 * @author Jonathan Chang
 */

import java.util.TreeMap;
import java.util.Arrays;

public class IndexMinPQ {
    //for all cars
    private int n;           // number of elements on PQ
    private Car[] pqPrice; //heap for price
    private Car[] pqMileage; //heap for mileage
    private TreeMap indexPrice; //stored as (VIN key, heap index value) INDEX for all cars
    private TreeMap indexMileage;

    //for make/model cars
    private TreeMap extraP;
    private TreeMap extraM;
    //to return
    private TreeMap tP;
    private TreeMap tM;
    private TreeMap countMMP;
    private TreeMap countMMM;
    //int toggle used throughout is 1 for mileage, 2 for price

    //INITIALIZE
    public IndexMinPQ()  {
        n=0;
        pqPrice = new Car[255];
        pqMileage = new Car[255];
        indexPrice = new TreeMap();
        indexMileage = new TreeMap();
        extraP = new TreeMap();
        extraM = new TreeMap();
        tP = new TreeMap();
        tM = new TreeMap();
        countMMP = new TreeMap();
        countMMM = new TreeMap();
    }

    /***********************************************************************************
     * ***************  MAJOR FUNCTIONS*************************************************
     * *********************************************************************************
     *
     *
     * 1) Add new car option
     * @param c the car added
     * @param VIN the VIN of the car
     */
    public void insert(Car c, String VIN) {
        n++;
        if(full()) resize();
        pqMileage[n]=c;
        pqPrice[n]=c;
        indexMileage.put(VIN, n);
        indexPrice.put(VIN, n);
        swim(n, 1, pqMileage, indexMileage); //lowest value comes up to first index
        swim(n, 2, pqPrice, indexPrice);
        swapCheck(c, 1);
        swapCheck(c, 2);
    }

    public void swapCheck(Car c, int toggle){//for make model updates
        TreeMap t;
        TreeMap m;
        TreeMap count;
        if(toggle ==1){
            t= tM;
            m=extraM;
            count=countMMM;
        }
        else{
            t=tP;
            m=extraP;
            count=countMMP;
        }
        if(!t.containsKey(c.getMake())){
            Car[] heap = new Car[255];
            heap[1] = c;
            t.put(c.getMake(), heap);
            m.put(c.getVIN(), 1);
            count.put(c.getMake(),1);
        }
        else{ //gets the heap we need
            Car [] old = (Car []) t.get(c.getMake());
            int num = (int) count.get(c.getMake()); //get saved count for this make/model
            num++;
            m.put(c.getVIN(), num);
            count.put(c.getMake(), num);
            old[num] = c;
            //swim
            swim(num, toggle, old, m);
            t.put(c.getMake(), old);
        }
    }

    /**
     * 2) Update car option
     * @param VIN the identifier for a car
     * @param price the new price for a car
     * @param mileage the new mileage for a car
     * @param color the new color for a car
     */
    public void updating(Car c, String VIN, int price, int mileage, String color){
        int indexor;
        if(price>0){
            //update and resort price heap
            indexor = (int) indexPrice.get(VIN); //saves the index
            pqPrice[indexor].setPrice(price);
            swim(indexor, 2, pqPrice, indexPrice); //places updated car in correct position in heap
            sink(indexor, 2, pqPrice, indexPrice, n);

            //just update the mileage heap value
            indexor = (int) indexMileage.get(VIN); //saves the index
            pqMileage[indexor].setPrice(price);

            indexor = (int) extraP.get(VIN);
            Car[] temp = (Car[]) tP.get(c.getMake());
            int len = (int) countMMP.get(c.getMake());
            temp[indexor].setPrice(price);
            swim(indexor, 2, temp, extraP);
            sink(indexor, 2, temp, extraP, len);
            sink(1,2,temp,extraP,len);
            tP.put(c.getMake(), temp);

            indexor = (int) extraM.get(VIN);
            temp = (Car[]) tM.get(c.getMake());
            temp[indexor].setPrice(price);
            tM.put(c.getMake(), temp);
            System.out.println("Price updated successfully!");
            return;
        }
        if(mileage>0){
            indexor = (int) indexMileage.get(VIN); //saves the index
            pqMileage[indexor].setMileage(mileage);
            swim(indexor, 1, pqMileage, indexMileage);
            sink(indexor, 1, pqMileage, indexMileage, n);

            indexor = (int) indexPrice.get(VIN); //saves the index
            pqPrice[indexor].setMileage(mileage);

            indexor = (int) extraM.get(VIN);
            Car[] temp = (Car[]) tM.get(c.getMake());
            int len = (int) countMMM.get(c.getMake());
            temp[indexor].setMileage(mileage);
            swim(indexor, 1, temp, extraM);
            sink(indexor, 1, temp, extraM, len);
            sink(1,1,temp,extraM,len);
            extraM.put(VIN, indexor);
            tM.put(c.getMake(), temp);

            indexor = (int) extraP.get(VIN);
            temp = (Car[]) tP.get(c.getMake());
            temp[indexor].setMileage(mileage);
            tP.put(c.getMake(), temp);

            System.out.println("Mileage updated successfully!");
            return;
        }
        if(!color.equals(" ")){ //update in both heaps, no resort needed
            indexor = (int) indexMileage.get(VIN);
            pqMileage[indexor].setColor(color);

            indexor = (int) indexPrice.get(VIN);
            pqPrice[indexor].setColor(color);

            indexor = (int) extraM.get(VIN);
            Car[] temp = (Car[]) tM.get(c.getMake());
            temp[indexor].setColor(color);
            tM.put(c.getMake(), temp);

            indexor = (int) extraP.get(VIN);
            temp = (Car[]) tP.get(c.getMake());
            temp[indexor].setColor(color);
            tP.put(c.getMake(), temp);

            System.out.println("Color updated successfully!");
            return;
        }
    }

    /**
     * 3) Remove a car option
     * @param VIN the identifier for a car
     */
    public void delete(Car c, String VIN) {
        if(isEmpty()){
            System.out.println("No more cars");
            return;
        }
        int indexor;
        //ALL CARS
        indexor = (int) indexMileage.get(VIN);
        exch(indexor, n, pqMileage, indexMileage); //puts car at end, puts last car where this car used to be
        pqMileage[n]=null; //removes the car from heap
        swim(indexor, 1, pqMileage, indexMileage); //these two rearrange the cars to be in new order
        sink(indexor, 1, pqMileage, indexMileage, n);
        indexMileage.remove(VIN); //removes reference to car in map
        //same for price
        indexor = (int) indexPrice.get(VIN);
        exch(indexor, n, pqPrice, indexPrice);
        pqPrice[n]=null;
        swim(indexor, 2, pqPrice, indexPrice);
        sink(indexor, 2, pqPrice, indexPrice, n);
        indexPrice.remove(VIN);
        n--;

        //MAKE MODEL CARS
        if(tM.get(c.getMake())==null) return;
        indexor = (int) extraM.get(VIN); //got index we need
        Car[] temp = (Car[]) tM.get(c.getMake()); //gets heap we need for this make/model
        int len = (int) countMMM.get(c.getMake()); //gets size we need
        if(len-1==0){ //removed last car of this make/model
            tM.remove(c.getMake());
            countMMM.remove(c.getMake());
            extraM.remove(VIN);
        }
        else{
            exch(indexor, len, temp, extraM);
            temp[len]=null;
            swim(indexor, 1, temp, extraM);
            sink(indexor, 1, temp, extraM, len);
            tM.put(c.getMake(), temp);
            countMMM.put(c.getMake(), --len);
            extraM.remove(VIN);
        }

        //same for price
        indexor = (int) extraP.get(VIN);
        temp = (Car[]) tP.get(c.getMake());
        len = (int) countMMP.get(c.getMake());
        if(len-1==0){ //removed last car of this make/model
            tP.remove(c.getMake());
            countMMP.remove(c.getMake());
            extraP.remove(VIN);
        }
        else{
            exch(indexor, len, temp, extraP);
            temp[len]=null;
            swim(indexor, 2, temp, extraP);
            sink(indexor, 2, temp, extraP, len);
            tP.put(c.getMake(), temp);
            countMMP.put(c.getMake(), --len);
            extraP.remove(VIN);
        }

        System.out.println("Remove completed successfully!");
    }

    /**
     * 4 & 5) Returns the Car with the minimum value (price or mileage) in the heap
     * @param toggle whether getting mileage or price
     * @return null if not found, or the minimum car
     */
    public Car getMin(int toggle){
        if (isEmpty()) return null;
        if(toggle==1) return pqMileage[1];
        else return pqPrice[1];
    }

    /**
     * 6 & 7) Returns the car with minimum value (price or mileage), specific make/model
     * @param toggle whether getting lowest mileage/price
     * @param make make of car
     * @param model model of car
     * @return minimum make/model car, or null if not found
     */
    public Car minMM(int toggle, String make, String model){
        if(isEmpty()) return null;
        String id = make+model;
        if(toggle==1){
            if(tM.get(id)==null) return null;
            Car [] temp = (Car[]) tM.get(id);
            return temp[1];
        }
        else{
            if(tP.get(id)==null) return null;
            Car [] temp = (Car[]) tP.get(id);
            return temp[1];
        }
    }

    /***************************************************************************
     * General helper functions.
     * These two methods are from the book code, IndexMinPQ.java
     * edited to allow for choice of either price or mileage functions (toggle)
     * exchange updates the relevant heap and its relevant index tree
     ***************************************************************************/
    private boolean greater(int i, int j, int toggle, Car[] heap) {
        if(heap[i] ==null || heap[j] == null){
            return false;
        }
        if(toggle==1){
            return heap[i].getMileage() > heap[j].getMileage();
        }
        else{
            return heap[i].getPrice() > heap[j].getPrice();
        }
    }

    private void exch(int i, int j, Car[] heap, TreeMap t) {
        Car swap = heap[i];
        heap[i] = heap[j];
        heap[j] = swap;

        t.put(heap[i].getVIN(), i);
        t.put(heap[j].getVIN(), j);
    }

    /***************************************************************************
     * Heap helper functions.
     * These two methods are also from the same book code
     * Edited to allow for either price or mileage functions
     ***************************************************************************/
    private void swim(int k, int toggle, Car[] heap, TreeMap t) {
        while (k > 1 && greater(k/2, k, toggle, heap)) {
            exch(k, k/2, heap, t);
            k = k/2;
        }
    }

    private void sink(int k, int toggle, Car[] heap, TreeMap t, int n) {
        while (2 * k <= n) {
            int j = 2 * k;
            if (j < n && greater(j, j + 1, toggle, heap)) j++;
            if (!greater(k, j, toggle, heap)) break;
            exch(k, j, heap, t);
            k = j;
        }
    }

    /***************************************************************************************************************
     *  MORE USEFUL FUNCTIONS
     *  ***********************************************************************************************************
     *
     * Returns true if this priority queue is empty.
     * @return {@code true} if this priority queue is empty;
     * {@code false} otherwise
     */
    private boolean isEmpty() {
        return n == 0;
    }

    /**
     * Determines if the heap is full
     * @return True if the heap is a full
     */
    private boolean full(){
        return n == pqPrice.length;
    }

    /**
     * Doubles the available space of the heap
     */
    private void resize(){
        pqPrice = Arrays.copyOf(pqPrice, 2 * pqPrice.length);
        pqMileage = Arrays.copyOf(pqMileage, 2 * pqMileage.length);
    }

    /**
     * Checks existence of car
     * @param VIN the identifier for a car
     * @return the car if successful, null otherwise
     */
    public Car lookup(String VIN){
        TreeMap t = indexMileage;
        if(t.get(VIN) == null) return null;
        int indexor = (int) t.get(VIN);
        return pqMileage[indexor];
    }
}