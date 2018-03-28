/**
 * CarTracker class
 * Terminal menu based driver program
 * @author Jonathan Chang
 */

import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;

public class CarTracker{
    private static Scanner s = new Scanner(System.in);

    public static void main(String[] args) {
        IndexMinPQ pq = new IndexMinPQ(); //initialize everything
        System.out.println("Reading in cars.txt\n"); //read in the txt file
        try{
            Scanner f = new Scanner(new File("cars.txt"));
            f.nextLine(); //skip first line
            while(f.hasNextLine()){
                String line = f.nextLine();
                String[] parts = line.split(":");
                String VIN = parts[0];
                String make = parts[1];
                String model = parts[2];
                int price = Integer.parseInt(parts[3]);
                int mileage = Integer.parseInt(parts[4]);
                String color = parts[5];
                Car newCar = new Car(VIN, make, model, price, mileage, color);
                pq.insert(newCar, VIN);
            }
            f.close();
            System.out.println("Finished reading in cars.txt");
        } catch(FileNotFoundException n){
            System.out.println("Couldn't find file cars.txt");
        }
        //done reading in txt file
        System.out.println("\nWelcome! Starting Car Tracker Program");
        //this keeps looping until user wants to exit
        while(true){
            System.out.print("------------------------------------------------\n"
                    + "                   Main Menu\n"
                    + "1.  Add a Car\n"
                    + "2.  Update a Car\n"
                    + "3.  Remove a Car\n"
                    + "4.  Lowest Price Car\n"
                    + "5.  Lowest Mileage Car\n"
                    + "6.  Lowest Price Car by Make and Model\n"
                    + "7.  Lowest Mileage Car by Make and Model\n"
                    + "0.  Exit\n"
                    + "Select option: ");
            try{
                int choice = Integer.parseInt(s.nextLine());
                if (choice == 1) add(pq);
                if (choice == 2) update(pq);
                if (choice == 3) remove(pq);
                if (choice == 4) lowestPrice(pq);
                if (choice == 5) lowestMileage(pq);
                if (choice == 6) lowestPriceMM(pq);
                if (choice == 7) lowestMileageMM(pq);
                if(choice == 0) {
                    s.close();
                    System.out.println("Exiting. Good Bye!");
                    System.exit(0);
                }
                else {
                    System.out.print("Back to main menu\n");
                }
            } catch(NumberFormatException ex){
                System.out.print("Invalid choice try again\n");
            }
        }
    }

    /**
     * 1) Allows the user to add a car to the PQ
     * @param pq the pq for the program
     */
    private static void add(IndexMinPQ pq){
        System.out.println("------------------------------------------------\n");
        System.out.println("             Add Car            ");
        Car c = new Car("", "", "", 0, 0, "");
        System.out.print("Enter VIN: ");
        String VIN;
        while(true){
            VIN = s.nextLine();
            VIN = VIN.toUpperCase();
            if(VIN.length() !=17 || VIN.contains("I") || VIN.contains("O") || VIN.contains("Q") || pq.lookup(VIN)!=null){
                System.out.print("Invalid VIN input\nEnter VIN: ");
            }
            else{
                c.setVIN(VIN);
                break;
            }
        }
        while(true){
            try{
                System.out.print("Enter make: ");
                c.setMake(s.nextLine());
                System.out.print("Enter model: ");
                c.setModel(s.nextLine());
                System.out.print("Enter price: ");
                c.setPrice(Integer.parseInt(s.nextLine()));
                System.out.print("Enter mileage: ");
                c.setMileage(Integer.parseInt(s.nextLine()));
                System.out.print("Enter color: ");
                c.setColor(s.nextLine());
                break;
            } catch(NumberFormatException ex){
                System.out.println("Bad entry detected. Start Over");
            }
        }
        pq.insert(c, VIN);
        System.out.println("Car successfully added!");
    }

    /**
     * 2) Update a car value
     * @param pq the pq for the program
     */
    private static void update(IndexMinPQ pq){
        System.out.println("------------------------------------------------\n");
        System.out.println("           Update Car           ");
        System.out.print("Please enter car VIN: ");
        String VIN = s.nextLine().toUpperCase();
        Car temp = pq.lookup(VIN);
        if(temp == null) {
            System.out.println("Car not found. Exiting.");
            return;
        }
        while(true){
            System.out.println("Update Car Menu");
            System.out.print("1.  Change price\n"
                    + "2.  Change mileage\n"
                    + "3.  Change color\n"
                    + "0.  Main Menu\n"
                    + "Please choose: ");
            try{
                int choice = Integer.parseInt(s.nextLine());
                if (choice == 1) {
                    System.out.print("New price: ");
                    int newPrice = Integer.parseInt(s.nextLine());
                    pq.updating(temp, VIN, newPrice, -1, " ");
                    return;
                }
                if (choice == 2) {
                    System.out.print("New mileage: ");
                    int newMileage = Integer.parseInt(s.nextLine());
                    pq.updating(temp, VIN, -1, newMileage, " ");
                    return;
                }
                if (choice == 3) {
                    System.out.print("New color: ");
                    String newColor = s.nextLine();
                    pq.updating(temp, VIN, -1, -1, newColor);
                    return;
                }
                if(choice ==0){
                    return;
                }
                else{
                    System.out.println("Invalid choice. Try again");
                }
            } catch(NumberFormatException ex){
                System.out.print("Invalid choice try again\n");
            }
        }
    }

    /**
     * 3) Allows the user to remove a car from the PQ
     * @param pq the pq for the program
     */
    private static void remove(IndexMinPQ pq){
        System.out.println("------------------------------------------------\n");
        System.out.println("           Remove Car           ");
        System.out.print("Enter VIN: ");
        String VIN = s.nextLine();
        Car temp = pq.lookup(VIN);
        if (temp == null) System.out.println("Car not found");
        else{
            System.out.println("Removing car");
            pq.delete(temp, VIN);
        }
    }

    /**
     * 4) Displays the lowest priced car
     * @param pq the pq for the program
     */
    private static void lowestPrice(IndexMinPQ pq){
        System.out.println("------------------------------------------------\n");
        System.out.println("        Lowest Price Car        ");
        Car temp = pq.getMin(2);
        if (temp == null) System.out.println("No cars left");
        else System.out.println("The lowest priced car is " + temp);
    }

    /**
     * 5) Displays the car with the lowest mileage
     * @param pq The PQ
     */
    private static void lowestMileage(IndexMinPQ pq){
        System.out.println("------------------------------------------------\n");
        System.out.println("       Lowest Mileage Car       ");
        Car temp = pq.getMin(1);
        if (temp == null) System.out.println("No cars left");
        else System.out.println("The lowest mileage car is " + temp);
    }

    /**
     * 6) Displays the lowest priced car by make and model
     * @param pq the pq for the program
     */
    private static void lowestPriceMM(IndexMinPQ pq){
        System.out.println("------------------------------------------------\n");
        System.out.println("        Lowest Price Car by Make and Model");
        System.out.print("Please enter the make: ");
        String make = s.nextLine().toUpperCase();
        if(make.length() !=1)
            make = make.substring(0,1).toUpperCase() + make.substring(1).toLowerCase();
        System.out.print("Please enter the model: ");
        String model = s.nextLine().toUpperCase();
        if(model.length() !=1)
            model = model.substring(0,1).toUpperCase() + model.substring(1).toLowerCase();
        Car temp = pq.minMM(2, make, model);
        if (temp == null) System.out.println("\nNo cars found");
        else System.out.println("Car found: " + temp);
    }

    /**
     * 7) Displays the car with the lowest mileage by make and model
     * @param pq the pq for the program
     */
    private static void lowestMileageMM(IndexMinPQ pq){
        System.out.println("------------------------------------------------\n");
        System.out.println("       Lowest Mileage Car by Make and Model");
        System.out.print("Please enter the make: ");
        String make = s.nextLine().toUpperCase();
        if(make.length() !=1)
            make = make.substring(0,1).toUpperCase() + make.substring(1).toLowerCase();
        System.out.print("Please enter the model: ");
        String model = s.nextLine().toUpperCase();
        if(model.length() !=1)
            model = model.substring(0,1).toUpperCase() + model.substring(1).toLowerCase();
        Car temp = pq.minMM(1, make, model);
        if (temp == null) System.out.println("\nNo cars found");
        else System.out.println("Car found: " + temp);
    }
}