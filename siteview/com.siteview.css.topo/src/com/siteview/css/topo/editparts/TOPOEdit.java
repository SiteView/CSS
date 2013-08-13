package com.siteview.css.topo.editparts;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;
import java.util.Map.Entry;

import org.csstudio.opibuilder.editor.OPIEditor;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.model.ConnectionModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.widgets.model.LabelModel;
import org.csstudio.swt.widgets.figures.PolylineFigure;
import org.csstudio.ui.util.NoResourceEditorInput;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.runtime.IPath;
import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MidpointLocator;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.FileEditorInput;


import ILOG.Diagrammer.GraphicObject;
import ILOG.Diagrammer.Link;
import ILOG.Diagrammer.GraphLayout.ForceDirectedLayout;
import ILOG.Diagrammer.GraphLayout.ForceDirectedLayoutReport;

import com.siteview.css.topo.common.TopoData;
import com.siteview.css.topo.models.DumbModel;
import com.siteview.snmp.common.ScanParam;
import com.siteview.snmp.constants.CommonDef;
import com.siteview.snmp.pojo.DevicePro;
import com.siteview.snmp.pojo.Edge;
import com.siteview.snmp.pojo.IDBody;
import com.siteview.snmp.util.ScanUtils;

public class TOPOEdit extends OPIEditor {
	
	final String TOP_LEFT = "TOP_LEFT";
	final String TOP = "TOP";
	final String TOP_RIGHT = "TOP_RIGHT";
	final String LEFT = "LEFT";
	final String RIGHT = "RIGHT";
	final String BOTTOM_LEFT = "BOTTOM_LEFT";
	final String BOTTOM = "BOTTOM";
	final String BOTTOM_RIGHT = "BOTTOM_RIGHT";

	public static final String PROP_IMAGE_FILE = "image_file";
	public static final String PROP_AUTOSIZE = "auto_size";
	private static final String[] FILE_EXTENSIONS = new String[] { "jpg",
			"jpeg", "gif", "bmp", "png" };
	int nnodes;
	public static final String ID = "com.siteview.css.topo.editparts.TOPOEdit";
	private TopoGraph container;
	public TOPOEdit() {
		super();
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
	}

	public void NetScan(Map<String, DevicePro> devtypemap,
			Map<String, Map<String, String>> specialoidlist, ScanParam param) {
	}

	
	protected void createGraphicalViewer(Composite parent) {
		super.createGraphicalViewer(parent);
		DisplayModel displayModel = getDisplayModel();

		// tree layout
		// TreeLayout treeLayout = new TreeLayout();
		// treeLayout.set_LayoutMode(TreeLayoutMode.Radial);
		// treeLayout.set_AspectRatio(1);
		// treeLayout.set_FirstCircleEvenlySpacing(true);

		// force layout
		ForceDirectedLayout forceLayout = new ForceDirectedLayout();
		forceLayout.SetLayoutReport(new ForceDirectedLayoutReport());
		forceLayout.set_PreferredLinksLength(200);
		forceLayout.set_RespectNodeSizes(true);

		TopoGraph container = new TopoGraph(displayModel);

		for (Entry<String, IDBody> entry : TopoData.deviceList.entrySet()) {
			String ip = entry.getKey();// 获取设备的所有ip包括亚设备的信息
			// System.out.println(ip+"```````````````````````"+entry.getValue());
			// entry.getValue();
			// 判断设备类型
			if (entry.getValue().getDevType().equals(CommonDef.OTHER)) {//亚设备-
				container.addNode(new TopoNode(container, ip, ModelFactory.getWidgetModel(ModelFactory.DUMBMODEL, ip), entry
						.getValue()));
			}else if (entry.getValue().getDevType().equals(CommonDef.ROUTE_SWITCH)) {//三层交换-
				AbstractPVWidgetModel model = ModelFactory.getWidgetModel(ModelFactory.SWITCH_ROUTER, ip);
				//model.setPropertyValue(AbstractPVWidgetModel.PROP_PVNAME, "sv://topo");
				container.addNode(new TopoNode(container, ip, model, entry
						.getValue()));
			}else if(entry.getValue().getDevType().equals(CommonDef.SWITCH)){//二层交换
				AbstractPVWidgetModel model = ModelFactory.getWidgetModel(ModelFactory.SWITCH, ip);
				//model.setPropertyValue(AbstractPVWidgetModel.PROP_PVNAME, "sv://topo");
			}else if(entry.getValue().getDevType().equals(CommonDef.ROUTER)){//路由器-
				AbstractPVWidgetModel model = ModelFactory.getWidgetModel(ModelFactory.ROUTER, ip);
				//model.setPropertyValue(AbstractPVWidgetModel.PROP_PVNAME, "sv://topo");
			}else if (entry.getValue().getDevType().equals(CommonDef.PC)) {//pc终端-
				container.addNode(new TopoNode(container, ip, ModelFactory.getWidgetModel(ModelFactory.TOPOLOGYMODEL, ip), entry
						.getValue()));
			}
		}
		
		
		String leftIp;
		String rightIp;
		for (int i = 0; i < TopoData.edgeList.size(); i++) {
			Edge edge = (Edge) TopoData.edgeList.get(i);
			leftIp = edge.getIp_left();
			rightIp = edge.getIp_right();
			// System.out.println(leftIp + "<-->" + rightIp);
			container.addLink(leftIp, rightIp);
		}

		forceLayout.Attach(container);

		forceLayout.Layout();
		List<GraphicObject> afterLayoutNodes = container.getNodes();
		if (afterLayoutNodes != null && afterLayoutNodes.size() > 0) {
			int tmpWidth = 0;
			for (GraphicObject object : afterLayoutNodes){
				if (object.isNode()) {
					int tmpX = object.getMode().getX();
					int tmpY = object.getMode().getY();
					tmpWidth = object.getMode().getWidth();
					int tmpHeight = object.getMode().getHeight();
					String id = object.getId();
					// int centerX = (int)(tmpX + tmpWidth/2);
					// int centerY = (int)(tmpY + tmpHeight/2);
					// Point centerP = new Point(centerX, centerY);
					// System.out.println("模型："+object.getId()+"x = " +
					// tmpX+"-----"+"y = "+tmpY);
					LabelModel labelModel = new LabelModel();
					labelModel.setText(id);
					labelModel.setForegroundColor(new RGB(255, 0, 255));
					labelModel.setX(tmpX-object.getMode().getWidth()+8);
					labelModel.setY(tmpY+object.getMode().getHeight()-8);
					displayModel.addChild(labelModel);
				}
				if (object.isLink()) {
					Link link = (Link) object;
					// System.out.println("起点："+link.getSource()+"  终点："+link.getTarget());
					link.connect(tmpWidth * 2, link.getSource(),
							link.getTarget());// 得到source，target的坐标
				}
			}
		}
		
		DumbModel dumbModel = new DumbModel();
		DumbModel dumbMode2 = new DumbModel();
		dumbModel.setX(1000);
		dumbModel.setY(100);
		dumbMode2.setX(1100);
		dumbMode2.setY(100);
		displayModel.addChild(dumbModel);
		displayModel.addChild(dumbMode2);
		ConnectionModel connectionModel1 = new ConnectionModel(displayModel);
		connectionModel1.connect(dumbModel, "TOP", dumbMode2, "BOTTOM");
		ConnectionModel connectionModel2 = new ConnectionModel(displayModel);
		connectionModel2.connect(dumbModel, "BOTTOM", dumbMode2, "TOP");
		
		
//		RectangleFigure node1 = new RectangleFigure(), node2 = new RectangleFigure();
//		node1.setBackgroundColor(ColorConstants.red);
//		node1.setBounds(new Rectangle(40, 40, 50, 30));
//		node2.setBackgroundColor(ColorConstants.blue);
//		node2.setBounds(new Rectangle(200, 40, 50, 30));
//
//		PolylineConnection conn = new PolylineConnection();
//		conn.setSourceAnchor(new ChopboxAnchor(node1));
//		conn.setTargetAnchor(new ChopboxAnchor(node2));
//		// conn.setTargetDecoration(new PolygonDecoration());//箭头
//
//		Label label = new Label("绑定数据");
//		// label.setOpaque(true);
//		// label.setBackgroundColor(ColorConstants.buttonLightest);
//		// label.setBorder(new LineBorder());
//		conn.add(label, new MidpointLocator(conn, 0));
		
		//displayModel.addChild(node1);
		
	}

	/**
	 * @return the origin editor input before wrapped by
	 *         {@link NoResourceEditorInput}.
	 */
	public IEditorInput getOriginEditorInput() {
		IEditorInput editorInput = super.getEditorInput();
		if (editorInput instanceof NoResourceEditorInput) {
			return ((NoResourceEditorInput) editorInput).getOriginEditorInput();
		}
		return editorInput;
	}

	private InputStream getInputStream() {
		InputStream result = null;

		IEditorInput editorInput = getOriginEditorInput();

		if (editorInput instanceof FileEditorInput) {

			// try {
			// result = ((FileEditorInput) editorInput).getFile()
			// .getContents();
			// } catch (CoreException e) {
			// e.printStackTrace();
			// }
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

	public void connetDirection() {

	}

}