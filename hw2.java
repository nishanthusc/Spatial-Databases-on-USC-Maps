//package com;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;

import oracle.sdoapi.OraSpatialManager;
import oracle.sdoapi.adapter.GeometryAdapter;
import oracle.sdoapi.adapter.GeometryInputTypeNotSupportedException;
import oracle.sdoapi.geom.CoordPoint;
import oracle.sdoapi.geom.Geometry;
import oracle.sdoapi.geom.InvalidGeometryException;
import oracle.sql.STRUCT;

public class hw2 extends JFrame{

	private static final long serialVersionUID = 5817236296119965383L;
	private Container c = null;
	private JPanel imagePanel = null;
	private JPanel optionsPanel = null;
	private JPanel queryPanel = null;
	//private JTextArea displayQuery = null;
	private JButton submitButton = null;
	List<Building> buildings = null;
	List<AnnSystem> announcements = null;
	List<Student> students = null;
	private JCheckBox asCB = new JCheckBox("AS");
	private JCheckBox bldngCB = new JCheckBox("Building");
	private JCheckBox stuCB = new JCheckBox("Students");
	private JRadioButton wholeRegion;
	private JRadioButton pointQuery;
	private JRadioButton rangeQuery;
	private JRadioButton surrStudent;
	private JRadioButton emerQuery;
	private ButtonGroup buttonGroup;
	static Connection mainConnection=null;
	
	private BufferedImage image;
	private int x;
	private int y;
	private Query query;
	private boolean drawPoly = true;
	public int x1, y1, x2, y2,xi,yi;
	private int pqX, pqY;
	private JTextArea dispQuery = null;
	public ArrayList<Point> path = new ArrayList<Point>();
	
	public hw2() {
		setTitle("Meghamsh Utkur");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		c = getContentPane();
		setSize(1100,680);
		c.setLayout(new BorderLayout());
		try {
			image = ImageIO.read(this.getClass().getResource("/map.jpg"));
		} catch (IOException ex) {
			System.out.println("Exception while loading image...");
		}
		
		//imagePanel = new ImagePanel(buildings, announcements, students, displayQuery);
		loadImagePanel();
		optionsPanel = new JPanel();
		queryPanel = new JPanel();
		addComponentsToOptionsPanel();
		dispQuery = new JTextArea(3, 80);
		JScrollPane scrollPane = new JScrollPane(dispQuery); 
		dispQuery.setEditable(false);
		queryPanel.add(scrollPane);
		//this.setDisplayQuery(dispQuery);
		c.add(imagePanel, BorderLayout.CENTER);
		c.add(optionsPanel, BorderLayout.LINE_END);
		c.add(queryPanel, BorderLayout.PAGE_END);
		//pack();
		setVisible(true);
	}
	
	/**
	 * This api is to populate the Student objects from data source	//TODO - need to connect to real data source
	 */
	
	
	static Connection getDBConnection(){
		 if(mainConnection != null)
			 return mainConnection;
		 
				try
				{
					// loading Oracle Driver
		    		System.out.print("Looking for Oracle's jdbc-odbc driver ... ");
			    	DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
			    	System.out.println(", Loaded.");

					String URL = "jdbc:oracle:thin:@localhost:1521:orcl";
			    	String userName = "system";
			    	String password = "password";

			    	System.out.print("Connecting to DB...");
			    	mainConnection = DriverManager.getConnection(URL, userName, password);
			    	System.out.println(", Connected!");
		   		}
		   		catch (Exception e)
		   		{
		     		System.out.println( "Error while connecting to DB: "+ e.toString() );
		     		e.printStackTrace();
		     		System.exit(-1);
		   		}
				return mainConnection;
		    
	}
	
	

	/**
	 * Designing Options panel and populating components
	 */
	private void addComponentsToOptionsPanel(){
		Box box = Box.createVerticalBox();
		box.add(new JLabel("Active Feature Type",SwingConstants.LEFT));
		JPanel featuresPanel = new JPanel(new GridLayout(2, 2));
		featuresPanel.setBorder(BorderFactory.createLineBorder (Color.black, 1));
		featuresPanel.add(asCB);
		featuresPanel.add(new JLabel(""));
		featuresPanel.add(bldngCB);
		featuresPanel.add(stuCB);
		box.add(featuresPanel);

		//creating empty space
		box.add(new JPanel());
		
		JLabel queryLabel = new JLabel("Query",SwingConstants.LEFT);
		box.add(queryLabel);
		JPanel queriesRB = new JPanel();
		queriesRB.setLayout(new BoxLayout(queriesRB, BoxLayout.Y_AXIS));
		buttonGroup = new ButtonGroup();
		wholeRegion = new JRadioButton("Whole Region");
		wholeRegion.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent arg0) {
				getQuery().setSubmitFlag(false);
			}
			
		});
		pointQuery = new JRadioButton("Point Query");
		pointQuery.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent ie) {
				getQuery().setSubmitFlag(false);
				if(ie.getStateChange() == ItemEvent.SELECTED){
					pointQuery.setSelected(true);
					getQuery().setSelectedQuery("Point Query");
				}else{
					pointQuery.setSelected(false);
					getQuery().setSelectedQuery(null);
					setPqX(-300);
					setPqY(-300);
					
				}
				imagePanel.repaint();
			}
		});
		rangeQuery = new JRadioButton("Range Query");
		rangeQuery.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent ie) {
				getQuery().setSubmitFlag(false);
				if(ie.getStateChange() == ItemEvent.SELECTED){
					rangeQuery.setSelected(true);
					getQuery().setSelectedQuery("Range Query");
					setDrawPoly(true);
				}else{
					rangeQuery.setSelected(false);
					getQuery().setSelectedQuery(null);
					getPath().clear();
				}
				imagePanel.repaint();
			}
		});
		surrStudent = new JRadioButton("Surrounding Student");
		surrStudent.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent ie) {
				getQuery().setSubmitFlag(false);
				if(ie.getStateChange() == ItemEvent.SELECTED){
					surrStudent.setSelected(true);
					getQuery().setSelectedQuery("Surrounding Student");
				}else{
					surrStudent.setSelected(false);
					getQuery().setSelectedQuery(null);
				}
				imagePanel.repaint();
			}
			
		});
		emerQuery = new JRadioButton("Emergency Query");
		emerQuery.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent ie) {
				getQuery().setSubmitFlag(false);
				if(ie.getStateChange() == ItemEvent.SELECTED){
					emerQuery.setSelected(true);
					getQuery().setSelectedQuery("Emergency Query");
				}else{
					emerQuery.setSelected(false);
					getQuery().setSelectedQuery(null);
				}
				imagePanel.repaint();
			}
			
		});
		buttonGroup.add(wholeRegion);
		buttonGroup.add(pointQuery);
		buttonGroup.add(rangeQuery);
		buttonGroup.add(surrStudent);
		buttonGroup.add(emerQuery);
		queriesRB.add(wholeRegion);
		queriesRB.add(pointQuery);
		queriesRB.add(rangeQuery);
		queriesRB.add(surrStudent);
		queriesRB.add(emerQuery);
		queriesRB.setBorder(BorderFactory.createLineBorder (Color.black, 1));
		box.add(queriesRB);
		//creating empty space
		box.add(new JPanel());
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		submitButton = new JButton("Submit Query");
		submitButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent ae) {
				Query query = new Query();
				query.setAsFlag(asCB.isSelected());
				query.setBuildingFlag(bldngCB.isSelected());
				query.setStudentFlag(stuCB.isSelected());
				query.setSubmitFlag(true);
				for(Enumeration<AbstractButton> radButtons = buttonGroup.getElements(); radButtons.hasMoreElements();){
					AbstractButton ab = radButtons.nextElement();
					if(ab.isSelected()){
						query.setSelectedQuery(ab.getText());
						break;
					}
				}
				setQuery(query);
				imagePanel.repaint();
			}
		});
		buttonPanel.add(submitButton);
		buttonPanel.setBorder(BorderFactory.createLineBorder (Color.black, 1));
		box.add(buttonPanel);
		optionsPanel.add(box);
	}
	
	/**
	 * a Kick Start of the application
	 * @param args
	 */
	public static void main(String[] args) {
		new hw2();
	}

	public JRadioButton getPointQuery() {
		return pointQuery;
	}

	public void setPointQuery(JRadioButton pointQuery) {
		this.pointQuery = pointQuery;
	}
//}//end of main program hw2

	
	private void loadImagePanel(){
	imagePanel = new JPanel(){
		@Override
		protected void paintComponent(Graphics g) {
			// TODO Auto-generated method stub
			super.paintComponents(g);
				//drawing image
				g.drawImage(image, 0, 0, null);
				
				Query q = getQuery();
				if(q == null || q.getSelectedQuery() == null)
					return;
				
				if(q.getSelectedQuery().trim().equalsIgnoreCase("Whole Region"))
				{
					if(getQuery().isSubmitFlag())
					{
						try
						{
							StringBuffer sqlText = new StringBuffer();
								Connection con=hw2.getDBConnection();
								Statement st=con.createStatement();
								
								if(getQuery().isBuildingFlag())
								{
									String sqlAll="select shape from buildings1";
									ResultSet rs=st.executeQuery(sqlAll);
									drawCustomPolygon(g,FeatureTypeEnum.BUILDINGS,rs,Color.YELLOW);
								}
								
								if(getQuery().isAsFlag())
								{
									String sqlAll="select loc,rad from announcementsystems";
									ResultSet rs=st.executeQuery(sqlAll);
									drawCustomPolygon(g,FeatureTypeEnum.AS,rs,Color.RED);
								}
								
								if(getQuery().isStudentFlag())
								{
									String sqlAll="select cord from test1";
									ResultSet rs=st.executeQuery(sqlAll);
									drawCustomPolygon(g,FeatureTypeEnum.STUDENTS,rs,Color.GREEN);
								}
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}
				}//whole region ends
				
				if(q.getSelectedQuery().trim().equalsIgnoreCase("Point Query")){
					g.setColor(Color.RED);
					g.drawRect(pqX, pqY, 5, 5);
					g.drawOval(pqX-50, pqY-50, 50*2, 50*2);
					if(getQuery().isSubmitFlag())
					{
					StringBuffer sqlText = new StringBuffer();
					try {
						Connection con=hw2.getDBConnection();
						Statement st=con.createStatement();
						if(getQuery().isBuildingFlag()){
							String sqlAll="select a.shape from buildings1 a where " +
									"sdo_geom.relate(sdo_geometry(2003,null" +
									",null,sdo_elem_info_array(1,1003,4),sdo_ordinate_array("+(pqX-50)+","+(pqY)+","+(pqX+50)+","+(pqY)+","+(pqX)+","+(pqY-50)+"))," +
									"'anyinteract',a.shape,0.005)='TRUE'";
							
							String sqlNearest="select b.shape from buildings1 b where " +
									"sdo_nn(b.shape,sdo_geometry(2001,null" +
									",sdo_point_type("+pqX+","+(pqY)+",null),null,null),"+
									"'sdo_num_res=1')='TRUE'";
							ResultSet rs1=st.executeQuery(sqlAll);
							drawCustomPolygon(g,FeatureTypeEnum.BUILDINGS,rs1,Color.GREEN);
							ResultSet rs2=st.executeQuery(sqlNearest);
							drawCustomPolygon(g,FeatureTypeEnum.BUILDINGS,rs2,Color.YELLOW);
							sqlText.append(sqlAll + "\n" +sqlNearest);
						}
						if(getQuery().isAsFlag())
						{
							String sqlAll ="select a.loc,a.rad from announcementsystems a where " +
									"sdo_geom.relate(sdo_geometry(2003,null,null,sdo_elem_info_array(1,1003,4)," +
									"sdo_ordinate_array("+(pqX-50)+","+(pqY)+","+(pqX+50)+","+(pqY)+","+(pqX)+","+(pqY-50)+")),'anyinteract',a.loc,0.005)='TRUE'";
							
							String sqlNearest="select b.loc,b.rad from announcementsystems b where " +
									"sdo_nn(b.loc,sdo_geometry(2001,null" +
									",sdo_point_type("+pqX+","+(pqY)+",null),null,null),"+
									"'sdo_num_res=1')='TRUE'";
							ResultSet rs1=st.executeQuery(sqlAll);
							drawCustomPolygon(g,FeatureTypeEnum.AS,rs1,Color.GREEN);
							ResultSet rs2=st.executeQuery(sqlNearest);
							drawCustomPolygon(g,FeatureTypeEnum.AS,rs2,Color.YELLOW);
							sqlText.append(sqlAll + "\n" +sqlNearest);
						}
						if(getQuery().isStudentFlag())
						{
							String sqlAll=" select s.cord from test1 s where " +
									"sdo_geom.relate(sdo_geometry(2003,null" +
									",null,sdo_elem_info_array(1,1003,4),sdo_ordinate_array("+(pqX-50)+","+(pqY)+","+(pqX+50)+","+(pqY)+","+(pqX)+","+(pqY-50)+"))," +
									"'anyinteract',s.cord,0.005)='TRUE'";
							String sqlNearest="select s.cord from test1 s where " +
									"sdo_nn(s.cord,sdo_geometry(2001,null" +
									",sdo_point_type("+pqX+","+(pqY)+",null),null,null),"+
									"'sdo_num_res=1')='TRUE'";
							ResultSet rs1=st.executeQuery(sqlAll);
							drawCustomPolygon(g,FeatureTypeEnum.STUDENTS,rs1,Color.GREEN);
							ResultSet rs2=st.executeQuery(sqlNearest);
							drawCustomPolygon(g,FeatureTypeEnum.STUDENTS,rs2,Color.YELLOW);
							sqlText.append(sqlAll + "\n" +sqlNearest);
						}
						getDispQuery().setText(sqlText.toString());
						st = null;
					} catch (SQLException e) {
						e.printStackTrace();
					}
					}	
					
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
							xi= path.get(i).x;
							yi= path.get(i).y;
							g.drawLine(a, b, c, d);
							}
						}
					if(getQuery().isSubmitFlag())
					{
					StringBuffer sqlText = new StringBuffer();
					try {
						Connection con=hw2.getDBConnection();
						Statement st=con.createStatement();
						String coordinates="";
						for(int i=0;i<path.size();i++)
						{
							coordinates+=path.get(i).x+","+path.get(i).y+",";
						}
						coordinates+=path.get(0).x+","+path.get(0).y;
						if(getQuery().isBuildingFlag()){
							
							String sqlAll="select a.shape from buildings1 a where " +
									"sdo_geom.relate(sdo_geometry(2003,null" +
									",null,sdo_elem_info_array(1,1003,1),sdo_ordinate_array("+coordinates+"))," +
									"'anyinteract',a.shape,0.005)='TRUE'";
							
							ResultSet rs1=st.executeQuery(sqlAll);
							drawCustomPolygon(g,FeatureTypeEnum.BUILDINGS,rs1,Color.YELLOW);
							sqlText.append(sqlAll + "\n" );
							getDispQuery().setText(sqlText.toString());
						}
						if(getQuery().isAsFlag())
						{
							String sqlAll ="select a.loc,a.rad from announcementsystems a where " +
									"sdo_geom.relate(sdo_geometry(2003,null,null,sdo_elem_info_array(1,1003,1)," +
									"sdo_ordinate_array("+coordinates+")),'anyinteract',a.loc,0.005)='TRUE'";
							
							ResultSet rs1=st.executeQuery(sqlAll);
							drawCustomPolygon(g,FeatureTypeEnum.AS,rs1,Color.RED);
							sqlText.append(sqlAll + "\n" );
							getDispQuery().setText(sqlText.toString());
						}
						
						if(getQuery().isStudentFlag())
						{
							String sqlAll=" select s.cord from test1 s where " +
									"sdo_geom.relate(sdo_geometry(2003,null" +
									",null,sdo_elem_info_array(1,1003,1),sdo_ordinate_array("+coordinates+"))," +
									"'anyinteract',s.cord,0.005)='TRUE'";
							
							ResultSet rs1=st.executeQuery(sqlAll);
							drawCustomPolygon(g,FeatureTypeEnum.STUDENTS,rs1,Color.GREEN);
							sqlText.append(sqlAll + "\n");
						}
						getDispQuery().setText(sqlText.toString());
						st = null;
					}		
					catch(Exception e)
					{
						e.printStackTrace();
					}
					}
					
					
					}	// end of Range Query
				
				if(q.getSelectedQuery().trim().equalsIgnoreCase("Surrounding Student"))
				{
					g.setColor(Color.RED);
					StringBuffer sqlText = new StringBuffer();
					g.drawRect(pqX, pqY, 5, 5);
					try {
						Connection con=hw2.getDBConnection();
						Statement st=con.createStatement();
						String sqlNearest="select a.loc,a.rad from announcementsystems a where " +
								"sdo_nn(a.loc,sdo_geometry(2001,null" +
								",sdo_point_type("+pqX+","+(pqY)+",null),null,null),"+
								"'sdo_num_res=1')='TRUE'";
						ResultSet rs2=st.executeQuery(sqlNearest);
						drawCustomPolygon(g,FeatureTypeEnum.AS,rs2,Color.RED);
						
						if(getQuery().isSubmitFlag())
						{
							int xco,yco,rad;
							String sqlAll="select p.rad,q.X,q.Y from announcementsystems p" +
									",table(sdo_util.getvertices(p.loc))q where p.bid in" +
									"(select a.bid from announcementsystems a where sdo_nn" +
									"(a.loc,sdo_geometry(2001,null" +
									",sdo_point_type("+pqX+","+(pqY)+",null),null,null),"+
									"'sdo_num_res=1')='TRUE')";
							ResultSet rs3=st.executeQuery(sqlAll);
							rs3.next();
							 rad=rs3.getInt(1);
							 xco=rs3.getInt(2);
							 yco=rs3.getInt(3);
							String sqlsurr="select c.cord from test1 c where sdo_geom.relate" +
									"(c.cord,'anyinteract',sdo_geometry(2003,null,null,sdo_elem_info_array(1,1003,4)" +
									",sdo_ordinate_array("+(xco+rad)+","+(yco)+","+(xco-rad)+","+(yco)+","+(xco)+","+(yco+rad)+
									")),0.005)='TRUE'";
							ResultSet rs8=st.executeQuery(sqlsurr);
							drawCustomPolygon(g,FeatureTypeEnum.STUDENTS,rs8,Color.GREEN);	
							sqlText.append("Query1:"+sqlAll + "\n"+"Query2:" +sqlNearest+"\n"+"Query3:"+sqlsurr);
							getDispQuery().setText(sqlText.toString());
						}	
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
						
				}//surronding query end
				
				if(q.getSelectedQuery().trim().equalsIgnoreCase("Emergency Query"))
				{
					g.setColor(Color.RED);
					StringBuffer sqlText = new StringBuffer();
					g.drawRect(pqX, pqY, 5, 5);
					int no4=0;
					try {
						Connection con=hw2.getDBConnection();
						Statement st=con.createStatement();
						String sqlNearest="select a.loc,a.rad from announcementsystems a where " +
								"sdo_nn(a.loc,sdo_geometry(2001,null" +
								",sdo_point_type("+pqX+","+(pqY)+",null),null,null),"+
								"'sdo_num_res=1')='TRUE'";
						no4++;
						sqlText.append("Query"+no4+":"+sqlNearest+"\n");
						ResultSet rs2=st.executeQuery(sqlNearest);
						drawCustomPolygon(g,FeatureTypeEnum.AS,rs2,Color.RED);
						
						if(getQuery().isSubmitFlag())
						{
							int xco,yco,rad;
							String aname;
							no4++;
							String sqlAll="select p.rad,p.bid,q.X,q.Y from announcementsystems p" +
									",table(sdo_util.getvertices(p.loc))q where p.bid in" +
									"(select a.bid from announcementsystems a where sdo_nn" +
									"(a.loc,sdo_geometry(2001,null" +
									",sdo_point_type("+pqX+","+(pqY)+",null),null,null),"+
									"'sdo_num_res=1')='TRUE')";
							sqlText.append("Query"+no4+":"+sqlAll+"\n");
							ResultSet rs3=st.executeQuery(sqlAll);
							rs3.next();
							rad=rs3.getInt(1);
							aname=rs3.getString(2);
							xco=rs3.getInt(3);
							yco=rs3.getInt(4);
							String[] A=new String[200];
							String[] alt=new String[100];
							String rem="select bid from announcementsystems where bid " +
									"not like '%"+aname+"%'";
							no4++;
							sqlText.append("Query"+no4+":"+rem+"\n");
							ResultSet remAS=st.executeQuery(rem);
							int i=0;
							while(remAS.next())
							{
								A[i]=remAS.getString(1);
								i++;
							}
							int k=0;
							for(k=0;k<i;k++)
							{
								no4++;
								alt[k]="select x.cord from announcementsystems y,test1 x " +
										"where sdo_nn(y.loc,x.cord,'sdo_num_res=2')='TRUE' and y.bid " +
										"like '%" +A[k]+"%'"+" and x.name in " +
										"(select a.name from test1 a where sdo_geom.relate" +
										"(a.cord,'anyinteract',sdo_geometry(2003,null," +
										"null,sdo_elem_info_array(1,1003,4)" +
										",sdo_ordinate_array("+(xco+rad)+","+(yco)+","+(xco-rad)
										+","+(yco)+","+(xco)+","+(yco+rad)+
										")),0.005)='TRUE')";
								sqlText.append("Query"+no4+":"+alt[k]+"\n");
							}
							ResultSet rs7=st.executeQuery(alt[0]);
							drawCustomPolygon(g,FeatureTypeEnum.STUDENTS,rs7,Color.WHITE);
							ResultSet rs8=st.executeQuery(alt[1]);
							drawCustomPolygon(g,FeatureTypeEnum.STUDENTS,rs8,Color.BLUE);
							ResultSet rs9=st.executeQuery(alt[2]);
							drawCustomPolygon(g,FeatureTypeEnum.STUDENTS,rs9,Color.CYAN);
							ResultSet rs10=st.executeQuery(alt[2]);
							drawCustomPolygon(g,FeatureTypeEnum.STUDENTS,rs10,Color.MAGENTA);
							ResultSet rs11=st.executeQuery(alt[2]);
							drawCustomPolygon(g,FeatureTypeEnum.STUDENTS,rs11,Color.green);
							getDispQuery().setText(sqlText.toString());
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
					
				}//Emergency Query Ends
					
			}
	};
	
			imagePanel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent me) {
					super.mouseClicked(me);
					getQuery().setSubmitFlag(false);
					pqX = me.getX();
					pqY = me.getY();
					Query q = getQuery();
					if(me.getButton() == MouseEvent.BUTTON1 && drawPoly && q.getSelectedQuery() != null && (q.getSelectedQuery().trim().equalsIgnoreCase("Range Query")) ){
						path.add(new Point(me.getX(), me.getY()));
						drawPoly = true;
										
					}else if(me.getButton() == MouseEvent.BUTTON3 && q.getSelectedQuery() != null && (q.getSelectedQuery().trim().equalsIgnoreCase("Range Query")) ){
						path.add(new Point(path.get(0).x, path.get(0).y));
						drawPoly = false;
					}
					repaint();
				}
				
			});
			
			imagePanel.addMouseMotionListener(new MouseAdapter() {
				@Override
				public void mouseMoved(MouseEvent me) {
					super.mouseMoved(me);
					x = me.getX();
					y = me.getY();
					repaint();
				}
			});
			
			//ToolTipManager.sharedInstance().registerComponent(this);
	}
		
		
		
		private void drawCustomPolygon(Graphics g, FeatureTypeEnum feature, ResultSet rs, Color color){
			GeometryAdapter sdoAdapter = OraSpatialManager.getGeometryAdapter("SDO", "9",STRUCT.class, null, null, hw2.getDBConnection());
			switch(feature){
			
				case BUILDINGS:	
					try {
						List<Building> buildings1 = new ArrayList<Building>();
						while(rs.next()){
							Building building =  new Building();
							STRUCT struct = (STRUCT)rs.getObject(1);
							Geometry geom = sdoAdapter.importGeometry( struct );
							oracle.sdoapi.geom.Polygon poly = (oracle.sdoapi.geom.Polygon) geom;
							oracle.sdoapi.geom.CurveString cs = poly.getExteriorRing();
						    oracle.sdoapi.geom.LineString lineString =(oracle.sdoapi.geom.LineString)cs;
						    CoordPoint[] tempPoints = lineString.getPointArray();
						    List<Double> xCords = new ArrayList<Double>();
							List<Double> yCords = new ArrayList<Double>();
						    for (int i=0; i<tempPoints.length; i++){
						       xCords.add(tempPoints[i].getX());
						       yCords.add(tempPoints[i].getY());
						      }
							building.setxCoOrds(xCords);
							building.setyCoOrds(yCords);
							buildings1.add(building);
							g.setColor(color);
							if(buildings1 != null && buildings1.size() > 0){
								for(Building eachBuilding : buildings1){
									Polygon poly1 = new Polygon(convertDoubleListToIntArray(eachBuilding.getxCoOrds()),convertDoubleListToIntArray(eachBuilding.getyCoOrds()),eachBuilding.getxCoOrds().size());
									g.drawPolygon(poly1);	
								}
							}
						}
					} catch (SQLException e) {
						e.printStackTrace();
					} catch (InvalidGeometryException e) {
						e.printStackTrace();
					} catch (GeometryInputTypeNotSupportedException e) {
						e.printStackTrace();
					}
					
					break;
				case AS:
					List<AnnSystem> announcements = new ArrayList<AnnSystem>();
					try {
						while(rs.next()){
							AnnSystem annSystem = new AnnSystem();
							STRUCT struct = (STRUCT)rs.getObject(1);
							Geometry geom = sdoAdapter.importGeometry( struct );
							if ( (geom instanceof oracle.sdoapi.geom.Point) )
							{
								oracle.sdoapi.geom.Point point0 = (oracle.sdoapi.geom.Point) geom;
								annSystem.setxCoOrd((int)point0.getX());
								annSystem.setyCoOrd((int)point0.getY());
								annSystem.setRadius(rs.getInt(2));
							}
							announcements.add(annSystem);
							g.setColor(color);
							if(announcements != null && announcements.size() > 0){
								for(AnnSystem eachAS : announcements){
									g.drawOval(eachAS.getxCoOrd()-eachAS.getRadius(), eachAS.getyCoOrd()-eachAS.getRadius(), eachAS.getRadius() * 2, eachAS.getRadius()*2);
								}
							}
						}
					} catch (SQLException e) {
						e.printStackTrace();
					} catch (InvalidGeometryException e) {
						e.printStackTrace();
					} catch (GeometryInputTypeNotSupportedException e) {
						e.printStackTrace();
					}
					break;
				case STUDENTS:
					List<Student> students = new ArrayList<Student>();
					try {
						while(rs.next()){
							Student student = new Student();
							STRUCT struct = (STRUCT)rs.getObject(1);
							Geometry geom = sdoAdapter.importGeometry( struct );
							if ( (geom instanceof oracle.sdoapi.geom.Point) )
				  			{
								oracle.sdoapi.geom.Point point0 = (oracle.sdoapi.geom.Point) geom;
								student.setxCoOrd((int)point0.getX());
								student.setyCoOrd((int)point0.getY());
				  			}
							students.add(student);
						}
						g.setColor(color);
						for(Student eachSt : students){
							g.drawRect(eachSt.getxCoOrd(), eachSt.getyCoOrd(), 10, 10);
						}
					} catch (Exception e) {
						e.printStackTrace();
					} 
					break;
			}
		
			rs = null;
		}
		
		/**
		 * This api is to convert List<Integer> to int[]
		 */
		private int[] convertDoubleListToIntArray(List<Double> doubleList){
			int[] intArray = new int[doubleList.size()];
			for(int i =0; i<doubleList.size(); i++){
				//intArray[i] = Integer.parseInt(String.valueOf(intList.get(i)));
				intArray[i] = doubleList.get(i).intValue();
			}
			return intArray;
		}
		 enum FeatureTypeEnum {
			   BUILDINGS(1),
			   AS(2),
			   STUDENTS(3);
			   
			   private int value;
			   
			   private FeatureTypeEnum(int value) {
			      this.value = value;
			   }
			   public int getValue() {
			      return value;
			   }
			}
		public int getX() {
			return x;
		}

		public void setX(int x) {
			this.x = x;
		}

		public int getY() {
			return y;
		}

		public void setY(int y) {
			this.y = y;
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

		public boolean isDrawPoly() {
			return drawPoly;
		}

		public void setDrawPoly(boolean drawPoly) {
			this.drawPoly = drawPoly;
		}

		public int getPqX() {
			return pqX;
		}

		public void setPqX(int pqX) {
			this.pqX = pqX;
		}

		public int getPqY() {
			return pqY;
		}

		public void setPqY(int pqY) {
			this.pqY = pqY;
		}

		public ArrayList<Point> getPath() {
			return path;
		}

		public void setPath(ArrayList<Point> path) {
			this.path = path;
		}

		public JTextArea getDispQuery() {
			return dispQuery;
		}

		public void setDispQuery(JTextArea dispQuery) {
			this.dispQuery = dispQuery;
		}

		
	 		
	}//end of main class 2	

/**
 * This is used to hold Announcement Systems data	
 * @author Meghamsh4N
 *
 */
class AnnSystem {
	
	private String asId;
	private int xCoOrd;
	private int yCoOrd;
	private int radius;
	
	public String getAsId() {
		return asId;
	}
	public void setAsId(String asId) {
		this.asId = asId;
	}
	public int getxCoOrd() {
		return xCoOrd;
	}
	public void setxCoOrd(int xCoOrd) {
		this.xCoOrd = xCoOrd;
	}
	public int getyCoOrd() {
		return yCoOrd;
	}
	public void setyCoOrd(int yCoOrd) {
		this.yCoOrd = yCoOrd;
	}
	public int getRadius() {
		return radius;
	}
	public void setRadius(int radius) {
		this.radius = radius;
	}

}

/**
 * This is used to hold Student data
 * @author Meghamsh4N
 *
 */
class Student {

	private String personId;
	private int xCoOrd;
	private int yCoOrd;
	
	public String getPersonId() {
		return personId;
	}
	public void setPersonId(String personId) {
		this.personId = personId;
	}
	public int getxCoOrd() {
		return xCoOrd;
	}
	public void setxCoOrd(int xCoOrd) {
		this.xCoOrd = xCoOrd;
	}
	public int getyCoOrd() {
		return yCoOrd;
	}
	public void setyCoOrd(int yCoOrd) {
		this.yCoOrd = yCoOrd;
	}

}

/**
 * This is used to hold Building data
 * @author Meghamsh4N
 *
 */
class Building{

	private String id;
	private String name;
	private int noOfVertices;
	private List<Double> xCoOrds;
	private List<Double> yCoOrds;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getNoOfVertices() {
		return noOfVertices;
	}
	public void setNoOfVertices(int noOfVertices) {
		this.noOfVertices = noOfVertices;
	}
	public List<Double> getxCoOrds() {
		return xCoOrds;
	}
	public void setxCoOrds(List<Double> xCoOrds) {
		this.xCoOrds = xCoOrds;
	}
	public List<Double> getyCoOrds() {
		return yCoOrds;
	}
	public void setyCoOrds(List<Double> yCoOrds) {
		this.yCoOrds = yCoOrds;
	}
}

/**
 * This is used to prepare Query criteria
 * @author Meghamsh4N
 *
 */
class Query{

	private boolean asFlag;
	private boolean buildingFlag;
	private boolean studentFlag;
	private String selectedQuery;
	private boolean submitFlag;
	
	public boolean isAsFlag() {
		return asFlag;
	}
	public void setAsFlag(boolean asFlag) {
		this.asFlag = asFlag;
	}
	public boolean isBuildingFlag() {
		return buildingFlag;
	}
	public void setBuildingFlag(boolean buildingFlag) {
		this.buildingFlag = buildingFlag;
	}
	public boolean isStudentFlag() {
		return studentFlag;
	}
	public void setStudentFlag(boolean studentFlag) {
		this.studentFlag = studentFlag;
	}
	public String getSelectedQuery() {
		return selectedQuery;
	}
	public void setSelectedQuery(String selectedQuery) {
		this.selectedQuery = selectedQuery;
	}
	public boolean isSubmitFlag() {
		return submitFlag;
	}
	public void setSubmitFlag(boolean submitFlag) {
		this.submitFlag = submitFlag;
	}
}