/**
 * Car class
 * Stores data about a car to buy
 * @author Jonathan Chang
 */
public class Car{
    private String VIN;
    private String Make;
    private String Model;
    private String Color;
    private int Mileage;
    private int Price;

    public Car(String id, String make, String model, int price, int mileage, String color){
        VIN = id;
        Make = make;
        Model = model;
        Price = price;
        Mileage = mileage;
        Color = color;
    }

    //get for exchanging cars, lookup functions
    public String getVIN(){
        return VIN;
    }

    public String getMake(){
        return getModel(Make);
    }

    public String getModel(String Make){
        return Make + Model;
    }

    public int getPrice(){
        return Price;
    }

    public int getMileage(){
        return Mileage;
    }


    //set for adding car, updating car
    public void setVIN(String newVIN){
        VIN = newVIN.toUpperCase();
    }

    public void setMake(String newMake){
        if(newMake.length()!=1)
            Make = newMake.substring(0,1).toUpperCase() + newMake.substring(1).toLowerCase();
        else
            Make = newMake.toUpperCase();
    }

    public void setModel(String newModel){
        if(newModel.length() !=1)
            Model = newModel.substring(0,1).toUpperCase() + newModel.substring(1).toLowerCase();
        else
            Model = newModel.toUpperCase();
    }

    public void setPrice(int newPrice){
        if(newPrice<0){
            newPrice = newPrice * (-1);
        }
        Price = newPrice;
    }

    public void setMileage(int newMileage){
        if(newMileage<0){
            newMileage = newMileage * (-1);
        }
        Mileage = newMileage;
    }

    public void setColor(String newColor){
        Color = newColor.toUpperCase();
    }

    public String toString(){
        return VIN + ":" + Make + ":" + Model + ":" + Price + ":" + Mileage + ":" + Color;
    }
}