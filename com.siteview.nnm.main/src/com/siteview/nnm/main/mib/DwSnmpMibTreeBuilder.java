package com.siteview.nnm.main.mib;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.*;

import org.eclipse.jface.viewers.TreeViewer;

import com.siteview.nnm.main.pojo.MenuNode;

import java.util.Vector;
import java.util.Enumeration;
import java.io.File;


public class DwSnmpMibTreeBuilder implements DwMibParserInterface,Runnable
{
	DwSnmpMibOutputHandler output=null;
	private MenuNode rootNode;
	private MenuNode treeRootNode;
	private MenuNode rootOrphan;

	TreeViewer tree;

	
	private Vector fileVect;
	private Vector orphanNodes;

	private String errorMsg="";

	public DwSnmpOidSupport oidSupport=new DwSnmpOidSupport();
	DwSnmpMibTreeHash treeHash;
	DwSnmpMibTreeHash variableHash;
	DwSnmpMibTreeHash orphanHash;
	public DwSnmpMibTreeBuilder(MenuNode treeRootNode) {
		this.treeRootNode = treeRootNode;

		MenuTreeRecord rootRec=new MenuTreeRecord();
		rootRec.name = "root";
		rootRec.type = "mib";
		rootRec.parent="MIB Browser";
		rootRec.number=20001;
		rootNode=new MenuNode(rootRec);
		
		MenuTreeRecord rootOrphanRec=new MenuTreeRecord();
		rootOrphanRec.name="Orphans";
		rootOrphanRec.parent="MIB Browser";
		rootOrphanRec.type = "mib";
		rootOrphanRec.description = "This subtree contains MIB Records whose parent cannot be traced";
		rootOrphanRec.number=10;
		rootOrphan =new MenuNode(rootOrphanRec);

		treeHash=new DwSnmpMibTreeHash();
		treeHash.put(rootRec.name,rootNode);

		variableHash=new DwSnmpMibTreeHash();
		orphanHash =new DwSnmpMibTreeHash();

		orphanNodes=new Vector();
		fileVect=new Vector();
		clearError();
	}

	public MenuNode getRootNode() {
		return rootNode;
	}

	public boolean addFile(String fName) {
		if(fName==null) return false;
		File mibFile=new File(fName);
		if(mibFile.exists()!=true) return false;
		fileVect.add(fName);
		return true;
	}

	public boolean addDirectory(String dirName) {
		System.out.println("Adding directory : " + dirName);
		File dir=new File(dirName);
		if(!dir.isDirectory()) return false;
		File files[]=dir.listFiles();
		if(files==null) return false;
		for(int i=0;i<files.length;i++) {
			fileVect.add(files[i].getPath());
		}
		return true;
	}


	public String[] getFiles() {

		try {
			Enumeration enu=fileVect.elements();
			String returnStr[]=new String[fileVect.size()];

			int i=0;
			while(enu.hasMoreElements()) {
				returnStr[i++] = (String)enu.nextElement();
			}
			clearError();
			return returnStr;
		}
		catch(Exception e) {
			setError("Error in getting filenames..\n" + e.toString());
			return null;
		}
	}

	private void clearError() {
		errorMsg = "";
	}

	private void setError(String err) {
		errorMsg=err;
	}

	public void buildTree() {
		// Check if files have been added to list
		if(fileVect.size()==0) {
			setError("Error : Please add files first");
			return;
		}

		oidSupport=new DwSnmpOidSupport();
		Thread treeThread=new Thread(this);
		treeThread.setPriority(Thread.MAX_PRIORITY-1);
		treeThread.start();
		treeRootNode.add(rootNode);
	}

	public void run() {
		// Get filenames and add nodes to rootnode
		Enumeration enu=fileVect.elements();
		String fileName="";
		JTree newTree=null;
		while(enu.hasMoreElements()) {
			fileName=(String) enu.nextElement();
			loadFile(fileName);
		}
		updateOrphans();
	}

	public static void main(String[] args) {
		MenuTreeRecord record = new MenuTreeRecord();
		record.type="mib";
		record.name="root";
		MenuNode mn = new MenuNode(record);
		
		DwSnmpMibTreeBuilder builder = new DwSnmpMibTreeBuilder(mn);
		builder.output = new DwSnmpMibOutputHandler();
		builder.loadFile("D:\\TRANSPORT-ADDRESS-MIB");
	}
	private void loadFile(String fileName) {
		if(parseFile(fileName)<0) outputError(".. Error");
		else System.out.println("..Done\n");
	}
	
	public boolean loadNewFile(String fName) {
		if(fName==null) return false;
		File mibFile=new File(fName);
		if(mibFile.exists()!=true) return false;
		if(fileVect.indexOf(fName)==-1) {
			fileVect.add(fName);
			loadFile(fName);
			updateOrphans();
			return true;
		}
		return false;
	}

	@SuppressWarnings("static-access")
	private void  updateOrphans() {
		// Check if orphan's parents have arrived. if yes, remove them from orphan list
		outputText("Updating orphans.");
		MenuTreeRecord orphanRec=null;
		Enumeration orphanEnu;
		boolean contFlag=true;

		while(contFlag) {
			contFlag=false;
			orphanEnu=orphanNodes.elements();
			while(orphanEnu.hasMoreElements()) {
				MenuNode orphanNode = (MenuNode)orphanEnu.nextElement();

				if (addNode(orphanNode)) {
					contFlag=true;
					orphanNodes.remove(orphanNode);
					continue;
				}
			}
			System.out.println(".");
		}
		System.out.println("Done");
		System.out.println("\nBuilding OID Name resolution table...");
		oidSupport.buildOidToNameResolutionTable(rootNode);

		//Add remaining orphans to treeroot.orphans
		orphanEnu=orphanNodes.elements();
		while(orphanEnu.hasMoreElements()) {
			MenuNode orphanNode = (MenuNode)orphanEnu.nextElement();
			orphanRec=(MenuTreeRecord) orphanNode.getUserObject();
			if(orphanRec.recordType==orphanRec.recVariable) continue;
			if(treeHash.containsKey(orphanRec.name)!=true) rootOrphan.add(orphanNode);
		}

		// Add variables to varroot
		outputText("Updating variables table..");
		Enumeration enuVar=variableHash.elements();
		outputText("Done");
	}

	private int parseFile(String fName) {

		DwSnmpMibParser fParser=new DwSnmpMibParser(fName,this);
		if(output!=null) fParser.setOutput(output);
		return fParser.parseMibFile();
	}

	private boolean addRecord(MenuTreeRecord childRec) {
		childRec.type = "mib";
		int parseStatus=0;
		if(childRec==null) return false;
		MenuNode newNode=new MenuNode(childRec);
		if(!addNode(newNode)) {
				orphanNodes.add(newNode);
				orphanHash.put(childRec.name,newNode);
			return false;
		}
		return true;
	}


	@SuppressWarnings("rawtypes")
	private boolean addNode(MenuNode newNode) {
		MenuTreeRecord newRec=(MenuTreeRecord) newNode.getUserObject();
		newRec.type = "mib";
		DefaultMutableTreeNode parentNode = (MenuNode)treeHash.get(newRec.parent);
		//if(parentNode==null) { // See if parent is an orphan
		//	parentNode = (DefaultMutableTreeNode)orphanHash.get(newRec.parent);
		//}
		if(parentNode==null) return false;

		// Check if parent is a Table, and set the node tableEntry accordingly
		MenuTreeRecord parentRec=(MenuTreeRecord)parentNode.getUserObject();
		if(parentRec.recordType > 0) newRec.recordType =parentRec.recordType+1;

		DefaultMutableTreeNode dupNode=isChildPresent(newNode);
		if(dupNode == null){		// Add  node to  its parent
			try {
				parentNode.add(newNode);
				newNode.setParent(parentNode);
				// See if parent is not an orphan
				treeHash.put(newRec.name,newNode);
				return true;
			} catch(Exception e) {
				System.out.println("Err in Child : " + newRec.name + "Parent : " + newRec.parent);
				return false;
			}
		}
		else {      // Node already present. add all its children to the existing node
			Enumeration dupChildren=newNode.children();
			while(dupChildren.hasMoreElements()) {
				MenuNode dupChildNode=(MenuNode)dupChildren.nextElement();
				if(isChildPresent(dupChildNode)==null) dupNode.add(dupChildNode);
			}
			return true;
		}
	}

	MenuNode isChildPresent(MenuNode childNode) {
		MenuTreeRecord childRec=(MenuTreeRecord)childNode.getUserObject();
		return(isChildPresent(childRec));
	}

	MenuNode isChildPresent(MenuTreeRecord rec) {
		DefaultMutableTreeNode parentNode =(MenuNode) treeHash.get(rec.parent);
		if(parentNode==null) parentNode =(MenuNode) orphanHash.get(rec.parent);
		if(parentNode==null) return null;
		Enumeration enuChildren=parentNode.children();
		if (enuChildren!=null) {
			MenuNode childNode;
			MenuTreeRecord childRec;
			while(enuChildren.hasMoreElements()) {
				childNode=(MenuNode) enuChildren.nextElement();
				childRec=(MenuTreeRecord )childNode.getUserObject();
				if(childRec.name.equals(rec.name)) {
					return childNode;
				}
			}
		}
		return null; // Child does not exist
	}

	public void setOutput(DwSnmpMibOutputHandler output) {
		this.output=output;
	}

	void outputText(String s) {
		try {
			System.out.println(s);
		} catch(Exception e) {
			System.out.println(s);
		}
	}
	void outputError(String s) {
		try {
			System.out.println(s);
		} catch(Exception e) {
			System.out.println(s);
		}
	}

	public void newMibParseToken(MenuTreeRecord rec) {
		addRecord(rec);

	}


	public void parseMibError(String s) {
		outputError(s);
	}

}






