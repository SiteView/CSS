package com.siteview.nnm.main.pojo;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单树的节点对象
 * @author haiming.wang
 *
 */
public class MenuItem {

	private String name;
	private MenuItem parent;
	private String imageName;
	private List<MenuItem> children = new ArrayList<MenuItem>();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public MenuItem getParent() {
		return parent;
	}
	public void setParent(MenuItem parent) {
		this.parent = parent;
	}
	public List<MenuItem> getChildren() {
		return children;
	}
	
	public String getImageName() {
		return imageName;
	}
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}
	public void setChildren(List<MenuItem> children) {
		this.children = children;
	}
	public MenuItem(){}
	public MenuItem(MenuItem node){
		this.setParent(node);
	}
	/**
	 * 是否有子节点。
	 * @return
	 */
	public boolean hasChild(){
		return ((this.children != null) && (this.children.size() > 0));
	}
	/**
	 * 添加子节点
	 * @param child
	 */
	public void addChild(MenuItem child){
		child.setParent(this);
		this.children.add(child);
	}
	/**
	 * 移除子节点
	 * @param child
	 */
	public void removeChild(MenuItem child){
		this.children.remove(child);
	}
}
