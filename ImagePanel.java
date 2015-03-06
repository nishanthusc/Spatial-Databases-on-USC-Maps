package com.hw2;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.ToolTipManager;

public class ImagePanel extends JPanel {
	private static final long serialVersionUID = 8833923130765913511L;
	
	private BufferedImage image;
	private List<Building> buildings;
	private List<AnnSystem> announcements;
	private List<Student> students;
	private int x;
	private int y;
	private Query query;
	private boolean drawPoly = true;
	public int x1, y1, x2, y2;
	private int pqX, pqY;
	public ArrayList<Point> path = new ArrayList<Point>();
	
	public ImagePanel() {
	}
	
	public ImagePanel(List<Building> blds, List<AnnSystem> anns, List<Student> stds) {
		this.buildings = blds;
		this.announcements = anns;
		this.students = stds;
		
		try {
			image = ImageIO.read(this.getClass().getResource("/map.jpg"));
		} catch (IOException ex) {
			System.out.println("Exception while loading image...");
		}
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent me) {
				super.mouseClicked(me);
				pqX = me.getX();
				pqY = me.getY();
				if(me.getButton() == MouseEvent.BUTTON1 && drawPoly){
					Query q = getQuery();
					if(q.getSelectedQuery() != null && (q.getSelectedQuery().trim().equalsIgnoreCase("Range Query")) ){
						path.add(new Point(me.getX(), me.getY()));
						//drawPoly = true;
					}					
				}else if(me.getButton() == MouseEvent.BUTTON3){
					drawPoly = false;
				}
				
				repaint();
			}
			
		});
		
		addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent me) {
				super.mouseMoved(me);
				x = me.getX();
				y = me.getY();
				repaint();
			}
		});
		
		ToolTipManager.sharedInstance().registerComponent(this);
	}
	
	public void updatePanel(){
		repaint();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		//drawing image
		g.drawImage(image, 0, 0, null);
		Query q = getQuery();
		if(q == null || q.getSelectedQuery() == null)
			return;
		
		if(q.getSelectedQuery().trim().equalsIgnoreCase("Whole Region") && q.isBuildingFlag()){
			//outlining buildings
			g.setColor(Color.YELLOW);
			if(buildings != null && buildings.size() > 0){
				for(Building eachBuilding : buildings){
					Polygon poly = new Polygon(convertIntListToIntArray(eachBuilding.getxCoOrds()),convertIntListToIntArray(eachBuilding.getyCoOrds()),eachBuilding.getxCoOrds().size());
					g.drawPolygon(poly);				
				}
			}			
		}
		
		if(q.getSelectedQuery().trim().equalsIgnoreCase("Whole Region") && q.isAsFlag()){
			//outlining Announcement Systems
			g.setColor(Color.RED);
			if(announcements != null && announcements.size() > 0){
				for(AnnSystem eachAS : announcements){
					g.drawOval(eachAS.getxCoOrd()-eachAS.getRadius(), eachAS.getyCoOrd()-eachAS.getRadius(), eachAS.getRadius() * 2, eachAS.getRadius()*2);
				}
			}
		}
		
		if(q.getSelectedQuery().trim().equalsIgnoreCase("Whole Region") && q.isStudentFlag()){
			//outlining Students
			g.setColor(Color.GREEN);
			for(Student eachSt : students){
				g.drawRect(eachSt.getxCoOrd(), eachSt.getyCoOrd(), 10, 10);
			}
		}
		
		if(q.getSelectedQuery().trim().equalsIgnoreCase("Point Query")){
			g.setColor(Color.RED);
			g.drawRect(pqX, pqY, 5, 5);
			g.drawOval(pqX-50, pqY-50, 50*2, 50*2);
		}
		
		if(q.getSelectedQuery().trim().equalsIgnoreCase("Range Query")){
			if(path.size() == 1){
				g.setColor(Color.RED);
				g.drawRect(path.get(0).x, path.get(0).y, 10, 10);
			}else{
				g.setColor(Color.RED);
				for(int i=1;i<path.size();i++){
					int a = path.get(i-1).x;
					int b = path.get(i-1).y;
					int c = path.get(i).x;
					int d = path.get(i).y;
					g.drawLine(a, b, c, d);
				}				
			}
		}
			
		//display image co-ordinates	
		g.setColor(Color.CYAN);
		g.drawString(x+", "+y,x, y);
		
	}
	
	/**
	 * This api is to convert List<Integer> to int[]
	 */
	private int[] convertIntListToIntArray(List<Integer> intList){
		int[] intArray = new int[intList.size()];
		for(int i =0; i<intList.size(); i++){
			intArray[i] = intList.get(i);
		}
		return intArray;
	}
	public Query getQuery() {
		if(query == null){
			query = new Query();
		}
		return query;
	}
	public void setQuery(Query query) {
		this.query = query;
	}

	public ArrayList<Point> getPath() {
		return path;
	}

	public void setPath(ArrayList<Point> path) {
		this.path = path;
	}

	public boolean isDrawPoly() {
		return drawPoly;
	}

	public void setDrawPoly(boolean drawPoly) {
		this.drawPoly = drawPoly;
	}

	
} //end of class