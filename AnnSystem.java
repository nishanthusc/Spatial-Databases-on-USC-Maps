package com.hw2;

import java.io.Serializable;

public class AnnSystem implements Serializable{
	private static final long serialVersionUID = 6867748628441327030L;
	
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
