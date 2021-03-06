package de.windrath.timadorus.latency.util.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

import de.windrath.timadorus.latency.util.network.IClientLogic;
import de.windrath.timadorus.latency.util.network.Message;

public class CoordGui {
    
    public final static int PLANE_SIZE = 200;
    public final static int HALF_PLANE = PLANE_SIZE/2;
    public final static int MENU_SIZE = 50;
    
    CoordFrame frame;
    
    public CoordGui(String titel){
        this(titel, 0, null);
    }
    
    public CoordGui(String titel, int myID, IClientLogic logic) {
        frame = new CoordFrame(titel, myID, logic);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    
    public void setVisible(boolean b){
        frame.setVisible(b);
    }
    
    public void setListener(WindowListener listener){
        frame.addWindowListener(listener);
    }
    
    public void setPrimaryEntityPosition(int iD, Point newPosition){
        frame.setPrimaryEntityPosition(iD, newPosition);
    }
    
    public void setSecondaryEntityPosition(int iD, Point newPosition){
        frame.setSecondaryEntityPosition(iD, newPosition);
    }
    
    public void setTertiaryEntityPosition(int iD, Point newPosition){
        frame.setTertiaryEntityPosition(iD, newPosition);
    }
    
    public void revertToSecondaryPosition(int iD){
    	frame.revertToSecondaryPosition(iD);
    }
    
}

class CoordFrame extends JFrame {
    
    public final static Color DARK_GREEN = new Color(0, 153, 0);
    
    /**
     * 
     */
    private static final long serialVersionUID = -7730758949607885679L;
    
    JPanel basicPanel;
    DrawPanel drawPanel;
    
    public CoordFrame(String titel){
        this(titel, 0, null);
    }

    
    public void revertToSecondaryPosition(int iD) {
		drawPanel.revertToSecondaryPosition(iD);
	}


	public CoordFrame(String titel, int myID, IClientLogic logic){
        setTitle(titel);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        basicPanel = new JPanel();
        basicPanel.setLayout(null);

        add(basicPanel);
        
        drawPanel = new DrawPanel(myID);
        
        if(logic != null){
            
            basicPanel.setPreferredSize(new Dimension(CoordGui.PLANE_SIZE + CoordGui.MENU_SIZE,CoordGui.PLANE_SIZE + CoordGui.MENU_SIZE));

            Entity myEntity = new Entity(new Point(CoordGui.HALF_PLANE,CoordGui.HALF_PLANE), DARK_GREEN, new Point(CoordGui.HALF_PLANE,CoordGui.HALF_PLANE), Color.RED, new Point(CoordGui.HALF_PLANE, CoordGui.HALF_PLANE), Color.BLUE);        
            drawPanel.addEntity(myID, myEntity);
            
            JSlider ySlider = new JSlider(SwingConstants.VERTICAL,0,CoordGui.PLANE_SIZE,CoordGui.HALF_PLANE);
            JSlider xSlider = new JSlider(SwingConstants.HORIZONTAL,0,CoordGui.PLANE_SIZE,CoordGui.HALF_PLANE);
            JButton button = new JButton("Set");
            
            ySlider.setBounds(CoordGui.PLANE_SIZE, 0, CoordGui.MENU_SIZE, CoordGui.PLANE_SIZE);
            ySlider.setInverted(true);
            xSlider.setBounds(0, CoordGui.PLANE_SIZE, CoordGui.PLANE_SIZE, CoordGui.MENU_SIZE);
            button.setBounds(CoordGui.PLANE_SIZE,CoordGui.PLANE_SIZE,CoordGui.MENU_SIZE,CoordGui.MENU_SIZE);
            CoordAction actionListener = new CoordAction(xSlider, ySlider, myEntity, drawPanel, myID, logic);
            button.addActionListener(actionListener);
            button.setFont(new Font("Arial", Font.PLAIN, 11));
            
            basicPanel.add(ySlider);
            basicPanel.add(xSlider);
            basicPanel.add(button);
            
        }else{
            basicPanel.setPreferredSize(new Dimension(CoordGui.PLANE_SIZE,CoordGui.PLANE_SIZE));
        }
        
        basicPanel.add(drawPanel);

        
        pack();
    }
    
    public void setPrimaryEntityPosition(int iD, Point newPosition){
        drawPanel.setPrimaryEntityPosition(iD, newPosition);
    }
    
    public void setSecondaryEntityPosition(int iD, Point newPosition){
        drawPanel.setSecondaryEntityPosition(iD, newPosition);
    }
    
    public void setTertiaryEntityPosition(int iD, Point newPosition){
        drawPanel.setTertiaryEntityPosition(iD, newPosition);
    }
    
}

class DrawPanel extends JPanel{
    
    /**
     * 
     */
    private static final long serialVersionUID = -2220502516591496075L;
    
    private HashMap<Integer, Entity> entities;
    
    public DrawPanel(int myID){
        setBounds(0, 0, CoordGui.PLANE_SIZE, CoordGui.PLANE_SIZE);
        setBackground(Color.WHITE);
        entities = new HashMap<Integer, Entity>();
    }
    
    public void revertToSecondaryPosition(int iD) {
    	 if(entities.containsKey(iD)){
             Entity entity = entities.get(iD);
             if(entity.getSecondaryColor() != null){
                 entities.get(iD).setPrimaryPosition(entities.get(iD).getSecondaryPosition());
             }
         }
         repaint();	
	}

	@Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        for (Entity entity : entities.values()){
            entity.draw(g);
        }
    }
    
    public void setPrimaryEntityPosition(int iD, Point newPosition){
        if(entities.containsKey(iD)){
            entities.get(iD).setPrimaryPosition(newPosition);
        }else{
            entities.put(iD, new Entity(Color.BLACK, newPosition));
        }
        repaint();
    }
    
    public void setSecondaryEntityPosition(int iD, Point newPosition){
        if(entities.containsKey(iD)){
            Entity entity = entities.get(iD);
            if(entity.getSecondaryColor() != null){
                entities.get(iD).setSecondaryPosition(newPosition);
            }

        }
        repaint();
    }
    
    public void setTertiaryEntityPosition(int iD, Point newPosition){
        if(entities.containsKey(iD)){
            Entity entity = entities.get(iD);
            if(entity.getTertiaryColor() != null){
                entities.get(iD).setTertiaryPosition(newPosition);
            }

        }
        repaint();
    }
    
    public void addEntity(int iD,Entity entity){
        entities.put(iD, entity);
        repaint();
    }
}

class CoordAction implements ActionListener
{

    JSlider xSlider;
    JSlider ySlider;
    Entity myEntity;
    DrawPanel panel;
    int iD;
    IClientLogic logic;
    
    public CoordAction(JSlider xSlider, JSlider ySlider, Entity myEntity, DrawPanel panel, int iD, IClientLogic logic) {
        this.xSlider = xSlider;
        this.ySlider = ySlider;
        this.myEntity = myEntity;
        this.panel = panel;
        this.iD = iD;
        this.logic = logic;
    }
    
    @Override
    public void actionPerformed(ActionEvent arg0) {
        final Point position = new Point(xSlider.getValue(), ySlider.getValue());
        Thread notifyThread = new Thread(new Runnable() {

            @Override
            public void run() {
                logic.onGUIEvent(new Message(iD, position));
            }

        });
        notifyThread.start();
    }
}

class Entity{
    Color primaryColor;
    Color secondaryColor = Color.WHITE;
    Color tertiaryColor = Color.WHITE;
    
    Point primaryPosition;
    Point secondaryPosition;
    Point tertiaryPosition;
    
    public Entity(Color primaryColor, Point primaryPosition) {
        super();
        this.primaryColor = primaryColor;
        this.secondaryColor = null;
        this.primaryPosition = primaryPosition;
        this.secondaryPosition = null;
    }
    
    public Entity(Point primaryPosition, Color primaryColor,Point secondaryPosition, Color secondaryColor){
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        this.primaryPosition = primaryPosition;
        this.secondaryPosition = secondaryPosition;
    }

    public Entity(Point primaryPosition, Color primaryColor, Point secondaryPosition, Color secondaryColor, Point tertiaryPosition, Color tertiaryColor) {
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        this.tertiaryColor = tertiaryColor;
        this.primaryPosition = primaryPosition;
        this.secondaryPosition = secondaryPosition;
        this.tertiaryPosition = tertiaryPosition;
    }

    public Color getPrimaryColor() {
        return primaryColor;
    }

    public void setPrimaryColor(Color primaryColor) {
        this.primaryColor = primaryColor;
    }

    public Color getSecondaryColor() {
        return secondaryColor;
    }

    public void setSecondaryColor(Color secondaryColor) {
        this.secondaryColor = secondaryColor;
    }

    public Point getPrimaryPosition() {
        return primaryPosition;
    }

    public void setPrimaryPosition(Point primaryPosition) {
        this.primaryPosition = primaryPosition;
    }

    public Point getSecondaryPosition() {
        return secondaryPosition;
    }

    public void setSecondaryPosition(Point secondaryPosition) {
        this.secondaryPosition = secondaryPosition;
    }
    
    public Color getTertiaryColor() {
        return tertiaryColor;
    }

    public void setTertiaryColor(Color tertiaryColor) {
        this.tertiaryColor = tertiaryColor;
    }

    public Point getTertiaryPosition() {
        return tertiaryPosition;
    }

    public void setTertiaryPosition(Point tertiaryPosition) {
        this.tertiaryPosition = tertiaryPosition;
    }

    public void draw(Graphics g){
        if(secondaryPosition != null && tertiaryPosition != null){
            drawCross(tertiaryPosition, tertiaryColor, 60, g);
            drawCross(secondaryPosition, secondaryColor, 30, g);
        }else if(secondaryPosition != null){
            drawCross(secondaryPosition, secondaryColor, 45, g);
        }
        drawCross(primaryPosition, primaryColor, 0, g);
    }
    
    private void drawCross(Point position, Color color, int rotation, Graphics g){
        Color oldColor = g.getColor();
        g.setColor(color);
        //drawLine: from (x,y) to (x,y)
        int[][] coords = calculateCoordinates(position, rotation);
        
        g.drawLine(coords[0][0],coords[0][1],coords[0][2],coords[0][3]);
        g.drawLine(coords[1][0],coords[1][1],coords[1][2],coords[1][3]);
        g.drawLine(coords[2][0],coords[2][1],coords[2][2],coords[2][3]);
        g.drawLine(coords[3][0],coords[3][1],coords[3][2],coords[3][3]);
        
        g.setColor(oldColor);
    }
    
    private int[][] calculateCoordinates(Point position, int rotation) {
        double rad = rotation*Math.PI/180;
        int[][] coords = new int[4][4];
        
        int start = 3;
        int end = 10;
        
        coords[0][0] = rotateX(0, start, rad) + position.x;
        coords[0][1] = rotateY(0, start, rad) + position.y;
        coords[0][2] = rotateX(0, end, rad) + position.x;
        coords[0][3] = rotateY(0, end, rad) + position.y;
        coords[1][0] = rotateX(start, 0, rad) + position.x;
        coords[1][1] = rotateY(start, 0, rad) + position.y;
        coords[1][2] = rotateX(end, 0, rad) + position.x;
        coords[1][3] = rotateY(end, 0, rad) + position.y;
        coords[2][0] = rotateX(0, start*(-1), rad) + position.x;
        coords[2][1] = rotateY(0, start*(-1), rad) + position.y;
        coords[2][2] = rotateX(0, end*(-1), rad) + position.x;
        coords[2][3] = rotateY(0, end*(-1), rad) + position.y;
        coords[3][0] = rotateX(start*(-1), 0, rad) + position.x;
        coords[3][1] = rotateY(start*(-1), 0, rad) + position.y;
        coords[3][2] = rotateX(end*(-1), 0, rad) + position.x;
        coords[3][3] = rotateY(end*(-1), 0, rad) + position.y;
        
        return coords;
    }

    private int rotateX(int x, int y, double rad){
        return new Double(x * Math.cos(rad) - y * Math.sin(rad)).intValue();
    }
    
    private int rotateY(int x, int y, double rad){
        return new Double(y * Math.cos(rad) + x * Math.sin(rad)).intValue();
    }
    
}