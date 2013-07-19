package com.siteview.css.topo.editparts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.siteview.css.topo.models.TopologyModel;

public class ReadAndCreate {
	/** ���궨λ */
	final String TOP_LEFT = "TOP_LEFT";
	final String TOP = "TOP";
	final String TOP_RIGHT = "TOP_RIGHT";
	final String LEFT = "LEFT";
	final String RIGHT = "RIGHT";
	final String BOTTOM_LEFT = "BOTTOM_LEFT";
	final String BOTTOM = "BOTTOM";
	final String BOTTOM_RIGHT = "BOTTOM_RIGHT";
	
	private final String filename = "C:\\����ͼ\\css3.gml";

	/**����OPI*/
	private StringBuffer strContext = new StringBuffer("");
	/**����gml*/
	private StringBuffer strGml = new StringBuffer("");

	/**
	 * ��ȡ�ڵ�
	 */
	public Map<String, String> readNode() {
		Map<String, String> map = new HashMap<String, String>();
		Reader reader = null;
		try {
			// һ�ζ�һ���ַ�
			reader = new InputStreamReader(new FileInputStream(filename));
			int tempchar;
			StringBuffer d = new StringBuffer("");
			while ((tempchar = reader.read()) != -1) {
				if (((char) tempchar) != '\r' && ((char) tempchar) != '\t'
						&& ((char) tempchar) != '\n') {
					d.append((char) tempchar);
				}
			}
			reader.close();

			Pattern pattern = Pattern.compile("node\\[.*\\]");
			Matcher matcher = pattern.matcher(d.toString().replaceAll(" ", ""));
			if (matcher.find()) {
				// Ҫ���1.���2.�յ�3.�ĸ�label4.�Լ���ע��Ϣ
				String[] spitNode = matcher.group(0).split("node");
				if (spitNode != null) {
					for (String str : spitNode) {
						if (str.indexOf("id") == -1)
							continue;
						String key = str.substring(str.indexOf("id") + 2,
								str.indexOf("label"));
						String x1 = str.substring(
								(str.indexOf("graphics") + 9),
								str.indexOf("type"));
						int x = (int) Float.parseFloat(x1.substring(
								x1.indexOf("x") + 1, x1.indexOf("y")));
						int y = (int) Float.parseFloat(x1.substring(
								x1.indexOf("y") + 1, x1.indexOf("w")));
						map.put(key, x + "-" + y);
					}
				}
			}
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * ��ȡ��
	 * 
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List readEdge() {
		List list = new ArrayList();
		Reader reader = null;
		try {
			// һ�ζ�һ���ַ�
			reader = new InputStreamReader(new FileInputStream(filename));
			int tempchar;
			StringBuffer d = new StringBuffer("");
			while ((tempchar = reader.read()) != -1) {
				if (((char) tempchar) != '\r' && ((char) tempchar) != '\t'
						&& ((char) tempchar) != '\n') {
					d.append((char) tempchar);
				}
			}
			reader.close();

			Pattern pattern = Pattern.compile("node\\[.*\\]");
			Matcher matcher = pattern.matcher(d.toString().replaceAll(" ", ""));
			if (matcher.find()) {
				// Ҫ���1.���2.�յ�3.�ĸ�label4.�Լ���ע��Ϣ
				String[] spitEdge = matcher.group(0).split("edge");
				if (spitEdge != null) {
					for (String strE : spitEdge) {
						if (strE.indexOf("source") == -1)
							continue;
						String source = strE.substring(
								strE.indexOf("source") + 6,
								strE.indexOf("target"));
						String target = strE.substring(
								strE.indexOf("target") + 6,
								strE.indexOf("label"));
						list.add(source + "-" + target);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return list;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List allInfo(){
		List li = new ArrayList();
		Map map = readNode();
		List list = readEdge();
		Iterator iterator = list.iterator();
		while (iterator.hasNext()) {
			String s = (String) iterator.next();
			String str[]= s.split("-");
			li.add(str[0]+":"+map.get(str[0])+","+str[1]+":"+map.get(str[1]));
		}
		Iterator iterator2 = li.iterator();
		while (iterator2.hasNext()) {
			System.out.println(iterator2.next());
		}
		return li;
	}
	
	
	/**
	 * ����OPIͷ��
	 */
	public void CreateHead() {
		strContext
				.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><display typeId=\"org.csstudio.opibuilder.Display\" version=\"1.0.0\">");
		strContext
				.append("\n<auto_zoom_to_fit_all>false</auto_zoom_to_fit_all>");
		strContext.append("\n<macros>");
		strContext
				.append("\n<include_parent_macros>true</include_parent_macros>");
		strContext.append("\n</macros>");
		strContext.append("\n<wuid>21c56654:13fc1641c9e:-7fff</wuid>");
		strContext.append("\n<boy_version>3.2.0.qualifier</boy_version>");
		strContext.append("\n<scripts />");
		strContext.append("\n<show_ruler>true</show_ruler>");
		strContext.append("\n<height>600</height>");
		strContext.append("\n<name></name>");
		strContext.append("\n<snap_to_geometry>true</snap_to_geometry>");
		strContext.append("\n<show_grid>true</show_grid>");
		strContext.append("\n<background_color>");
		strContext.append("\n<color red=\"240\" green=\"240\" blue=\"240\" />");
		strContext.append("\n</background_color>");
		strContext.append("\n<foreground_color>");
		strContext.append("\n<color red=\"192\" green=\"192\" blue=\"192\" />");
		strContext.append("\n</foreground_color>");
		strContext.append("\n<widget_type>Display</widget_type>");
		strContext.append("\n<show_close_button>true</show_close_button>");
		strContext.append("\n<width>800</width>");
		strContext.append("\n<rules />");
		strContext.append("\n<show_edit_range>true</show_edit_range>");
		strContext.append("\n<grid_space>6</grid_space>");
		strContext.append("\n<auto_scale_widgets>");
		strContext.append("\n<auto_scale_widgets>false</auto_scale_widgets>");
		strContext.append("\n<min_width>-1</min_width>");
		strContext.append("\n<min_height>-1</min_height>");
		strContext.append("\n</auto_scale_widgets>");
		strContext.append("\n<actions hook=\"false\" hook_all=\"false\" />");
		strContext.append("\n<y>-1</y>");
		strContext.append("\n<x>-1</x>");
	}

	/**
	 * ����OPI�ڵ�
	 * 
	 * @param wuid
	 * @param x
	 * @param y
	 */
	public void CreateNode(String wuid, int x, int y) {
		strContext
				.append("\n<widget typeId=\"org.csstudio.opibuilder.widgets.Image\" version=\"1.0.0\">");
		strContext.append("\n<crop_right>0</crop_right>");
		strContext.append("\n<crop_left>0</crop_left>");
		strContext.append("\n<visible>true</visible>");
		strContext.append("\n<wuid>" + wuid + "</wuid>");
		strContext.append("\n<auto_size>true</auto_size>");
		strContext.append("\n<scripts />");
		strContext.append("\n<height>33</height>");
		strContext.append("\n<name>Image</name>");
		strContext.append("\n<stretch_to_fit>false</stretch_to_fit>");
		strContext.append("\n<scale_options>");
		strContext.append("\n<width_scalable>true</width_scalable>");
		strContext.append("\n<height_scalable>true</height_scalable>");
		strContext.append("\n<keep_wh_ratio>false</keep_wh_ratio>");
		strContext.append("\n</scale_options>");
		strContext.append("\n<background_color>");
		strContext.append("\n<color red=\"240\" green=\"240\" blue=\"240\" />");
		strContext.append("\n</background_color>");
		strContext.append("\n<foreground_color>");
		strContext.append("\n<color red=\"192\" green=\"192\" blue=\"192\" />");
		strContext.append("\n</foreground_color>");
		strContext.append("\n<widget_type>Image</widget_type>");
		strContext.append("\n<enabled>true</enabled>");
		strContext.append("\n<font>");
		strContext
				.append("\n<opifont.name fontName=\"Microsoft YaHei UI\" height=\"9\" style=\"0\">Default</opifont.name>");
		strContext.append("\n</font>");
		strContext.append("\n<width>35</width>");
		strContext.append("\n<no_animation>false</no_animation>");
		strContext.append("\n<flip_horizontal>false</flip_horizontal>");
		strContext.append("\n<permutation_matrix>");
		strContext.append("\n<row>");
		strContext.append("\n<col>1.0</col>");
		strContext.append("\n<col>0.0</col>");
		strContext.append("\n</row>");
		strContext.append("\n<row>");
		strContext.append("\n<col>0.0</col>");
		strContext.append("\n<col>1.0</col>");
		strContext.append("\n</row>");
		strContext.append("\n</permutation_matrix>");
		strContext.append("\n<crop_top>0</crop_top>");
		strContext.append("\n<border_style>0</border_style>");
		strContext.append("\n<rules />");
		strContext.append("\n<flip_vertical>false</flip_vertical>");
		strContext.append("\n<crop_bottom>0</crop_bottom>");
		strContext.append("\n<degree>0</degree>");
		strContext.append("\n<border_width>1</border_width>");
		strContext.append("\n<image_file>bmp_PC_Gray.bmp</image_file>");
		strContext.append("\n<border_color>");
		strContext.append("\n<color red=\"0\" green=\"128\" blue=\"255\" />");
		strContext.append("\n</border_color>");
		strContext.append("\n<actions hook=\"false\" hook_all=\"false\" />");
		strContext.append("\n<y>" + y + "</y>");
		strContext.append("\n<tooltip></tooltip>");
		strContext.append("\n<x>" + x + "</x>");
		strContext.append("\n</widget>");
	}

	/**
	 * ����OPI���ӱ�
	 * @param src
	 * @param tgt
	 */
	public void CreateEdge(String src, String tgt,String src_term,String tgt_term) {
		strContext
				.append("\n<connection typeId=\"org.csstudio.opibuilder.connection\" version=\"1.0.0\">");
		strContext.append("\n<fill_arrow>true</fill_arrow>");
		strContext.append("\n<widget_type>connection</widget_type>");
		strContext.append("\n<src_term>"+src_term+"</src_term>");
		strContext.append("\n<arrow_length>15</arrow_length>");
		strContext.append("\n<line_style>0</line_style>");
		strContext.append("\n<line_color>");
		strContext.append("\n<color red=\"0\" green=\"0\" blue=\"0\" />");
		strContext.append("\n</line_color>");
		strContext.append("\n<wuid>6e843118:13fbda91f83:-7fe7</wuid>");
		strContext.append("\n<tgt_term>"+tgt_term+"</tgt_term>");
		strContext.append("\n<arrows>0</arrows>");
		strContext.append("\n<anti_alias>true</anti_alias>");
		strContext.append("\n<router>1</router>");
		strContext.append("\n<name>connection</name>");
		strContext.append("\n<line_width>1</line_width>");
		strContext.append("\n<src_wuid>" + src + "</src_wuid>");
		strContext.append("\n<points />");
		strContext.append("\n<tgt_wuid>" + tgt + "</tgt_wuid>");
		strContext.append("\n</connection>");
	}

	/**
	 * ����OPI����
	 */
	public void CreateOver() {
		strContext.append("\n</display>");
		SaveFile(strContext,"css.opi");
	}

	/**
	 * ����opi
	 */
	public void load() {
		CreateHead();
		CreateNodeAndEdge();
		CreateOver();
	}

	/**
	 * ����OPI�ڵ�ͱ�
	 */
	final int modeWidth = 2 * TopologyModel.WIDTH;
	private String s2[];
	private String s4[];
	private int x;
	private int y;
	private int x1;
	private int y1;
	@SuppressWarnings("rawtypes")
	public void CreateNodeAndEdge() {
		Map map = readNode();
		Iterator iterator = map.keySet().iterator();
		while (iterator.hasNext()) {// ������
			String key = (String) iterator.next();
			String value = (String) map.get(key);
			String spit[] = value.split("-");
			// System.out.println(key + value);
			CreateNode(key, Integer.parseInt(spit[0]),
					Integer.parseInt(spit[1]));
		}
		String xy = "";
		String x1y1 = "";
		List list =allInfo();
		Iterator info = list.iterator();

		for (int i = 0; i < list.size(); i++) {
			String s  = (String) info.next();
			String str[] = s.split(",");
			s2 = str[0].split(":");
			s4 = str[1].split(":");
			String s3[] = s2[1].split("-");
			String s5[] = s4[1].split("-");
			x = Integer.parseInt(s3[0]);
			y =Integer.parseInt(s3[1]);
			x1=Integer.parseInt(s5[0]);
			y1=Integer.parseInt(s5[1]);
			
			// �ж����·�
			if (x - x1 <= modeWidth
					&& x - x1 >= -modeWidth) {
				if (y - y1 > 0) {
					xy = TOP;
					x1y1 = BOTTOM;
				}
				if (y - y1 < 0) {
					xy = BOTTOM;
					x1y1 = TOP;
				}
			}
			// �ж����ҷ�
			if (y - y1 <= modeWidth
					&& y - y1 >= -modeWidth) {
				if (x - x1 > 0) {
					xy = LEFT;
					x1y1 = RIGHT;
				}
				if (x - x1 < 0) {
					xy = RIGHT;
					x1y1 = LEFT;
				}
			}
			if (x - x1 > modeWidth
					&& y - y1 > modeWidth) {
				xy = TOP_LEFT;
				x1y1 = BOTTOM_RIGHT;
			}
			if (x - x1 > modeWidth
					&& y - y1 < -modeWidth) {
				xy = BOTTOM_LEFT;
				x1y1 = TOP_RIGHT;
			}
			if (x - x1 < -modeWidth
					&& y - y1 > modeWidth) {
				xy = TOP_RIGHT;
				x1y1 = BOTTOM_LEFT;
			}
			if (x - x1 < -modeWidth
					&& y - y1 < -modeWidth) {
				xy = BOTTOM_RIGHT;
				x1y1 = TOP_LEFT;
			}
			CreateEdge(s2[0], s4[0],xy, x1y1);
		}
	}
	
	
	/**
	 * ����gmlͷ��
	 */
	public void DrawHead() {
		strGml.append("Creator	\"yFiles\" ");
		strGml.append("\nVersion	\"2.8\" ");
		strGml.append("\ngraph ");
		strGml.append("\n[\n\thierarchic	1");
		strGml.append("\n\tlabel	\"\"");
		strGml.append("\n\tdirected	1 \n");
	}
	
	/**
	 * Task:����gml�ڵ�
	 * 
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param key
	 * @param key2
	 * @param type
	 * @param color
	 * @param outline
	 */
	public void Drawnode(int x, int y, int w, int h, String key, String key2,
			String type, String color, String outline) {
		strGml.append("\t node\n \t[\n");
		strGml.append("\t\t id \"" + key + "\"\n");
		strGml.append("\t\t label \"" + key2 + "\"\n");
		strGml.append("\t\t graphics\n \t\t[ \n");
		strGml.append("\t\t\t x " + x + "\n");
		strGml.append("\t\t\t y " + y + "\n");
		strGml.append("\t\t\t w " + w + "\n");
		strGml.append("\t\t\t h " + h + "\n");
		strGml.append("\t\t\t type \"" + type + "\"\n");
		strGml.append("\t\t\t fill \"" + color + "\"\n");
		strGml.append("\t\t\t outline \"" + outline + "\"\n");
		strGml.append("\t\t ] \n");
		strGml.append("\t\t LabelGraphics\n \t\t [ \n");
		strGml.append("\t\t\t text \"" + key2 + "\"\n");//��ʶ·��ip
		strGml.append("\t\t\t fontSize 12\n");
		strGml.append("\t\t\t fontName \"Dialog\"\n");
		strGml.append("\t\t\t anchor \"c\"\n");//mac��ַ
		strGml.append("\t\t ] \n");
		strGml.append("\t ] \n");
	}
	
	/**
	 * Task:����gml��
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
		strGml.append("\t edge\n \t[\n");
		strGml.append("\t\t source \"" + source + "\"\n");
		strGml.append("\t\t target \"" + target + "\"\n");
		strGml.append("\t\t label \"" + name + "\"\n");
		strGml.append("\t\t graphics\n \t\t[ \n");
		strGml.append("\t\t\t fill \"" + color + "\"\n");
		strGml.append("\t\t\t targetArrow \"" + targetArrow + "\"\n");
		strGml.append("\t\t ] \n");
		strGml.append("\t\t LabelGraphics\n \t\t [ \n");
		strGml.append("\t\t\t text \"" + name + "\"\n");
		strGml.append("\t\t\t fontSize 12\n");
		strGml.append("\t\t\t fontName \"Dialog\"\n");
		strGml.append("\t\t\t model \"six_pos\"\n");
		strGml.append("\t\t\t position \"tail\"\n");
		strGml.append("\t\t ] \n");
		strGml.append("\t ] \n");
	}
	
	/**
	 * ����gml����
	 */
	public void DrawOver() {
		strGml.append("] \n");
		SaveFile(strGml,"css.gml");
	}
	
	/**
	 * �����ļ�
	 */
	public void SaveFile(StringBuffer sBuffer,String file){
		// ����һ��Ĭ���ļ���·��
		String path = "C:\\����ͼ\\";
		File fileUpdate = new File(path);
		if (fileUpdate.exists() == false) {
			fileUpdate.mkdirs();
		} else {
			System.out.println("�ļ�·������:" + path);
		}
		// ����gml�ļ�
		File f = new File(path + file);
		OutputStream out = null;
		try {
			out = new FileOutputStream(f);
			byte b[] = sBuffer.toString().getBytes();
			for (int i = 0; i < b.length; i++) {
				out.write(b[i]);
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
