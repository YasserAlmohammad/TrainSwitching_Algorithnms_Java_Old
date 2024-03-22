package railwaySwitchingCore;
import java.util.Formatter;
import java.awt.*;
/**
 * <p>Title: </p>
 *
 * <p>Description:
 * car to hold each carriage information
 * </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class TrainCar {
    public int carNum;
    public String cargo;
    public String origin;
    public String destination;
    public int weight;
    public int miles;

    //coordinates of the graphical figure
    public int x=0;
    public int y=0;
    public int width=0;
    public int height=0;

    public TrainCar() {
    }

    public TrainCar(int carNum,String cargo,String origin,String destination,int weight,int miles) {
        this.carNum=carNum;
        this.cargo=cargo;
        this.origin=origin;
        this.destination=destination;
        this.weight=weight;
        this.miles=miles;
    }

    /**
     * formats the car info in a prober way to display
     * @return String
     */
    public String toString(){
        StringBuffer str=new StringBuffer();
        Formatter formatter=new Formatter(str);
        formatter.format("%8d %10s %20s %20s %8d %8d",carNum,cargo,origin,destination,weight,miles);

        return str.toString();
    }

    /**
     * a car is responsible for drawing itself
     * @param g Graphics2D
     */
    public void draw(Graphics2D g){
        g.setColor(Color.LIGHT_GRAY);
        g.fill3DRect(x,y,width,height,true);
        g.setColor(Color.BLACK);
        g.drawString(""+carNum,x,(int)(y+height/4));
        g.drawString(""+origin,x,(int)(y+height/2));
        g.drawString(""+destination,x,(int)(y+height/1.2));
    }
}
