package com.siteview.css.topo.editparts;

import java.awt.geom.Rectangle2D;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;
import java.util.Map.Entry;

import org.csstudio.opibuilder.editor.OPIEditor;
import org.csstudio.opibuilder.model.ConnectionModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.widgets.model.ImageModel;
import org.csstudio.ui.util.NoResourceEditorInput;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.runtime.IPath;
import org.eclipse.draw2d.graph.Node;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.FileEditorInput;

import ILOG.Diagrammer.GraphicContainer;
import ILOG.Diagrammer.GraphicObject;
import ILOG.Diagrammer.GraphLayout.ForceDirectedLayout;
import ILOG.Diagrammer.GraphLayout.ForceDirectedLayoutReport;
import ILOG.Diagrammer.GraphLayout.TreeLayout;
import ILOG.Diagrammer.GraphLayout.TreeLayoutMode;

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
		// 添加模型
		// List testList = new ArrayList();
		// TopologyModel[] model = new TopologyModel[15];
		// ConnectionModel[] cModels = new ConnectionModel[model.length];
		// for (int i = 0; i < model.length; i++) {
		// model[i] = new TopologyModel();
		// cModels[i] = new ConnectionModel(displayModel);
		// displayModel.addChild(model[i]);
		// }
		// //设置连接模型
		// String t =
		// "0-1,0-2,0-3,1-4,1-5,1-6,2-7,2-8,2-9,3-10,3-11,3-12,3-13,3-14";
		// String test[] = t.split(",");
		// for (int i = 0; i < test.length; i++) {
		// testList.add(test[i]);
		// }
		//
		// Iterator it = testList.iterator();
		// String mStr = "";
		// String spitStr[];
		// for (int i = 0; i < test.length; i++) {
		// mStr = (String) it.next();
		// spitStr = mStr.split("-");
		// cModels[i].connect(model[Integer.parseInt(spitStr[0])], BOTTOM,
		// model[Integer.parseInt(spitStr[1])], TOP);
		// }

		// Map<String, String> map = red.readNode();
		// Iterator its = map.keySet().iterator();
		// while (its.hasNext()) {
		// String key = (String) its.next();
		// String value = map.get(key);
		// String[] values = value.split("-");
		//
		// model[Integer.parseInt(key)].setX(Integer.parseInt(values[0]));
		// model[Integer.parseInt(key)].setY(Integer.parseInt(values[1]));
		// }

		// tree layout
		// TreeLayout treeLayout = new TreeLayout();
		// treeLayout.set_LayoutMode(TreeLayoutMode.Radial);
		// treeLayout.set_AspectRatio(1);
		// treeLayout.set_FirstCircleEvenlySpacing(true);

		// force layout
		ForceDirectedLayout forceLayout = new ForceDirectedLayout();
		forceLayout.SetLayoutReport(new ForceDirectedLayoutReport());
		forceLayout.set_PreferredLinksLength(3);
		forceLayout.set_RespectNodeSizes(true);

		TopoGraph container = new TopoGraph(displayModel);

		for (Entry<String, IDBody> entry : TopoData.deviceList.entrySet()) {
			String ip = entry.getKey();
			// entry.getValue();
			container.addNode(new TopoNode(container, ip, new TopologyModel(),
					entry.getValue()));
		}

		String leftIp;
		String rightIp;

		for (int i = 0; i < TopoData.edgeList.size(); i++) {
			Edge edge = (Edge) TopoData.edgeList.get(i);
			leftIp = edge.getIp_left();
			rightIp = edge.getIp_right();
			if (leftIp.startsWith("DUMP")) {
				IDBody td = new IDBody();
				td.setDevType("6");
				td.getIps().add(leftIp);
				container.addNode(new TopoNode(container, leftIp,
						new TopologyModel(), td));
			}
			if (rightIp.startsWith("DUMP")) {
				IDBody td = new IDBody();
				td.setDevType("DUMP");
				td.getIps().add(rightIp);
				container.addNode(new TopoNode(container, rightIp,
						new TopologyModel(), td));
			}

		}

		for (int i = 0; i < TopoData.edgeList.size(); i++) {
			Edge edge = (Edge) TopoData.edgeList.get(i);
			leftIp = edge.getIp_left();
			rightIp = edge.getIp_right();
			//System.out.println(leftIp + "<-->" + rightIp);
			container.addLink(leftIp, rightIp);
		}

		forceLayout.Attach(container);

		forceLayout.Layout();

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
	private void buildLevelTree(Node v, int x) {
		if (v.getOffsetIncoming() != 0) {

		}
	}
}