package com.siteview.css.topo.wizard.scan;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.*;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.*;

/**
 * 扫描种子向导
 * 
 * @author zhangxinnan
 * 
 */
public class ScanSeedsWizard extends WizardPage {

	private Table table;
	private TableEditor editor = null;

	protected ScanSeedsWizard() {
		super("Some wizard Page");
		setTitle("扫描参数 -->" + "扫描范围 -->" + "排除范围 -->" + "共同体设置 -->" + "扫描种子");
		setMessage("扫描的种子地址一般用核心设备的地址。");
	}

	public void createControl(Composite parent) {
		// 群组
		Group group = new Group(parent, SWT.NONE);
		group.setText("扫描种子");
		group.setLayout(new FillLayout());
		GridData layoutData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		layoutData.verticalIndent = 15;
		group.setLayoutData(layoutData);
		// tabel设置
		table = new Table(group, SWT.BORDER | SWT.NONE | SWT.FULL_SELECTION
				| SWT.HIDE_SELECTION | SWT.VIRTUAL);
		table.setHeaderVisible(true);// 标题
		table.setLinesVisible(true);// 表格线可见
		table.setSize(200, 200);
		// 编辑器设置
		editor = new TableEditor(table);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		// 创建列
		TableColumn col1 = new TableColumn(table, SWT.LEFT);
		col1.setText("扫描种子");
		col1.setWidth(600);

		// 添加表格数据
		final TableColumn[] columns = table.getColumns();
		for (int i = 0; i < columns.length; i++) {
			TableItem item = new TableItem(table, SWT.NONE);
		}

		// 修改table
		{
			table.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDoubleClick(MouseEvent e) {
					try {
						// 清空编辑器
						Control c = editor.getEditor();
						if (c != null) {
							c.dispose();
						}
						// 得到选中的行
						Point point = new Point(e.x, e.y);
						final TableItem tableitem = table.getItem(point);
						// // 得到选中的列
						int column = -1;
						for (int i = 0; i < table.getColumnCount(); i++) {
							Rectangle rec = tableitem.getBounds(i);
							if (rec.contains(point)) {
								column = i;
								TableItem item = new TableItem(table, SWT.NONE);
								TableItem[] items = table.getItems();
							} else {// 如果点击的是不存在的行。那么也新增一行
								TableItem item = new TableItem(table, SWT.NONE);
								TableItem[] items = table.getItems();
							}
						}
						final int col1 = column;
						// 其他的修改都是用文本框
						final Text txt = new Text(table, SWT.NONE);
						txt.setText(tableitem.getText(col1));// 取得当前单元格里的值
						txt.forceFocus();
						editor.setEditor(txt, tableitem, col1);
						txt.addModifyListener(new ModifyListener() {
							@Override
							public void modifyText(ModifyEvent e) {
								tableitem.setText(col1, txt.getText());

							}
						});
						// 在table更新时text editor消失 实现刷新效果
						tableitem
								.addDisposeListener(new org.eclipse.swt.events.DisposeListener() {
									public void widgetDisposed(
											org.eclipse.swt.events.DisposeEvent e) {
										txt.dispose();
									}
								});
					} catch (Exception e2) {
						e2.getStackTrace();
					}
					;
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