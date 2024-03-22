package railwaySwitchingCore;

import myUtils.CarQueue;
import java.io.*;
import java.util.StringTokenizer;

/**
 * <p>Title: </p>
 *
 * <p>Description:
 * we'll use this class static method to load train data from an input file
 * </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class TrainDataLoader {
    public TrainDataLoader() {
    }

    /**
     * passing an input file path we construct the CarQueue objecy
     * @param filePath String
     * @return CarQueue
     */
    public static CarQueue loadTrainInfo(String filePath) {
        CarQueue mainTrain = new CarQueue(YardCoords.mainTrain.x,YardCoords.mainTrain.y,YardCoords.mainTrain.width,YardCoords.mainTrain.height);

        /* file is expected to hold information for each car in a single file in the
           following format:
         carNum(int) cargo(String) source(String) dest(String) weight(int) miles(int)
         */
        BufferedReader reader=null;
        try {
            reader = new BufferedReader(new FileReader(filePath));

            String line;
            while ((line = reader.readLine()) != null) {
                StringTokenizer tokens = new StringTokenizer(line);
                TrainCar car = new TrainCar();
                car.carNum = Integer.parseInt(tokens.nextToken());
                car.cargo = tokens.nextToken();
                car.origin = tokens.nextToken();
                car.destination = tokens.nextToken();
                car.weight = Integer.parseInt(tokens.nextToken());
                car.miles = Integer.parseInt(tokens.nextToken());
                mainTrain.queue(car);
            }
        }

        catch (FileNotFoundException ex) {
            System.out.println("file: [" + filePath + "] not found");
            return null;
        } catch (IOException ex) {
            System.out.println("IO ERROR");
            return null;
        }

        return mainTrain;
    }
}
