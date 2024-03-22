package railwaySwitchingCore;
import javax.swing.JTextArea;

/**
 * <p>Title: </p>
 *
 * <p>Description:
 * it logs messages to both a TextArea field if available
 * <br> also to the standard output
 * </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class LogMsg {
    public static JTextArea textArea=null;
    public LogMsg() {
    }

    public static void logMsg(String str){
        System.out.println(str);
        if(textArea!=null)
            textArea.append(str);
    }

    public static void logMsg(Object obj) {
        System.out.println(obj);
        if (textArea != null && obj!=null)
            textArea.append(obj.toString());
    }

}
