package myUtils;

import java.awt.*;
import railwaySwitchingCore.TrainCar;
/**
 * <p>Title: </p>
 *
 * <p>Description:
 * Graphical Car data
 * this class extends MyStack class, so it will have a stack functionality but for objects
 * of TrainCar objects.
 * <br>each car will be drawn by itself
 * <br>drawing will be from left to right </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CarStack extends MyStack{
    int x=0;
    int y=0;
    int width=0;
    int height=0;

    int carWidth=0;  //for every single car
    int carHeight=0; //for every single car
    static final int margin=2; //2 pixels between two lines
    public Color color=new Color(72 ,39,119);
    /**
     * passing the limits through which a rectangular surface will be drawn to hold train car shapes
     *
     * @param x int
     * @param y int
     * @param width int
     * @param height int
     * @param carWidth int
     */
    public CarStack(int x,int y,int width,int height,int carWidth) {
        this.x=x;
        this.y=y;
        this.width=width;
        this.height=height;

        this.carWidth=carWidth;
        carHeight=height-margin*2;
    }

    /**
     * coordinate data are inherited from the head
     * each insertion propagates change through the whole stack
     * @param car TrainCar
     * @return TrainCar
     */
    public TrainCar push(TrainCar car){
            car.x=x+margin;
            car.y=y+margin;
            car.width=carWidth;
            car.height=carHeight;
        super.push(car);
        //update coords
        updateCoords();

        return car;
    }

    /**
     * updates the coordinates
     * @return TrainCar
     */
    public TrainCar pop(){
        TrainCar obj=(TrainCar)super.pop();
        updateCoords();
        return obj;
    }

    /**
     * just draw a rect for this stack place then forward draw command to it's stack elements [cars]
     * @param g Graphics2D
     */
    public void draw(Graphics2D g){
        //draw empty first
        g.setColor(color);
        g.fillRoundRect(x,y,width,height,20,20);
        g.setColor(Color.WHITE);
        g.drawRoundRect(x,y,width,height,20,20);
        //then draw cars
        Node temp = head;
        while (temp != null) {
            ((TrainCar)temp.obj).draw(g);
            temp = temp.next;
        }
    }

    /**
     * after each change to the stack we call this internally so we update coordinates
     * of each car
     */
    public void updateCoords(){
        if(head==null)
            return;
        Node temp=head;
        int prevX=x+margin;
        while(temp!=null){
            TrainCar car=(TrainCar)temp.obj;
            car.x=prevX;
            //calc new x,y
            prevX = prevX+carWidth+margin;
            temp=temp.next;
        }
    }

}
