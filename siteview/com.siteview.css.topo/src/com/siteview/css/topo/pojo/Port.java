package com.siteview.css.topo.pojo;

import org.eclipse.draw2d.geometry.Rectangle;

public class Port {
	public int id;
	public String name;
	public boolean portState;
	public Point p;
	public long Width;
	public long Heigth;

	public Rectangle rec;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isPortState() {
		return portState;
	}

	public void setPortState(boolean portState) {
		this.portState = portState;
	}

	public Point getP() {
		return p;
	}

	public void setP(Point p) {
		this.p = p;
	}

	public long getWidth() {
		return Width;
	}

	public void setWidth(long width) {
		Width = width;
	}

	public long getHeigth() {
		return Heigth;
	}

	public void setHeigth(long heigth) {
		Heigth = heigth;
	}

	public Rectangle getRec() {
		return rec;
	}

	public void setRec(Rectangle rec) {
		this.rec = rec;
	}

}
