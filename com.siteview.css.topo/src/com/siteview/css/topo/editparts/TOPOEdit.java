package com.siteview.css.topo.editparts;

import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.csstudio.opibuilder.editor.OPIEditor;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.ConnectionModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.widgets.model.LabelModel;
import org.csstudio.ui.util.NoResourceEditorInput;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.runtime.IPath;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.graph.DirectedGraph;
import org.eclipse.draw2d.graph.DirectedGraphLayout;
import org.eclipse.draw2d.graph.Edge;
import org.eclipse.draw2d.graph.EdgeList;
import org.eclipse.draw2d.graph.NodeList;
import org.eclipse.draw2d.graph.Node;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.NewExampleAction;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import com.siteview.css.topo.models.TopologyModel;

public class TOPOEdit extends OPIEditor{
	/**模型节点*/
	LabelModel[] model=new LabelModel[13];
	/**连接模型*/
	ConnectionModel[] cModels = new ConnectionModel[model.length];
	/**测试数据*/
	List testList = new ArrayList();
	
	int nnodes;
	//List list =  new A
	private StringBuffer strContext = new StringBuffer("");
	public static final String ID = "com.siteview.css.topo.editparts.TOPOEdit";
	static Random rand = new Random(90);
	public TOPOEdit(){
		super();
	}
	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
	}
	
 /*   //查找节点    
    int findNode(String lbl) {//x-a,x-b,a-c,a-f,b-d,b-e
    	System.out.println("初始值="+nnodes+"   lbl="+lbl);
		for (int i = 0 ; i < nnodes ; i++) {
			System.out.println("现在获得的lbl是："+model[i].getIndex()+"   i="+i);
		    if ((model[i].getIndex()+"").equals(lbl)) {
		    	System.out.println("返回的i为:"+i);
		    	return i;
		    }
		}
		return addNode(lbl);
    }
    //添加节点 设置节点坐标
    int x=300;
    int y=0;
    int addNode(String lbl) {
    	
	    model[nnodes].setX(x);  //(int) (10 + 380*Math.random())
	    model[nnodes].setY(y);
		y=y+30;
		x=x+20;
		return nnodes++;
    }
    
    //添加边线  0-1,0-2,0-3,1-4,1-5,1-6,2-7,2-8,2-9,3-10,3-11,3-12
    void addEdge(String from, String to) {//x-a,x-b,a-c,a-f,b-d,b-e
    	System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~from="+from+" to="+to);
		System.out.println("=================from=============");
		findNode(from);
		System.out.println("=================to===============");
		findNode(to);
    }*/
    
    
	@Override
	protected void createGraphicalViewer(Composite parent) {
		super.createGraphicalViewer(parent);
		DisplayModel displayModel = getDisplayModel();
		
		int oneModel=1;
		int oneConnectModel=1;
		for (int i = 0; i < model.length; i++) {
			//创建 i个模型
			model[i] = new LabelModel();
			//创建i个连接线模型
			cModels[i] = new ConnectionModel(displayModel);
			//设置模型坐标 
			getDisplayModel().addChild(model[i]);
		}
		//解析设置模型坐标
		TopoParsing topParsing= new TopoParsing();
		Map<String, String> map =topParsing.readFileByChars();
		Iterator its = map.keySet().iterator();
		while (its.hasNext()) {
			String key = (String) its.next();
			String value = map.get(key);
			String[] values = value.split("-");
			String value1[] =values[0].split("[.]");
			String value2[] =values[1].split("[.]");
		    model[Integer.parseInt(key)].setX(Integer.parseInt(value1[0]));  
		    model[Integer.parseInt(key)].setY(Integer.parseInt(value2[0])); 
			//System.out.println("key="+key+"-----value="+value);
		}
		
//		/**
//		 * 1.解析gml获得坐标 key=0 value=300,0  map存放
//		 */
//		int centerX = 400;//圆心坐标  
//		int centerY = 300;  
//		int radius = 200;//半径  
//		//count: 节点数目  
//		int count =13;
//		for (int i= 0; i<count; i++)  
//		{  
//		    int x = centerX+ (int)(radius * Math.cos(Math.PI * 2 / count * i));  
//		    int y = centerY+ (int)(radius * Math.sin(Math.PI * 2 / count * i));  
//		    model[i].setX(x);  
//		    model[i].setY(y);  
//		}
		
		String t = "0-1,0-2,0-3,1-4,1-5,1-6,2-7,2-8,2-9,3-10,3-11,3-12";
		String test[] = t.split(",");
		for (int i = 0; i < test.length; i++) {
			testList.add(test[i]);
		}
		Iterator it = testList.iterator();
		String mStr = "";
		String spitStr[];
		for (int i = 0; i < test.length; i++) {
			mStr = (String) it.next();
			spitStr = mStr.split("-");
			cModels[i].connect(model[Integer.parseInt(spitStr[0])], "BOTTOM", model[Integer.parseInt(spitStr[1])], "TOP");
			//addEdge(spitStr[0],spitStr[1]);//设置模型坐标
		}

		load();
	}
	
	/**Task:step
	 * 1.生成gml文件
	 * 2.调用yEd API 生成想要的布局方式
	 * 3.把gml转换为OPI格式
	 */
	private void load() {
		// TODO Auto-generated method stub
		System.out.println("加载");
		DrawHead();
		QueryBusinessObjectDef();
		DrawOver();
	}

	
	/**
	 * 查询所有业务对象
	 * 1.查询所有模型。
	 * 2.获取节点与边的信息
	 */
	private void QueryBusinessObjectDef() {
		//画节点
		for (int i = 0; i < model.length; i++) {
			Drawnode(482, 84, DrawWidth("拓扑图", 30), 30,
					model[i].getWUID(),model[i].getName(), "rectangle", "#FFCC00","#000000");
		}
		//画边
		for(int j = 0; j < cModels.length-1; j++){
			Drawedge(cModels[j].getSource().getWUID(), cModels[j].getTarget().getWUID(),
					"备注信息", "#000000", "standard");
		}
	}
	
	
	/**
	 * 生成结束
	 */
	private void DrawOver() {
		strContext.append("] \n");
		//设置一个默认文件夹路径
		String path ="C:\\拓扑图\\"; 		
		File fileUpdate = new File(path);
		if (fileUpdate.exists() == false) {
			fileUpdate.mkdirs();
		}else {
			System.out.println("文件路径存在:" + path);
		}
		//创建gml文件
		File f = new File(path+"css.gml");
		OutputStream out = null;
		try {
			out = new FileOutputStream(f);
			byte b[] = strContext.toString().getBytes();
			for (int i = 0; i < b.length; i++) {
				out.write(b[i]);
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 生成头部
	 */
	private void DrawHead() {
		strContext.append("Creator	\"yFiles\" ");
		strContext.append("\nVersion	\"2.8\" ");
		strContext.append("\ngraph ");
		strContext.append("\n[\n\thierarchic	1");
		strContext.append("\n\tlabel	\"\"");
		strContext.append("\n\tdirected	1 \n");
	}
	
	
	/**Task:生成节点
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param id
	 * @param name
	 * @param type
	 * @param color
	 * @param outline
	 */
	public void Drawnode(int x, int y, int w, int h, String id, String name,
			String type, String color, String outline) {
		strContext.append("\t node\n \t[\n");
		strContext.append("\t\t id \"" + id + "\"\n");
		strContext.append("\t\t label \"" + name + "\"\n");
		strContext.append("\t\t graphics\n \t\t[ \n");
		strContext.append("\t\t\t x " + x + "\n");
		strContext.append("\t\t\t y " + y + "\n");
		strContext.append("\t\t\t w " + w + "\n");
		strContext.append("\t\t\t h " + h + "\n");
		strContext.append("\t\t\t type \"" + type + "\"\n");
		strContext.append("\t\t\t fill \"" + color + "\"\n");
		strContext.append("\t\t\t outline \"" + outline + "\"\n");
		strContext.append("\t\t ] \n");
		strContext.append("\t\t LabelGraphics\n \t\t [ \n");
		strContext.append("\t\t\t text \"" + name + "\"\n");
		strContext.append("\t\t\t fontSize 12\n");
		strContext.append("\t\t\t fontName \"Dialog\"\n");
		strContext.append("\t\t\t anchor \"c\"\n");
		strContext.append("\t\t ] \n");
		strContext.append("\t ] \n");
	}
	
	
	/**Task:生成边
	 * @param source
	 * @param target
	 * @param name
	 * @param color
	 * @param targetArrow
	 */
	public void Drawedge(String source, String target, String name,
			String color, String targetArrow) {
		// name="";
		strContext.append("\t edge\n \t[\n");
		strContext.append("\t\t source \"" + source + "\"\n");
		strContext.append("\t\t target \"" + target + "\"\n");
		strContext.append("\t\t label \"" + name + "\"\n");
		strContext.append("\t\t graphics\n \t\t[ \n");
		strContext.append("\t\t\t fill \"" + color + "\"\n");
		strContext.append("\t\t\t targetArrow \"" + targetArrow + "\"\n");
		strContext.append("\t\t ] \n");
		strContext.append("\t\t LabelGraphics\n \t\t [ \n");
		strContext.append("\t\t\t text \"" + name + "\"\n");
		strContext.append("\t\t\t fontSize 12\n");
		strContext.append("\t\t\t fontName \"Dialog\"\n");
		strContext.append("\t\t\t model \"six_pos\"\n");
		strContext.append("\t\t\t position \"tail\"\n");
		strContext.append("\t\t ] \n");
		strContext.append("\t ] \n");

	}
	
	
	/**
	 * 判断 生成 字符长度大小
	 */
	public int DrawWidth(String name, int number) {
		int width = name.length() * number;
		if (name.charAt(0) > 0 && name.charAt(0) < 127) {
			width = name.length() * number / 2;
		}
		return width;
	}
	
	/**
	 * @return the origin editor input before wrapped by {@link NoResourceEditorInput}.
	 */
	public IEditorInput getOriginEditorInput() {
		IEditorInput editorInput = super.getEditorInput();
		if(editorInput instanceof NoResourceEditorInput){
			return ((NoResourceEditorInput)editorInput).getOriginEditorInput();
		}
		return editorInput;
	}
	private InputStream getInputStream() {
		InputStream result = null;

		IEditorInput editorInput = getOriginEditorInput();

		if (editorInput instanceof FileEditorInput) {
			
//			try {
//				result = ((FileEditorInput) editorInput).getFile()
//						.getContents();
//			} catch (CoreException e) {
//				e.printStackTrace();
//			}
		} else if (editorInput instanceof FileStoreEditorInput) {
			IPath path = URIUtil.toPath(((FileStoreEditorInput) editorInput)
					.getURI());
			try {
				result = new FileInputStream(path.toFile());
			} catch (FileNotFoundException e) {
				result = null;
			}
		}
		return result;
	}
//========================
	private void buildLevelTree(Node v,int x){
		if (v.getOffsetIncoming() !=0) {
			
		}
	}
//==============
}