package railwaySwitchingCore;

import java.io.*;
import java.util.Formatter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.AffineTransformOp;
import java.awt.geom.AffineTransform;
import javax.swing.JFrame;
import myUtils.*;

/**
 *
 * <p>Title: </p>
 *
 * <p>Description:
 * our core class that maintains everything attached together to do the task of this project
 * <br> the class will the main train info, a transfer tran, and subtrains
 * <br> it also has the graphical information to draw against
 *
 * <br>drawing is done using double buffering teqnique, with partial changes taken into account:
 * <br only objects being changed are being redrawn, and drawing is done offscreen then flipped into screen
 * <br> so drawing can't be any smoother
 * <br> for example: when a car is removed from the main train to the transfer train, only those are redrawn
 * </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class TrainSwitching {
    CarStack[] subTrains = new CarStack[4];
    int[] lengths = new int[4]; //each train length

    CarQueue mainTrain;

    CarStack transfer;


    public boolean enableSwitching = true;
    public Graphics2D targetGraphics;
    public int width; //width and height could be updated automatically upon changing the window size of the surface drawn against
    public int height;
    public static BufferedImage image = null;
    public Graphics2D img = null; //imageGraphics
    public JFrame ui=null;
    public long step=500;

    /**
     * passing a file name to load data from and width, height of the target window and a graphics device to drawn against
     * the main train is loaded and becomes ready to switch
     * @param filePath String
     * @param width int
     * @param height int
     * @param g Graphics2D
     */
    public TrainSwitching(String filePath, int width, int height, Graphics2D g) {
        YardCoords.calcCoords(width, height);
        mainTrain = TrainDataLoader.loadTrainInfo(filePath);
        if (mainTrain == null) {
            LogMsg.logMsg("error loading data file\r\n");
        }
        for (int i = 0; i < 4; i++)
            subTrains[i] = new CarStack(YardCoords.subTrain[i].x,YardCoords.subTrain[i].y,YardCoords.subTrain[i].width,YardCoords.subTrain[i].height,YardCoords.subTrain[i].width/6);
        transfer = new CarStack(YardCoords.transferTrain.x,YardCoords.transferTrain.y,YardCoords.transferTrain.width,YardCoords.transferTrain.height,YardCoords.transferTrain.width/3);
        transfer.color=new Color(255,255,128);
        this.targetGraphics = g;
        this.width = width;
        this.height = height;
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        img = image.createGraphics();

    }

    /**
     * this method initiates a new thread to do the switching thing
     * the thread do the switching and calls the rendering
     */
    public void switchTrains() {
        (new SwitchingThread()).start();
    }

    /**
     * views the main train content on the standard output and in a logging text area
     */
    public void viewMainTrain() {
        LogMsg.logMsg(mainTrain+"\r\n");
    }

    /**
     * views the subtrains content on the standard output
     */
    public void viewSubTrains() {
        LogMsg.logMsg("\n*****************************  CONTENT OF SUBTRAINS  ***************************\r\n");
        for (int i = 0; i < subTrains.length; i++) {
            LogMsg.logMsg("Track " + (i + 1) + " :\r\n");
            LogMsg.logMsg(subTrains[i]+"\r\n");
            LogMsg.logMsg("*************************\r\n");
        }
    }

    /**
     * go through all sub trains and make an accounting file for each
     * this should be the final method to get called, which in turn will destroy the train stacks
     */
    public void billTrains() {
        for (int i = 0; i < 4; i++) {
            double totalWieght = 0;
            double totalBill = 0;
            int totalCars = 0;
            File accFile = new File("acct" + (i + 1) + ".txt");

            try {
                PrintWriter print = new PrintWriter(accFile);
                StringBuffer header = new StringBuffer("Track " + (i + 1) +
                        " : A total of " + lengths[i] + " cars bound for ");

                switch (i) {
                case 0:
                    header.append("Trenton:");
                    break;
                case 1:
                    header.append("Charlotte:");
                    break;

                case 2:
                    header.append("Baltimore:");
                    break;
                case 3:
                    header.append("other destinations:");
                    break;
                }

                print.println(header);
                LogMsg.logMsg(header+"\r\n");

                while (!subTrains[i].isEmpty()) {
                    int tempCost = 0;
                    TrainCar car = (TrainCar) subTrains[i].pop();

                    tempCost = car.miles * car.weight / 2000;
                    StringBuffer str = new StringBuffer();
                    Formatter formatter = new Formatter(str);
                    formatter.format(" Car number : %d\r\n" +
                                     "      Cargo : %s \r\n" +
                                     "     Origin : %s \r\n" +
                                     "Destination : %s \r\n" +
                                     "     Wieght : %d pounds \r\n" +
                                     "   Distance : %d miles \r\n" +
                                     "       Cost : $%s \r\n"
                                     , car.carNum, car.cargo, car.origin,
                                     car.destination, car.weight, car.miles,
                                     tempCost);
                    totalBill += tempCost;
                    totalWieght += car.weight;
                    print.println(str);
                    LogMsg.logMsg(str+"\r\n");
                }
                header = new StringBuffer("Total weight:" + totalWieght / 2000 +
                                          " tons\r\n");
                header.append(" Total Bill : $" + totalBill);
                print.println(header);
                LogMsg.logMsg(header+"\r\n");
                print.close();
            } catch (FileNotFoundException ex) {
            } finally {
                accFile = null;
            }

        }
    }

    /**
     *
     * <p>Title: </p>
     *
     * <p>Description:
     * an internal class to do the switching job, without locking the main UI
     *  </p>
     *
     * <p>Copyright: Copyright (c) 2006</p>
     *
     * <p>Company: </p>
     *
     * @author not attributable
     * @version 1.0
     */
    class SwitchingThread extends Thread {
        //accessing the graphics will be syncronized
        AffineTransformOp op = new AffineTransformOp(new AffineTransform(),
                AffineTransformOp.TYPE_NEAREST_NEIGHBOR);

        /**
         * initially we draw the yard it self, with no cars
         * @param g Graphics2D
         * @param width int
         * @param height int
         */
        public void drawYard(Graphics2D g, int width, int height) {
            g.setBackground(new Color(0, 0, 0));
            if(mainTrain!=null)
                mainTrain.draw(g);
            if(transfer!=null)
                transfer.draw(g);
            for(int i=0;i<4;i++){
                if(subTrains[i]!=null)
                    subTrains[i].draw(g);
            }

            img.setColor(Color.WHITE);
            for(int i=0;i<4;i++)
                img.drawString("SubTrain "+i,YardCoords.subTrain[i].x-100,YardCoords.subTrain[i].y+YardCoords.subTrain[i].height/2);

            img.drawString("MainTrain ",YardCoords.mainTrain.x+2,YardCoords.mainTrain.y-20);
            img.drawString("TransferTrain ",YardCoords.transferTrain.x,YardCoords.transferTrain.y-20);

        }

        /**
         * all changes to the image draw is commited to the screen now
         */
        public void commitScene() {
            targetGraphics.drawImage(image, op, 0, 0);
        }

        public void run() {
            drawYard(img, width, height);
            commitScene();
            LogMsg.logMsg("****************************   SWITCING STATEMENTS  ***************************\r\n");
            //first we move cars from main train stack into the transfer stack tell consecutive cars mismatch destination
            while (enableSwitching && !mainTrain.isEmpty()) {

                //with each exit for this loop, transfer stack must be emptied
                transfer.push(mainTrain.dequeue());
                mainTrain.draw(img);
                transfer.draw(img);
                step();

                //couple similar destination cars
                while (!mainTrain.isEmpty()) {
                    if (((TrainCar) mainTrain.peek()).destination.equals(((
                            TrainCar) transfer.peek()).destination)) {
                        transfer.push(mainTrain.dequeue());
                        mainTrain.draw(img);
                        transfer.draw(img);
                        step();
                    } else
                        break;
                }

                //now issue uncoupling statement
                if (!mainTrain.isEmpty()) {
                    LogMsg.logMsg("uncoupling between cars " +
                                     ((TrainCar) transfer.peek()).carNum +
                                     " and " +
                                     ((TrainCar) mainTrain.peek()).carNum +
                                     " and back ");
                }

                int trackNum = 0;
                int multiple = 0;
                //now we got transfer stack, so we pop it's elements onto the right track, depending on destination
                //the final track is left for other destinations that don't match

                String dest = ((TrainCar) transfer.peek()).destination;
                boolean added = false;
                if (dest.equalsIgnoreCase("Trenton"))
                    trackNum = 1;
                else
                if (dest.equalsIgnoreCase("Charlotte"))
                    trackNum = 2;
                else if (dest.equalsIgnoreCase("Baltimore"))
                    trackNum = 3;
                else
                    trackNum = 4;

                while (!transfer.isEmpty()) {
                    subTrains[trackNum - 1].push(transfer.pop());
                    lengths[trackNum - 1]++;
                    multiple++;
                   subTrains[trackNum - 1].draw(img);
                   transfer.draw(img);
                   step();
                }

                //complete uncoupling statement issue
                if (mainTrain.isEmpty()) {
                    LogMsg.logMsg("Back remaining cars on track " +
                                       (trackNum)+"\r\n");
                } else {
                    if (multiple > 1)
                       LogMsg.logMsg("them onto track " + (trackNum) + "\r\n");
                    else {
                        LogMsg.logMsg("car " +
                                         ((TrainCar) subTrains[trackNum -
                                          1].peek()).
                                         carNum + " onto track " + (trackNum) +
                                         "\r\n");
                    }
                }

            }

        }

        /**
         * one step by sleeping for a while and updating the figure drawn
         */
        public void step() {
            try {
                sleep(step);
            } catch (InterruptedException ex) {
            }
            commitScene();
            ui.repaint(0,0,1,1);
        }
    }


}


/**
 *
 * <p>Title: </p>
 *
 * <p>Description:
 * class designated to calculate the main object coordinates in the yard
 * </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
class YardCoords {
    public static Rectangle mainTrain = null;

    public static Rectangle transferTrain = null;

    public static Rectangle[] subTrain = new Rectangle[4];

    public static void calcCoords(int width, int height) {
        int x = 0;
        int y = (int) (height * 0.42);
        int curWidth = 0;
        int curHeight = 0;

        curWidth = (int) (width * 0.75);
        curHeight = (int) (height * 0.16);
        mainTrain = new Rectangle(x, y, curWidth, curHeight);

        x = mainTrain.width + mainTrain.x + 4;
        curWidth = (int) (width * 0.2);
        transferTrain = new Rectangle(x, y, curWidth, curHeight);

        //1st
        y = 4;
        x =(int)(width*0.5) + 4;
        curWidth = (int) (width * 0.45);
        subTrain[0] = new Rectangle(x, y, curWidth, curHeight);

        //2d
        y = y + 4 + curHeight;
        subTrain[1] = new Rectangle(x, y, curWidth, curHeight);

        //4th
        y = height - curHeight - 4;
        subTrain[3] = new Rectangle(x, y, curWidth, curHeight);

        //3d
        y = y - 4 - curHeight;
        subTrain[2] = new Rectangle(x, y, curWidth, curHeight);

    }
}
