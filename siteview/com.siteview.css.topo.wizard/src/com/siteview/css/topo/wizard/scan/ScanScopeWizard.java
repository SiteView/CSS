package com.siteview.css.topo.wizard.scan;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.*;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.*;

import com.siteview.itsm.nnm.scan.core.snmp.model.Pair;


/**
 * 扫描参数界面
 * 
 * @author zhangxinnan
 * 
 */
public class ScanScopeWizard extends WizardPage {

	private Table table;
	private TableEditor editor = null;
	private TableItem item;
	Group group = null;
	/**
	 * 扫描范围的列表
	 */
	private List<Pair<String, String>> scaleList = new ArrayList<Pair<String, String>>();
	
	public void setScaleList(List<Pair<String, String>> scaleList) {
		this.scaleList = scaleList;
	}
	
	public List<Pair<String, String>> getScaleList() {
		return scaleList;
	}

	public Table getTable(){
		return this.table;
	}
	protected ScanScopeWizard() {
		super("ScanScopeWizard");
		setTitle("扫描参数 -->" + "扫描范围 -->" + "排除范围 -->" + "共同体设置 -->" + "扫描种子");
		setMessage("用起始ip地址指定扫描范围，不设置表示全范围扫描");
	}

	public  TableItem getTableItem(){
		return item;
	}
	final String[] strArray = new String[5];
	/**
	 * 创建界面视图
	 */
	public void createControl(Composite parent) {
		// 群组
		group = new Group(parent, SWT.NONE);
		group.setText("扫描范围");
		group.setLayout(new FillLayout());
		GridData layoutData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		layoutData.verticalIndent = 15;
		group.setLayoutData(layoutData);
		// tabel设置
		table = new Table(group, SWT.BORDER | SWT.NONE | SWT.FULL_SELECTION
				| SWT.VIRTUAL);
		
		table.setHeaderVisible(true);// 标题
		table.setLinesVisible(true);// 表格线可见
		// 编辑器设置
		editor = new TableEditor(table);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		
		// 创建列
		TableColumn col1 = new TableColumn(table, SWT.LEFT);
		col1.setText("起始IP地址");
		col1.setWidth(300);
		TableColumn col2 = new TableColumn(table, SWT.LEFT);
		col2.setText("终止IP地址");
		col2.setWidth(300);
		// 添加表格数据
		if(!scaleList.isEmpty()){
			table.clearAll();
			for(Pair<String,String> p : scaleList){
				TableItem item = new TableItem(table, SWT.NONE);
				item.setText(new String[]{p.getFirst(),p.getSecond()});
				table.showItem(item);
			}
		}else{
			for(int i=0;i<1;i++){
				TableItem item = new TableItem(table, SWT.NONE);
				item.setText(new String[]{"",""});
				table.showItem(item);
			}
		}
		// 修改table
		{
			table.addMouseListener(new MouseAdapter() {
				@SuppressWarnings("unused")
				@Override
				public void mouseDoubleClick(MouseEvent e) {
					try {
						table.getSelection();
						// 清空编辑器
						Control c = editor.getEditor();
						if (c != null) {
							c.dispose();
						}
						// 得到选中的行
						Point point = new Point(e.x, e.y);
						table.getSelectionIndex();
						
						final TableItem tableitem = table.getItem(point);
						TableItem ti = table.getItem(table.getTopIndex());
						// // 得到选中的列
						int column = -1;
						for (int i = 0; i < table.getColumnCount(); i++) {
							Rectangle rec = tableitem.getBounds(i);
							if (rec.contains(point)) {
								column = i;
							} else {// 如果点击的是不存在的行。那么新增一行
								new TableItem(table, SWT.NONE);
							}
						}

						// System.out.println("表格数" + table.getItems().length);
						final int col1 = column;

						// 其他的修改都是用文本框
						final Text txt = new Text(table, SWT.NONE);
						txt.setText(tableitem.getText(col1));// 取得当前单元格里的值
						editor.setEditor(txt, tableitem, col1);
						
						txt.addModifyListener(new ModifyListener() {
							@Override
							public void modifyText(ModifyEvent e) {
								// 如果col1==0为第一列
								tableitem.setText(col1, txt.getText());// 哪一列输入的文字
								// System.out.println(tableitem.getText(0)+"----"+tableitem.getText(1));
								for (int i = 0; i < strArray.length; i++) {
									strArray[i] = "0:"+tableitem.getText(0) + "@" + tableitem.getText(1);
								}
								ScanScopeWizard.this.getWizard()
								.getDialogSettings()
								.put("Array", strArray);
								// 可以存数组
								ScanScopeWizard.this.getWizard()
										.getDialogSettings()
										.put("起始ID", tableitem.getText(0));
								ScanScopeWizard.this.getWizard()
										.getDialogSettings()
										.put("终止ID", tableitem.getText(1));
							}

						});
						txt.selectAll();
						txt.forceFocus();

						// 在table更新时text editor消失 实现刷新效果
						tableitem
								.addDisposeListener(new org.eclipse.swt.events.DisposeListener() {
									public void widgetDisposed(
											org.eclipse.swt.events.DisposeEvent e) {
										txt.dispose();
									}
								});
						// 获取表格里面的值
						// TableItem[] tables = table.getItems();
						// for (int i = 0; i < tables.length; i++) {
						// System.out.println(tables[i].getText(0)+"-------------"+tables[i].getText(1));
						// // 把数据统一交给wizard里面的通用存储器DialogSettings来存值储
						// ScanScopeWizard.this.getWizard().getDialogSettings().put("",
						// "");
						// }
					} catch (Exception e2) {
						e2.getStackTrace();
					}
				}
			});
		}

		// 右键删除菜单
		{
			Menu menu1 = new Menu(group);
			table.setMenu(menu1);
			MenuItem menuitem1 = new MenuItem(menu1, SWT.PUSH);
			menuitem1.setText("删除");

			menuitem1.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					MessageBox mbox = new MessageBox(new Shell(),
							SWT.DIALOG_TRIM | SWT.ICON_INFORMATION);
					mbox.setText("删除成功");
					mbox.setMessage("删除了" + table.getSelectionCount() + "条记录");
					table.remove(table.getSelectionIndices());
					mbox.open();
				}
			});
		}
		setControl(group);
	}
}