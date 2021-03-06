package gui;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import log.Logger;

public class MainApplicationFrame extends JFrame
{
    private final JDesktopPane desktopPane = new JDesktopPane();
    private final RobotModel robotModel;
    private final RobotController robotController;
    private Info info = null;
    
    public MainApplicationFrame() {
        //Make the big window be indented 50 pixels from each edge
        //of the screen.
	    int inset = 50; 
	    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	    setBounds(inset, inset,
	        screenSize.width  - inset*2,
	        screenSize.height - inset*2);
	    setContentPane(desktopPane);
	        
	    if (new File("info.json").exists()) readJSON();
	        
	    LogWindow logWindow = createLogWindow();
	    addWindow(logWindow);
	
	    GameWindow gameWindow = new GameWindow();
	    if (info != null)
	    {
	        gameWindow.setLocation(info.getGameFrame().location());
	        gameWindow.setSize(info.getGameFrame().width(), info.getGameFrame().height());
	    }
	    else
	        gameWindow.setSize(400,  400);
	    addWindow(gameWindow);
	    
	    RobotPositionWindow posWindow = createPosWindow();
	    addWindow(posWindow);
	    
	    if (info != null)
	    {
	        if(info.getLogFrame().isMin())
				logWindow.setSize(100, 100);
	        //if(info.getFrame1().isMax())
				//desktopPane.getDesktopManager().maximizeFrame(logWindow);
	        if(info.getGameFrame().isMin())
				gameWindow.setSize(100, 100);
	        //if(info.getFrame2().isMax())
				//desktopPane.getDesktopManager().maximizeFrame(gameWindow);
	        if(info.getPosFrame().isMin())
	        	posWindow.setSize(100, 100);
	    }
	    setJMenuBar(generateMenuBar());
	    setDefaultCloseOperation(EXIT_ON_CLOSE);
	    
	    robotModel = new RobotModel(gameWindow.getVisualizer(), posWindow.getVisualizer());
	    robotController = new RobotController(robotModel);
	    gameWindow.getVisualizer().setController(robotController);
    }
    
    private void readJSON() {
    	Gson gson = new GsonBuilder().create();
		try {
			info = gson.fromJson(new FileReader("info.json"), Info.class);
		} catch (JsonSyntaxException e) {
			System.out.print("Can't read json(syntax)");
			e.printStackTrace();
		} catch (JsonIOException e) {
			System.out.print("Can't read json(fileException)");
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			System.out.print("Can't read json(file not found)");
			e.printStackTrace();
		}
	}
    
    protected RobotPositionWindow createPosWindow()
    {
    	RobotPositionWindow posWindow = new RobotPositionWindow();
    	if (info != null)
    	{
    		posWindow.setLocation(info.getPosFrame().location());
            posWindow.setSize(info.getPosFrame().width(), info.getPosFrame().height());
            
    	}
    	else
    	{
	    	posWindow.setLocation(300,10);
			posWindow.setSize(300, 200);
			setMinimumSize(posWindow.getSize());
    	}
        return posWindow;
    }

	protected LogWindow createLogWindow()
    {
    	LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
    	if (info != null)
    	{
    		logWindow.setLocation(info.getLogFrame().location());
            logWindow.setSize(info.getLogFrame().width(), info.getLogFrame().height());
            
    	}
    	else {
        logWindow.setLocation(10,10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
    	}
    	//logWindow.pack();
        Logger.debug("�������� ��������");
        return logWindow;
    }
    
    protected void addWindow(JInternalFrame frame)
    {
        desktopPane.add(frame);
        frame.setVisible(true);
    }
    
//    protected JMenuBar createMenuBar() {
//        JMenuBar menuBar = new JMenuBar();
// 
//        //Set up the lone menu.
//        JMenu menu = new JMenu("Document");
//        menu.setMnemonic(KeyEvent.VK_D);
//        menuBar.add(menu);
// 
//        //Set up the first menu item.
//        JMenuItem menuItem = new JMenuItem("New");
//        menuItem.setMnemonic(KeyEvent.VK_N);
//        menuItem.setAccelerator(KeyStroke.getKeyStroke(
//                KeyEvent.VK_N, ActionEvent.ALT_MASK));
//        menuItem.setActionCommand("new");
////        menuItem.addActionListener(this);
//        menu.add(menuItem);
// 
//        //Set up the second menu item.
//        menuItem = new JMenuItem("Quit");
//        menuItem.setMnemonic(KeyEvent.VK_Q);
//        menuItem.setAccelerator(KeyStroke.getKeyStroke(
//                KeyEvent.VK_Q, ActionEvent.ALT_MASK));
//        menuItem.setActionCommand("quit");
////        menuItem.addActionListener(this);
//        menu.add(menuItem);
// 
//        return menuBar;
//    }
    
    private JMenuBar generateMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu lookAndFeelMenu = new JMenu("����� �����������");
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(
        		"���������� ������� ����������� ����������");
        lookAndFeelMenu.add(getSystemLookAndFeel());
        lookAndFeelMenu.add(getCrossplatformLookAndFeel());
        
        JMenu testMenu = new JMenu("�����");
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription(
        		"�������� �������"); 
        testMenu.add(getLogMessageItem());
         
        menuBar.add(lookAndFeelMenu);
        menuBar.add(testMenu);
        menuBar.add(getCloseMenuItem());
        return menuBar;
    }
    
    private JMenuItem getCloseMenuItem() {
    	JMenuItem closeMenu = new JMenuItem("�������", KeyEvent.VK_C);
        closeMenu.addActionListener(new ActionListener() {
            @Override
        	public void actionPerformed(ActionEvent e) {
                   int result = JOptionPane.showOptionDialog(
                		   MainApplicationFrame.this, 
                		   "������� ����������?", 
                		   "�������������", 
                		   JOptionPane.YES_NO_OPTION, 
                		   JOptionPane.INFORMATION_MESSAGE, 
                		   null, 
                		   new Object[]{"��", "���"}, 
                		   "��");                   
                if (result == JOptionPane.YES_OPTION)
                	saveAndExit();
                }}
 	
        );
        return closeMenu;
    }

	private JMenuItem getLogMessageItem() {
    	JMenuItem logMessageItem = new JMenuItem("��������� � ���", KeyEvent.VK_S);
        logMessageItem.addActionListener((event) -> {
            Logger.debug("����� ������");
        });
        return logMessageItem;
    }

	private JMenuItem getCrossplatformLookAndFeel() {
    	JMenuItem crossplatformLookAndFeel = new JMenuItem("������������� �����", KeyEvent.VK_S);
        crossplatformLookAndFeel.addActionListener((event) -> {
            setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            this.invalidate();
        });
        return crossplatformLookAndFeel;
	}

	private JMenuItem getSystemLookAndFeel() {
    	JMenuItem systemLookAndFeel = new JMenuItem("��������� �����", KeyEvent.VK_S);
        systemLookAndFeel.addActionListener((event) -> {
            setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            this.invalidate();
        });
        return systemLookAndFeel;
	}

	private void saveAndExit()
    {
    	JInternalFrame[] panes = new JInternalFrame[3];
    	for(JInternalFrame frame : desktopPane.getAllFrames())
    	{
    		if (frame instanceof LogWindow) panes[0] = frame;
    		if (frame instanceof GameWindow) panes[1] = frame;
    		if (frame instanceof RobotPositionWindow) panes[2] = frame;
    	} 
    	FrameInfo logWindowInfo = ((LogWindow)panes[0]).getInfo(); 
    	FrameInfo gameWindowInfo = ((GameWindow)panes[1]).getInfo(); 
    	FrameInfo robotPositionWindowInfo = ((RobotPositionWindow)panes[2]).getInfo(); 
    	Info info = new Info(logWindowInfo, gameWindowInfo, robotPositionWindowInfo);
    	Gson gson = new GsonBuilder().create();
    	FileWriter outStream;
    	try {
			outStream = new FileWriter("info.json", false);
			outStream.write(gson.toJson(info));
			outStream.flush();
			outStream.close();
		} catch (IOException e) {
			System.out.print("JSON file wasn't write");
			e.printStackTrace();
		}
    	System.exit(0);
    }
    
    private void setLookAndFeel(String className)
    {
        try
        {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        }
        catch (ClassNotFoundException | InstantiationException
            | IllegalAccessException | UnsupportedLookAndFeelException e)
        {
            // just ignore
        }
    }
}
