package com.siteview.css.topo.editparts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

import org.csstudio.opibuilder.editor.OPIEditor;
import org.csstudio.opibuilder.model.ConnectionModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.properties.FilePathProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.widgets.model.ImageModel;
import org.csstudio.ui.util.NoResourceEditorInput;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.draw2d.graph.Node;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.FileEditorInput;

import com.siteview.css.topo.models.TopologyModel;
import com.siteview.snmp.common.ScanParam;
import com.siteview.snmp.pojo.DevicePro;

public class TOPOEdit extends OPIEditor {
	/** ģ�ͽڵ� */
	TopologyModel[] model = new TopologyModel[13];
	/** ����ģ�� */
	ConnectionModel[] cModels = new ConnectionModel[model.length];
	/** �������� */
	List testList = new ArrayList();
	public static final String PROP_IMAGE_FILE = "image_file";
	private static final String[] FILE_EXTENSIONS = new String[] { "jpg",
			"jpeg", "gif", "bmp", "png" };
	int nnodes;
	// List list = new A
	private StringBuffer strContext = new StringBuffer("");
	public static final String ID = "com.siteview.css.topo.editparts.TOPOEdit";
	static Random rand = new Random(90);

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

	/*
	 * //���ҽڵ� int findNode(String lbl) {//x-a,x-b,a-c,a-f,b-d,b-e
	 * System.out.println("��ʼֵ="+nnodes+"   lbl="+lbl); for (int i = 0 ; i <
	 * nnodes ; i++) {
	 * System.out.println("���ڻ�õ�lbl�ǣ�"+model[i].getIndex()+"   i="+i); if
	 * ((model[i].getIndex()+"").equals(lbl)) { System.out.println("���ص�iΪ:"+i);
	 * return i; } } return addNode(lbl); } //���ӽڵ� ���ýڵ����� int x=300; int y=0;
	 * int addNode(String lbl) {
	 * 
	 * model[nnodes].setX(x); //(int) (10 + 380*Math.random())
	 * model[nnodes].setY(y); y=y+30; x=x+20; return nnodes++; }
	 * 
	 * //���ӱ��� 0-1,0-2,0-3,1-4,1-5,1-6,2-7,2-8,2-9,3-10,3-11,3-12 void
	 * addEdge(String from, String to) {//x-a,x-b,a-c,a-f,b-d,b-e
	 * System.out.println
	 * ("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~from="+from+" to="+to);
	 * System.out.println("=================from============="); findNode(from);
	 * System.out.println("=================to==============="); findNode(to); }
	 */

	protected void createGraphicalViewer(Composite parent) {
		super.createGraphicalViewer(parent);
		DisplayModel displayModel = getDisplayModel();

		int oneModel = 1;
		int oneConnectModel = 1;
		for (int i = 0; i < model.length; i++) {
			// ���� i��ģ��
			model[i] = new TopologyModel();
			// ����i��������ģ��
			cModels[i] = new ConnectionModel(displayModel);
			// ����ģ������
			getDisplayModel().addChild(model[i]);
		}
		TopoParsing topParsing = new TopoParsing();
		Map<String, String> map = topParsing.readFileByChars();
		Iterator its = map.keySet().iterator();
		while (its.hasNext()) {
			String key = (String) its.next();
			String value = map.get(key);
			String[] values = value.split("-");
			String value1[] = values[0].split("[.]");
			String value2[] = values[1].split("[.]");
			model[Integer.parseInt(key)].setX(Integer.parseInt(value1[0]));
			model[Integer.parseInt(key)].setY(Integer.parseInt(value2[0]));
			// IPath path = model[0].getFilename();
			if (Integer.parseInt(key) == 0) {
				model[0].addProperty(new FilePathProperty(PROP_IMAGE_FILE,
						"Image File", WidgetPropertyCategory.Basic, new Path(
								"bmp_PC_Blue.bmp"), FILE_EXTENSIONS));
			} else if (Integer.parseInt(key) == 1 || Integer.parseInt(key) == 2
					|| Integer.parseInt(key) == 3 || Integer.parseInt(key) == 4) {
				model[1].addProperty(new FilePathProperty(PROP_IMAGE_FILE,
						"Image File", WidgetPropertyCategory.Basic, new Path(
								"bmp_PC_Gray.bmp"), FILE_EXTENSIONS));
				model[2].addProperty(new FilePathProperty(PROP_IMAGE_FILE,
						"Image File", WidgetPropertyCategory.Basic, new Path(
								"bmp_PC_Gray.bmp"), FILE_EXTENSIONS));
				model[3].addProperty(new FilePathProperty(PROP_IMAGE_FILE,
						"Image File", WidgetPropertyCategory.Basic, new Path(
								"bmp_PC_Gray.bmp"), FILE_EXTENSIONS));
			}
			// model[Integer.parseInt(key)].addProperty(n);
			// System.out.println("key="+key+"-----value="+value);
		}

		// /**
		// * 1.��Բ
		// */
		// int centerX = 400;//Բ������
		// int centerY = 300;
		// int radius = 200;//�뾶
		// //count: �ڵ���Ŀ
		// int count =13;
		// for (int i= 0; i<count; i++)
		// {
		// int x = centerX+ (int)(radius * Math.cos(Math.PI * 2 / count * i));
		// int y = centerY+ (int)(radius * Math.sin(Math.PI * 2 / count * i));
		// model[i].setX(x);
		// model[i].setY(y);
		// }

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
			cModels[i].connect(model[Integer.parseInt(spitStr[0])], "BOTTOM",
					model[Integer.parseInt(spitStr[1])], "TOP");
			// addEdge(spitStr[0],spitStr[1]);//����ģ������
		}
		load();
	}

	/**
	 * Task:step 1.����gml�ļ� 2.����yEd API ������Ҫ�Ĳ��ַ�ʽ 3.��gmlת��ΪOPI��ʽ
	 */
	private void load() {
		// TODO Auto-generated method stub
		System.out.println("����");
		DrawHead();
		QueryBusinessObjectDef();
		DrawOver();
	}

	/**
	 * ��ѯ����ҵ����� 1.��ѯ����ģ�͡� 2.��ȡ�ڵ���ߵ���Ϣ
	 */
	private void QueryBusinessObjectDef() {
		// ���ڵ�
		for (int i = 0; i < model.length; i++) {
			Drawnode(482, 84, 122, 122, model[i].getWUID(), model[i].getName(),
					"rectangle", "#FFCC00", "#000000");
		}
		// ����
		for (int j = 0; j < cModels.length - 1; j++) {
			Drawedge(cModels[j].getSource().getWUID(), cModels[j].getTarget()
					.getWUID(), "��ע��Ϣ", "#000000", "standard");
		}
	}

	/**
	 * ���ɽ���
	 */
	private void DrawOver() {
		strContext.append("] \n");
		// ����һ��Ĭ���ļ���·��
		String path = "C:\\����ͼ\\";
		File fileUpdate = new File(path);
		if (fileUpdate.exists() == false) {
			fileUpdate.mkdirs();
		} else {
			System.out.println("�ļ�·������:" + path);
		}
		// ����gml�ļ�
		File f = new File(path + "css.gml");
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
	 * ����ͷ��
	 */
	private void DrawHead() {
		strContext.append("Creator	\"yFiles\" ");
		strContext.append("\nVersion	\"2.8\" ");
		strContext.append("\ngraph ");
		strContext.append("\n[\n\thierarchic	1");
		strContext.append("\n\tlabel	\"\"");
		strContext.append("\n\tdirected	1 \n");
	}

	/**
	 * Task:���ɽڵ�
	 * 
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

	/**
	 * Task:���ɱ�
	 * 
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
	 * �ж� ���� �ַ����ȴ�С
	 */
	public int DrawWidth(String name, int number) {
		int width = name.length() * number;
		if (name.charAt(0) > 0 && name.charAt(0) < 127) {
			width = name.length() * number / 2;
		}
		return width;
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

	// ========================
	private void buildLevelTree(Node v, int x) {
		if (v.getOffsetIncoming() != 0) {

		}
	}
}