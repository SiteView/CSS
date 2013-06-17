package com.siteview.css.topo.editparts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
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
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import com.siteview.css.topo.models.TopologyModel;

public class TOPOEdit extends OPIEditor {
	/**模型节点*/
	LabelModel[] model=new LabelModel[5];
	/**连接模型*/
	ConnectionModel[] cModels = new ConnectionModel[model.length];
	
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
			
//			model[i].setX(model[0].getX());
//			oneModel +=180;
			getDisplayModel().addChild(model[i]);
		}
		model[0].setX(200);
		model[0].setY(0);
		//如果横坐标相同，那么向右移动。如果纵坐标相同，那么向下移动。
		model[1].setX(0);
		model[1].setY(125);
		model[2].setX(150);
		model[2].setY(125);
		model[3].setX(300);
		model[3].setY(125);
		model[4].setX(450);
		model[4].setY(125);
		
		//设置连接模型
//		for (int i = 0; i < cModels.length-1; i++) {
//				cModels[i].connect(model[i], "BOTTOM", model[i+oneConnectModel], "TOP");
//		}
 
		cModels[0].connect(model[0], "BOTTOM", model[0+oneConnectModel], "TOP");//这里可以用键值对的形式
		cModels[1].connect(model[0], "BOTTOM", model[1+oneConnectModel], "TOP");
		cModels[2].connect(model[0], "BOTTOM", model[2+oneConnectModel], "TOP");
		cModels[3].connect(model[0], "BOTTOM", model[3+oneConnectModel], "TOP");
		
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

}