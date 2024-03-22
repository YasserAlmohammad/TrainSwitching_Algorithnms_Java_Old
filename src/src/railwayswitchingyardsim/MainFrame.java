package railwayswitchingyardsim;

import java.awt.*;
import java.awt.event.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JButton;
import com.borland.jbcl.layout.XYLayout;
import com.borland.jbcl.layout.*;
import javax.swing.JFileChooser;
import myUtils.*;
import railwaySwitchingCore.*;
import javax.swing.JTextArea;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import java.awt.image.AffineTransformOp;
import java.awt.geom.AffineTransform;
import javax.swing.UIManager;
import javax.swing.JTextField;
import javax.swing.JLabel;

/**
 * <p>Title: </p>
 *
 * <p>Description:
 * </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class MainFrame extends JFrame {
    JPanel contentPane;
    JMenuBar jMenuBar1 = new JMenuBar();
    JMenu jMenuFile = new JMenu();
    JMenuItem jMenuFileExit = new JMenuItem();
    JMenu jMenuHelp = new JMenu();
    JMenuItem jMenuHelpAbout = new JMenuItem();
    JButton jButton3 = new JButton();
    BorderLayout borderLayout1 = new BorderLayout();
    JPanel northPanel = new JPanel();
    JPanel centerPanel = new JPanel();
    JPanel bottomPanel = new JPanel();
    JTextArea logArea = new JTextArea();
    BorderLayout borderLayout2 = new BorderLayout();
    JScrollPane jScrollPane1 = new JScrollPane(logArea);
    JButton billBtn = new JButton();
    public MainFrame() {
        try {
            LogMsg.textArea=logArea;
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            jbInit();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Component initialization.
     *
     * @throws java.lang.Exception
     */
    private void jbInit() throws Exception {
        contentPane = (JPanel) getContentPane();
        contentPane.setLayout(borderLayout1);
        setSize(new Dimension(590, 500));
        setTitle("railway switching yard simulation window");
        this.addWindowListener(new MainFrame_this_windowAdapter(this));
        this.addHierarchyBoundsListener(new
                                        MainFrame_this_hierarchyBoundsAdapter(this));
        this.addComponentListener(new MainFrame_this_componentAdapter(this));
        this.addWindowStateListener(new MainFrame_this_windowStateAdapter(this));
        jMenuFile.setText("File");
        jMenuFileExit.setText("Exit");
        jMenuFileExit.addActionListener(new
                                        MainFrame_jMenuFileExit_ActionAdapter(this));
        jMenuHelp.setText("Help");
        jMenuHelpAbout.setText("About");
        jMenuHelpAbout.addActionListener(new
                                         MainFrame_jMenuHelpAbout_ActionAdapter(this));
        contentPane.setToolTipText("");
        jButton3.setText("loadData and switch");
        jButton3.addActionListener(new MainFrame_jButton3_actionAdapter(this));
        northPanel.setBackground(UIManager.getColor("textInactiveText"));
        bottomPanel.setBackground(Color.pink);
        bottomPanel.setPreferredSize(new Dimension(70, 100));
        bottomPanel.setLayout(borderLayout2);
        billBtn.setText("bill tarins and view content");
        billBtn.addActionListener(new MainFrame_jButton1_actionAdapter(this));
        stopSwitching.setText("stop switching");
        stopSwitching.addActionListener(new
                                        MainFrame_stopSwitching_actionAdapter(this));
        centerPanel.setBackground(Color.black);
        //    logArea.setText("jTextArea1");
        logArea.setFont(new Font(null,Font.PLAIN,11));
        timeStep.setPreferredSize(new Dimension(50, 20));
        timeStep.setText("500");
        jLabel1.setText("time step(millis)");
        jMenuBar1.add(jMenuFile);
        jMenuFile.add(jMenuFileExit);
        jMenuBar1.add(jMenuHelp);
        jMenuHelp.add(jMenuHelpAbout);
        northPanel.add(jLabel1);
        northPanel.add(timeStep);
        northPanel.add(stopSwitching);
        northPanel.add(billBtn);
        northPanel.add(jButton3);
        contentPane.add(centerPanel, java.awt.BorderLayout.CENTER);
        contentPane.add(northPanel, java.awt.BorderLayout.NORTH);
        contentPane.add(bottomPanel, java.awt.BorderLayout.SOUTH);
        bottomPanel.add(jScrollPane1, java.awt.BorderLayout.CENTER);
        setJMenuBar(jMenuBar1);
    }

    /**
     * File | Exit action performed.
     *
     * @param actionEvent ActionEvent
     */
    void jMenuFileExit_actionPerformed(ActionEvent actionEvent) {
        System.exit(0);
    }

    /**
     * Help | About action performed.
     *
     * @param actionEvent ActionEvent
     */
    void jMenuHelpAbout_actionPerformed(ActionEvent actionEvent) {
        MainFrame_AboutBox dlg = new MainFrame_AboutBox(this);
        Dimension dlgSize = dlg.getPreferredSize();
        Dimension frmSize = getSize();
        Point loc = getLocation();
        dlg.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x,
                        (frmSize.height - dlgSize.height) / 2 + loc.y);
        dlg.setModal(true);
        dlg.pack();
        dlg.show();
    }

    public void jButton2_actionPerformed(ActionEvent e) {
        MyQueue q=new MyQueue();
        for(int i=0;i<10;i++)
            q.queue(new Integer(i));
        System.out.print(q);
    }

    TrainSwitching switching=null;
    JButton stopSwitching = new JButton();
    JTextField timeStep = new JTextField();
    JLabel jLabel1 = new JLabel();
    public void jButton3_actionPerformed(ActionEvent e) {
        JFileChooser openDlg=new JFileChooser();
        int res=openDlg.showOpenDialog(this);
        if(res==openDlg.APPROVE_OPTION){
            Dimension dim=centerPanel.getSize();
            switching=new TrainSwitching(openDlg.getSelectedFile().getPath(),dim.width,dim.height,(Graphics2D)centerPanel.getGraphics());
            switching.viewMainTrain();
            switching.ui=this;
            logArea.setText("");

            try {
                switching.step=Long.parseLong(timeStep.getText());
            } catch (Exception ex) {
                System.out.println("Error parsing the time step [reset to 500]");
            }
            switching.switchTrains();

        }
    }

    public void paint(Graphics g){
        super.paint(g);
        if(TrainSwitching.image!=null){
             AffineTransformOp op=new AffineTransformOp(new AffineTransform(),AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            ((Graphics2D)centerPanel.getGraphics()).drawImage(TrainSwitching.image,op,0,0);
        }
    }

    public void jButton1_actionPerformed(ActionEvent actionEvent) {
        if(switching!=null){
            switching.viewSubTrains();
            switching.billTrains();
        }
    }

    public void this_windowStateChanged(WindowEvent windowEvent) {
        if(TrainSwitching.image!=null){
             AffineTransformOp op=new AffineTransformOp(new AffineTransform(),AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            ((Graphics2D)centerPanel.getGraphics()).drawImage(TrainSwitching.image,op,0,0);
        }
    }

    public void this_componentResized(ComponentEvent componentEvent) {

    }

    public void this_ancestorResized(HierarchyEvent hierarchyEvent) {

    }

    public void this_windowActivated(WindowEvent windowEvent) {

    }

    public void stopSwitching_actionPerformed(ActionEvent actionEvent) {
        switching.enableSwitching=!switching.enableSwitching;
    }
}


class MainFrame_stopSwitching_actionAdapter implements ActionListener {
    private MainFrame adaptee;
    MainFrame_stopSwitching_actionAdapter(MainFrame adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent actionEvent) {
        adaptee.stopSwitching_actionPerformed(actionEvent);
    }
}


class MainFrame_jButton1_actionAdapter implements ActionListener {
    private MainFrame adaptee;
    MainFrame_jButton1_actionAdapter(MainFrame adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent actionEvent) {

        adaptee.jButton1_actionPerformed(actionEvent);
    }
}


class MainFrame_this_windowStateAdapter implements WindowStateListener {
    private MainFrame adaptee;
    MainFrame_this_windowStateAdapter(MainFrame adaptee) {
        this.adaptee = adaptee;
    }

    public void windowStateChanged(WindowEvent windowEvent) {
        adaptee.this_windowStateChanged(windowEvent);
    }
}


class MainFrame_this_componentAdapter extends ComponentAdapter {
    private MainFrame adaptee;
    MainFrame_this_componentAdapter(MainFrame adaptee) {
        this.adaptee = adaptee;
    }

    public void componentResized(ComponentEvent componentEvent) {
        adaptee.this_componentResized(componentEvent);
    }
}


class MainFrame_this_hierarchyBoundsAdapter extends HierarchyBoundsAdapter {
    private MainFrame adaptee;
    MainFrame_this_hierarchyBoundsAdapter(MainFrame adaptee) {
        this.adaptee = adaptee;
    }

    public void ancestorResized(HierarchyEvent hierarchyEvent) {
        adaptee.this_ancestorResized(hierarchyEvent);
    }
}


class MainFrame_this_windowAdapter extends WindowAdapter {
    private MainFrame adaptee;
    MainFrame_this_windowAdapter(MainFrame adaptee) {
        this.adaptee = adaptee;
    }

    public void windowActivated(WindowEvent windowEvent) {
        adaptee.this_windowActivated(windowEvent);
    }
}


class MainFrame_jButton3_actionAdapter implements ActionListener {
    private MainFrame adaptee;
    MainFrame_jButton3_actionAdapter(MainFrame adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButton3_actionPerformed(e);
    }
}


class MainFrame_jMenuFileExit_ActionAdapter implements ActionListener {
    MainFrame adaptee;

    MainFrame_jMenuFileExit_ActionAdapter(MainFrame adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent actionEvent) {
        adaptee.jMenuFileExit_actionPerformed(actionEvent);
    }
}


class MainFrame_jMenuHelpAbout_ActionAdapter implements ActionListener {
    MainFrame adaptee;

    MainFrame_jMenuHelpAbout_ActionAdapter(MainFrame adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent actionEvent) {
        adaptee.jMenuHelpAbout_actionPerformed(actionEvent);
    }

}
