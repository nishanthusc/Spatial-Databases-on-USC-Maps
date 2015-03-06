package com.hw2;

import java.io.Serializable;
import java.util.List;

public class Building implements Serializable{
	private static final long serialVersionUID = 6615308598535857637L;
	
	private String id;
	private String name;
	private int noOfVertices;
	private List<Integer> xCoOrds;
	private List<Integer> yCoOrds;
	
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

	public List<Integer> getxCoOrds() {
		return xCoOrds;
	}

	public void setxCoOrds(List<Integer> xCoOrds) {
		this.xCoOrds = xCoOrds;
	}

	public List<Integer> getyCoOrds() {
		return yCoOrds;
	}

	public void setyCoOrds(List<Integer> yCoOrds) {
		this.yCoOrds = yCoOrds;
	}
	
	
	
}
