package com.siteview.nnm.main.mib;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Enumeration;

public class DwSnmpOidSupport
{

	DwSnmpMibTreeHash oidResolveHash;
	DwSnmpMibOutputHandler output=null;

	public DwSnmpOidSupport() {
		oidResolveHash=new DwSnmpMibTreeHash();
		outputText("OID Support Library initialized");
	}

	public String getNodeOid(DefaultMutableTreeNode node) {
		String strPath="";
        MenuTreeRecord  nodeInfo = (MenuTreeRecord )node.getUserObject();
		if(nodeInfo.recordType==nodeInfo.recVariable) return(nodeInfo.name + "-" + nodeInfo.syntax);
		try	{
			TreeNode[] nodePath1= node.getPath();
			List<TreeNode> nodePathList = new ArrayList<TreeNode>();
			for(int i=0;i<nodePath1.length;i++){
				MenuTreeRecord re = (MenuTreeRecord)(((DefaultMutableTreeNode)nodePath1[i]).getUserObject());
				if(re != null && re.number != 2000){
					nodePathList.add(nodePath1[i]);
				}
			}
			Object[] nodePath = nodePathList.toArray();
		 	MenuTreeRecord recTemp;
			if (nodePath.length < 2 ) return(".");
			for(int i=1;i<nodePath.length;i++)	{
				recTemp=(MenuTreeRecord)(((DefaultMutableTreeNode)nodePath[i]).getUserObject());
				//System.out.println(recTemp.name + "  " + recTemp.number);
				strPath=strPath.concat("." + String.valueOf (recTemp.number ));
			}
		}
		catch (Exception e1) {
			e1.printStackTrace();
				return("Error in getting path..\n"+e1.toString());
		}
		// Node OID Obtained, now check if it is in a table
		// For Table Element
		//System.out.println("Getting OID Name...");
		if(nodeInfo.recordType  == 3)
		{
			MenuTreeRecord  recParTemp=(MenuTreeRecord) ((DefaultMutableTreeNode)node.getParent()).getUserObject();
			if(recParTemp.tableEntry==-1)	strPath=strPath.concat(".("+1 + " - " + "n"+")");
			else
				strPath=strPath.concat(".(" + 1 + " - " + String.valueOf(recParTemp.tableEntry)+")");
		}
		else
		if(nodeInfo.recordType  == 2)
		{
			if(nodeInfo.tableEntry==-1)	strPath=strPath.concat(".(1-"+node.getChildCount()+")"+ ".(1-" + "n)");
			else
				strPath=strPath.concat(".(1-"+node.getChildCount()+")"+ ".(1-" + String.valueOf(nodeInfo.tableEntry)+")");
		}
		else
		if(node.isLeaf() == true) strPath=strPath.concat(".0");
		else
		strPath=strPath.concat(".*");
		//System.out.println(strPath);
		return strPath;
	}
	/**  END OF getNodeOid
	 */

	/** getNodeOidQuery : RETURNS OID VALUES SUCH THAT THEY CAN
	 *					  BE STRAIGHTAWAY USED FOR QUERIES
	 */

	public String getNodeOidQuery(DefaultMutableTreeNode node) {

		String strPath="";
        MenuTreeRecord  nodeInfo = (MenuTreeRecord )node.getUserObject();
		try	{
			TreeNode[] nodePath= node.getPath();
			MenuTreeRecord recTemp;
			if (nodePath.length < 2 ) return(".");
			for(int i=2;i<nodePath.length;i++)	{
				recTemp=(MenuTreeRecord)(((DefaultMutableTreeNode)nodePath[i]).getUserObject());
				//System.out.println(recTemp.name + "  " + recTemp.number);
				strPath=strPath.concat("." + String.valueOf (recTemp.number ));
			}
		}
		catch (Exception e1) {
			e1.printStackTrace();
				return "";
		}
		// Node OID Obtained, now check if it is in a table
		// For Table Element
		if(nodeInfo.recordType  == 3)
		{
			MenuTreeRecord  recParTemp=(MenuTreeRecord) ((DefaultMutableTreeNode)node.getParent()).getUserObject();
			if(recParTemp.tableEntry==-1)	strPath=strPath.concat(".65535");
			else
				strPath=strPath.concat("."+String.valueOf(recParTemp.tableEntry));
		}
		else
		if(nodeInfo.recordType  == 2)
		{
			if(nodeInfo.tableEntry==-1)	strPath=strPath.concat(".1.1");
			else
				strPath=strPath.concat(".1." + String.valueOf(nodeInfo.tableEntry));
		}
		else
		if(node.isLeaf() == true) strPath=strPath.concat(".0");
		else
		strPath=strPath.concat(".0");
		return strPath;
	}
	/**  END OF getNodeOidQuery
	 */



	/** getNodeOidActual	RETURNS THE ACTUAL OID, WITHOUT
	 *						APPENDING ANYTHING. MAINLY USED
	 *						FOR OID TO NAME RESOLVING.
	 *
	 */
	public String getNodeOidActual(DefaultMutableTreeNode node) {

		String strPath="";
		try	{
			TreeNode[] nodePath= node.getPath();
			MenuTreeRecord recTemp;
			if (nodePath.length < 2 ) return(".");
			for(int i=2;i<nodePath.length;i++)	{
				recTemp=(MenuTreeRecord)(((DefaultMutableTreeNode)nodePath[i]).getUserObject());
				strPath=strPath.concat("." + String.valueOf (recTemp.number ));
			}
		}
		catch (Exception e1) {
			e1.printStackTrace();
				return("Error, cannot resolve OID name");
		}
		return strPath;
	}

	void buildOidToNameResolutionTable(DefaultMutableTreeNode treeRoot)
	{
		try {
			DefaultMutableTreeNode node;
			MenuTreeRecord nodeRec;
			Enumeration enu=treeRoot.breadthFirstEnumeration();
			while(enu.hasMoreElements()) {
				node=(DefaultMutableTreeNode) enu.nextElement();
				nodeRec=(MenuTreeRecord) node.getUserObject();
				String sRec=getNodeOidActual(node);
				oidResolveHash.put(sRec,nodeRec.name);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String resolveOidName(String oid)
	{
		String objName=null;
		String oidCopy;

                if(oid.startsWith(".")) oidCopy=oid.toString();
                else oidCopy="." + oid.toString();

		try {
			oidCopy=oidCopy.substring(0,oidCopy.lastIndexOf('.'));

			while(objName==null && oidCopy.length()>2) {
				objName=(String)oidResolveHash.get(oidCopy);
				oidCopy=oidCopy.substring(0,oidCopy.lastIndexOf('.'));
			}
			if(objName==null) return("***");
		}
		catch(Exception e) { 
			e.printStackTrace(); 
			}
		return(objName+oid.substring(oid.indexOf(".",oidCopy.length()+1)));
	}


	public void setOutput(DwSnmpMibOutputHandler output) {
		this.output=output;
	}
	void outputText(String s) {
		try {
			System.out.println(s);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	void outputError(String s) {
		try {
			System.out.println(s);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}




