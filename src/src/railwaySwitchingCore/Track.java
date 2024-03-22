package railwaySwitchingCore;

import myUtils.*;
import java.io.*;
/**
 *
 * <p>Title: </p>
 *
 * <p>Description:
 * the track class will hold a stack and a track info, plus additionl billing info
 * <br>it will also be responisible for outputing billing into accounting file
 *  </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class Track{
    public String name;
    public String destCity;
    private MyStack stack=new MyStack();
    private File accFile;
    private int totalWeight=0;
    private int totalCars=0;
    private int totalBill=0;

    public Track(){
    }

    /**
     * build a track from a name and a destined city
     * @param trackName String
     * @param dest String
     */
    public Track(String trackName,String dest){
        name=trackName;
        destCity=dest;
    }

    /**
     * adds a car to this track, updates total weight and bill info
     * @param car TrainCar
     */
    public void addCar(TrainCar car){
        stack.push(car);
        totalWeight+=car.weight;
        totalCars++;
        totalBill+=car.miles*car.weight/2000; //for instance
    }

    /**
     * empty method
     * to issue a total billing command and close related resources
     */
    public void closeTrain(){
    }
}
