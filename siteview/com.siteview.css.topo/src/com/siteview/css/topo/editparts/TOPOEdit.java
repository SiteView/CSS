package com.siteview.css.topo.editparts;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;
import java.util.Map.Entry;

import org.csstudio.opibuilder.editor.OPIEditor;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.widgets.model.LabelModel;
import org.csstudio.ui.util.NoResourceEditorInput;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
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

import com.siteview.itsm.nnm.scan.core.snmp.common.ScanParam;
import com.siteview.itsm.nnm.scan.core.snmp.constants.CommonDef;
import com.siteview.itsm.nnm.scan.core.snmp.data.GlobalData;
import com.siteview.itsm.nnm.scan.core.snmp.model.Pair;
import com.siteview.itsm.nnm.scan.core.snmp.pojo.DevicePro;
import com.siteview.itsm.nnm.scan.core.snmp.pojo.Edge;
import com.siteview.itsm.nnm.scan.core.snmp.pojo.IDBody;

public class TOPOEdit extends OPIEditor {
	/**
	 * The source widget UID
	 */
	public static final String PROP_SRC_WUID = "src_wuid"; //$NON-NLS-1$

	/**
	 * The target widget UID
	 */
	public static final String PROP_TGT_WUID = "tgt_wuid"; //$NON-NLS-1$
	final String TOP_LEFT = "TOP_LEFT";
	final String TOP = "TOP";
	final String TOP_RIGHT = "TOP_RIGHT";
	final String LEFT = "LEFT";
	final String RIGHT = "RIGHT";
	final String BOTTOM_LEFT = "BOTTOM_LEFT";
	final String BOTTOM = "BOTTOM";
	final String BOTTOM_RIGHT = "BOTTOM_RIGHT";
	final private static String PVFACTORY_EXT_ID =
	        "org.csstudio.utility.pv.pvfactory";
	public static final String PROP_IMAGE_FILE = "image_file";
	public static final String PROP_AUTOSIZE = "auto_size";
	int nnodes;
	public static final String ID = "com.siteview.css.topo.editparts.TOPOEdit";
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

	DisplayModel displayModel;
	@SuppressWarnings("unused")
	protected void createGraphicalViewer(Composite parent) {
		super.createGraphicalViewer(parent);
		displayModel = getDisplayModel();
		
		IConfigurationElement[] configs = Platform.getExtensionRegistry()
		            .getConfigurationElementsFor(PVFACTORY_EXT_ID);

		// force layout
		ForceDirectedLayout forceLayout = new ForceDirectedLayout();
		forceLayout.SetLayoutReport(new ForceDirectedLayoutReport());
		forceLayout.set_PreferredLinksLength(200);
		forceLayout.set_RespectNodeSizes(true);

		TopoGraph container = new TopoGraph(displayModel);
		
		for (Entry<String, IDBody> entry : GlobalData.deviceList.entrySet()) {
			String ip = entry.getKey();// 获取设备的所有ip包括亚设备的信息
			// 判断设备类型
			if (entry.getValue().getDevType().equals(CommonDef.OTHER)) {//亚设备-
				container.addNode(new TopoNode(container, ip, ModelFactory.getWidgetModel(ModelFactory.DUMBMODEL,new Pair<String, IDBody>(entry.getKey(), entry.getValue())), entry
						.getValue()));
			}else if (entry.getValue().getDevType().equals(CommonDef.ROUTE_SWITCH)) {//三层交换-
				AbstractPVWidgetModel model = ModelFactory.getWidgetModel(ModelFactory.SWITCH_ROUTER, new Pair<String, IDBody>(entry.getKey(), entry.getValue()));
				//model.setPropertyValue(AbstractPVWidgetModel.PROP_PVNAME, "sv://topo");
				container.addNode(new TopoNode(container, ip, model, entry
						.getValue()));
			}else if(entry.getValue().getDevType().equals(CommonDef.SWITCH)){//二层交换
				AbstractPVWidgetModel model = ModelFactory.getWidgetModel(ModelFactory.SWITCH, new Pair<String, IDBody>(entry.getKey(), entry.getValue()));
				//model.setPropertyValue(AbstractPVWidgetModel.PROP_PVNAME, "sv://topo");
			}else if(entry.getValue().getDevType().equals(CommonDef.ROUTER)){//路由器-
				AbstractPVWidgetModel model = ModelFactory.getWidgetModel(ModelFactory.ROUTER, new Pair<String, IDBody>(entry.getKey(), entry.getValue()));
				//model.setPropertyValue(AbstractPVWidgetModel.PROP_PVNAME, "sv://topo");
			}else if (entry.getValue().getDevType().equals(CommonDef.PC)) {//pc终端-
				container.addNode(new TopoNode(container, ip, ModelFactory.getWidgetModel(ModelFactory.TOPOLOGYMODEL, new Pair<String, IDBody>(entry.getKey(), entry.getValue())), entry
						.getValue()));
			}
		}
		
		
		String leftIp;
		String rightIp;
		for (int i = 0; i < GlobalData.edgeList.size(); i++) {
			Edge edge = (Edge) GlobalData.edgeList.get(i);
			leftIp = edge.getIp_left();
			rightIp = edge.getIp_right();
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
//		final DumbModel dumb1 = new DumbModel();
//		dumb1.setLocation(0, 0);
//		
//		final DumbModel dumb2 = new DumbModel();
//		dumb2.setLocation(100,100);
//		
//		ConnectionModel cModel = new ConnectionModel(displayModel);
//		cModel.connect(dumb1, dumb1.getName(), dumb2, dumb2.getName());
//		PolyLineModel model = new PolyLineModel();
//		PointList points = new PointList();
//		points.addPoint(dumb1.getLocation());
//		points.addPoint(dumb2.getLocation());
//		model.setPoints(points, true);
//		model.addConnection(cModel);
//		model.setParent(displayModel);
//		AbstractWidgetProperty srcWUIDProp = new StringProperty(PROP_SRC_WUID,
//				"Source WUID", WidgetPropertyCategory.Display, "");
//		model.addProperty(srcWUIDProp);
//		srcWUIDProp.addPropertyChangeListener(new PropertyChangeListener() {			
//			@Override
//			public void propertyChange(PropertyChangeEvent evt) {
//				if(displayModel == null)
//					return;
//				String wuid = evt.getNewValue().toString();
//				String srcWuid = evt.getOldValue().toString();
//				AbstractWidgetModel w = displayModel.getWidgetFromWUID(wuid);
//				AbstractWidgetModel s = displayModel.getWidgetFromWUID(wuid);
//				PointList pl = new PointList();
//				if(w != null){
//					pl.addPoint(w.getLocation());
//					pl.addPoint(dumb2.getLocation());
//				}
//				else{
//					pl.addPoint(dumb1.getLocation());
//					pl.addPoint(dumb2.getLocation());
//				}
//			}
//		});
//		AbstractWidgetProperty tgtWUIDProp = new StringProperty(PROP_TGT_WUID,
//				"Target WUID", WidgetPropertyCategory.Display, "");
//		model.addProperty(tgtWUIDProp);
//		tgtWUIDProp.addPropertyChangeListener(new PropertyChangeListener() {			
//			@Override
//			public void propertyChange(PropertyChangeEvent evt) {
//				if(displayModel == null)
//					return;
//				String wuid = evt.getNewValue().toString();
//				AbstractWidgetModel w = displayModel.getWidgetFromWUID(wuid);
//				String tarWuid = evt.getOldValue().toString();
//				AbstractWidgetModel t = displayModel.getWidgetFromWUID(tarWuid);
//				PointList pl = new PointList();
//				if(w != null){
//					pl.addPoint(w.getLocation());
//					pl.addPoint(dumb2.getLocation());
//				}
//				else{
//					pl.addPoint(dumb1.getLocation());
//					pl.addPoint(dumb2.getLocation());
//				}
//			}
//		});		
//		displayModel.addChild(model);
//		displayModel.addChild(dumb1);
//		displayModel.addChild(dumb2);
		//保存widget
		this.doSave(null);
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

	@SuppressWarnings("unused")
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