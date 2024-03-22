package myUtils;

import java.awt.*;
import railwaySwitchingCore.TrainCar;
/**
 * <p>Title: </p>
 *
 * <p>Description:
 * Graphical Car data.
 * <br>drawing will be from right to left [ from head to tail ]
 * <br>thid is a queue with specil added functionality to draw cars and have a graphical representation </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CarQueue extends MyQueue{
    int rightX=0; //of the train queue
    int rightY=0; //of the train queue
    int width=0;
    int height=0;
    int carWidth=0;  //for every single car
    int carHeight=0; //for every single car
    static final int margin=2; //2 pixels between two lines
    /**
     * pass coordinates of the queue
     * @param x int
     * @param y int
     * @param width int
     * @param height int
     */
    public CarQueue(int x,int y,int width,int height) {
        rightX=x+width;
        rightY=y;
        this.width=width;
        this.height=height;
        carWidth=width/13;
        carHeight=height-margin*2;
    }

    /**
     * coordinate data are inherited from the tail
     * @param car TrainCar
     * @return TrainCar
     */
    public TrainCar queue(TrainCar car){
        if(head==null){
            car.x=rightX-carWidth-margin;
            car.y=rightY+margin;
            car.width=carWidth;
            car.height=carHeight;
        }
        else{ //get data from tail
            car.x = ((TrainCar)tail.obj).x - carWidth - margin;
            car.y = ((TrainCar)tail.obj).y;
            car.width = ((TrainCar)tail.obj).width;
            car.height = ((TrainCar)tail.obj).height;
        }

        super.queue(car);

        return car;
    }

    /**
     * as a car is dequeued, coordinates are updated
     * @return TrainCar
     */
    public TrainCar dequeue(){
        TrainCar obj=(TrainCar)super.dequeue();
        updateCoords();
        return obj;
    }

    /**
     * we draw the queue then forward the drawing command to it's cars
     * @param g Graphics2D
     */
    public void draw(Graphics2D g){
        //draw empty first
        g.setColor(new Color(128,128,255));
        g.fillRoundRect(rightX-width,rightY,width,height,20,20);
        g.setColor(Color.WHITE);
        g.drawRoundRect(rightX-width,rightY,width,height,20,20);

        Node temp = head;
        while (temp != null) {
            ((TrainCar)temp.obj).draw(g);
            temp = temp.next;
        }
    }

    /**
     * updates each car coords after each change
     */
    public void updateCoords(){
        if(head==null)
            return;
        Node temp=head;

        int prevX=rightX-carWidth-margin;
        while(temp!=null){
            TrainCar car=(TrainCar)temp.obj;
            car.x=prevX;
            //calc new x,y
            prevX = prevX - carWidth - margin;
            temp=temp.next;
        }
    }

}
