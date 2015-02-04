package de.windrath.timadorus.latency.util.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Observable;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

public class CoordGui {

    CoordFrame frame;
    
    public CoordGui(String titel, int myID, boolean hasOwnEntity) {
        frame = new CoordFrame(titel, myID, hasOwnEntity);
    }
    
    public static void main(String[] args)
    {
       CoordFrame frame = new CoordFrame("Test", 1, true);
       frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       frame.setVisible(true);
    }
    
}

class CoordFrame extends JFrame {
    
    /**
     * 
     */
    private static final long serialVersionUID = -7730758949607885679L;
    
    JPanel basicPanel;
    DrawPanel drawPanel;
    
    public CoordFrame(String titel, int myID, boolean hasOwnEntity){
        setTitle(titel);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        basicPanel = new JPanel();
        basicPanel.setLayout(null);
        

        add(basicPanel);
        
        drawPanel = new DrawPanel(myID);
        
        if(hasOwnEntity){
            
            basicPanel.setPreferredSize(new Dimension(550,550));
            Color darkGreen = new Color(0, 153, 0);
            Entity myEntity = new Entity(new Point(250,250), darkGreen, new Point(250,250), Color.RED, new Point(250, 250), Color.BLUE);        
            drawPanel.addEntity(myID, myEntity);
            
            JSlider ySlider = new JSlider(SwingConstants.VERTICAL,0,500,250);
            JSlider xSlider = new JSlider(SwingConstants.HORIZONTAL,0,500,250);
            JButton button = new JButton("Set");
            
            ySlider.setBounds(500, 0, 50, 500);
            ySlider.setInverted(true);
            xSlider.setBounds(0, 500, 500, 50);
            button.setBounds(500,500,50,50);
            
            button.addActionListener(new CoordAction(xSlider, ySlider, myEntity, drawPanel));
            button.setFont(new Font("Arial", Font.PLAIN, 11));
            
            basicPanel.add(ySlider);
            basicPanel.add(xSlider);
            basicPanel.add(button);
        }else{
            basicPanel.setPreferredSize(new Dimension(500,500));
        }
        
        basicPanel.add(drawPanel);

        
        pack();
    }
    
    public void setEntityPosition(int iD, Point newPosition){
        drawPanel.setEntityPosition(iD, newPosition);
    }
    
}

class DrawPanel extends JPanel{
    
    /**
     * 
     */
    private static final long serialVersionUID = -2220502516591496075L;
    
    private HashMap<Integer, Entity> entities;
    
    public DrawPanel(int myID){
        //setPreferredSize(new Dimension(500,500));
        setBounds(0, 0, 500, 500);
        setBackground(Color.WHITE);
        entities = new HashMap<Integer, Entity>();
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        for (Entity entity : entities.values()){
            entity.draw(g);
        }
    }
    
    public void setEntityPosition(int iD, Point newPosition){
        if(entities.containsKey(iD)){
            entities.get(iD).setPrimaryPosition(newPosition);
        }else{
            entities.put(iD, new Entity(Color.BLUE, newPosition));
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
    
    public CoordAction(JSlider xSlider, JSlider ySlider, Entity myEntity, DrawPanel panel) {
        this.xSlider = xSlider;
        this.ySlider = ySlider;
        this.myEntity = myEntity;
        this.panel = panel;
    }
    
    @Override
    public void actionPerformed(ActionEvent arg0) {
        myEntity.setSecondaryPosition(new Point(xSlider.getValue(), ySlider.getValue()));
        panel.repaint();
        
    }
}

class Entity extends Observable{
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
        setChanged();
        notifyObservers(secondaryPosition);
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