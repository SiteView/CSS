package com.siteview.css.topo.editparts;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;
import java.util.Map.Entry;

import org.csstudio.opibuilder.editor.OPIEditor;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.properties.FilePathProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.widgets.model.ImageModel;
import org.csstudio.ui.util.NoResourceEditorInput;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.FileEditorInput;

import system.ComponentModel.ISite;

import ILOG.Diagrammer.GraphicObject;
import ILOG.Diagrammer.Link;
import ILOG.Diagrammer.Rectangle2D;
import ILOG.Diagrammer.GraphLayout.CoordinatesMode;
import ILOG.Diagrammer.GraphLayout.ForceDirectedLayout;
import ILOG.Diagrammer.GraphLayout.ForceDirectedLayoutReport;
import ILOG.Diagrammer.GraphLayout.GraphLayoutRegionMode;

import com.siteview.css.topo.common.TopoData;
import com.siteview.css.topo.models.TopologyModel;
import com.siteview.snmp.common.ScanParam;
import com.siteview.snmp.pojo.DevicePro;
import com.siteview.snmp.pojo.Edge;
import com.siteview.snmp.pojo.IDBody;

public class TOPOEdit extends OPIEditor {
	/** 坐标定位 */
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
		forceLayout.set_PreferredLinksLength(150);
		forceLayout.set_RespectNodeSizes(true);

		TopoGraph container = new TopoGraph(displayModel);

		for (Entry<String, IDBody> entry : TopoData.deviceList.entrySet()) {
			String ip = entry.getKey();// 获取设备的所有ip包括亚设备的信息
			// System.out.println(ip+"```````````````````````"+entry.getValue());
			// entry.getValue();
			// 判断设备类型
			ImageModel imageModel = new ImageModel();
			imageModel.setWidth(32);
			imageModel.setHeight(32);

			if (ip.startsWith("DUMB")) {
				imageModel.addProperty(new FilePathProperty(PROP_IMAGE_FILE,
						"Image File", WidgetPropertyCategory.Basic, new Path(
								"bmp_PC_Gray.bmp"), FILE_EXTENSIONS));
				imageModel.setName(ip);
			} else {
				imageModel.addProperty(new FilePathProperty(PROP_IMAGE_FILE,
						"Image File", WidgetPropertyCategory.Basic, new Path(
								"bmp_PC_Blue.bmp"), FILE_EXTENSIONS));
				imageModel.setName(ip);

			}
			container.addNode(new TopoNode(container, ip, imageModel, entry
					.getValue()));
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
			for (GraphicObject object : afterLayoutNodes) {
				if (object.isNode()) {
					int tmpX = object.getMode().getX();
					int tmpY = object.getMode().getY();
					tmpWidth = object.getMode().getWidth();
					int tmpHeight = object.getMode().getHeight();
					// int centerX = (int)(tmpX + tmpWidth/2);
					// int centerY = (int)(tmpY + tmpHeight/2);
					// Point centerP = new Point(centerX, centerY);
					// System.out.println("模型："+object.getId()+"x = " +
					// tmpX+"-----"+"y = "+tmpY);
				}
				if (object.isLink()) {
					Link link = (Link) object;
					// System.out.println("起点："+link.getSource()+"  终点："+link.getTarget());
					link.connect(tmpWidth * 2, link.getSource(),
							link.getTarget());// 得到source，target的坐标
				}
			}
		}
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

	// ========================
	// private void buildLevelTree(Node v, int x) {
	// if (v.getOffsetIncoming() != 0) {
	//
	// }
	// }
}