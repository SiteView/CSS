package com.siteview.nnm.main.pojo;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import com.siteview.nnm.main.mib.MenuTreeRecord;

/**
 * �˵����Ľڵ����
 * 
 * @author haiming.wang
 * 
 */
public class MenuNode extends DefaultMutableTreeNode implements Comparable {

	public MenuNode() {
	}

	public MenuNode(Object userObject) {
		this(userObject, true);
	}
	public MenuNode(Object userObject, boolean allowsChildren) {
        super();
        parent = null;
        this.allowsChildren = allowsChildren;
        this.userObject = userObject;
    }

	public Vector<MenuNode> getChildrenVector(){
		return this.children;
	}
	@Override
	public int compareTo(Object o) {
		if (o instanceof MenuNode) {
			MenuNode compObj = (MenuNode) o;
			MenuTreeRecord record = (MenuTreeRecord) compObj.getUserObject();
			if (record != null) {
				MenuTreeRecord thisRecord = (MenuTreeRecord) this
						.getUserObject();
				return (thisRecord.number - record.number);
			}
			throw new RuntimeException("MenuTreeRecord is null");
		}
		throw new RuntimeException("Object is not MenuNode");
	}
	public void addMenuNode(MenuNode newChild) {
		if(newChild != null && newChild.getParent() == this)
            insert(newChild, getChildCount() - 1);
        else
            insert(newChild, getChildCount());
	}
	
}
