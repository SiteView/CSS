package com.xinnan.patientims.dialogs;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.*;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

/**
 * 界面2
 * 
 * @author zhangxinnan
 * 
 */
public class ScanScopeWizard extends WizardPage {

	private Table table;
	private TableEditor editor = null;

	protected ScanScopeWizard() {
		super("Some wizard Page");
		setTitle("扫描参数 -->" + "扫描范围 -->" + "排除范围 -->" + "共同体设置 -->" + "扫描种子");
		setMessage("用起始ip地址指定扫描范围，不设置表示全范围扫描");
	}

	public void createControl(Composite parent) {
		// 设置颜色
		// display = parent.getDisplay();
		// COLOR_SYSTEM_RED = display.getSystemColor(SWT.COLOR_RED);
		// 群组
		Group group = new Group(parent, SWT.NONE);
		group.setText("扫描范围");
		group.setLayout(new FillLayout());
		GridData layoutData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		layoutData.verticalIndent = 15;
		group.setLayoutData(layoutData);
		// tabel设置
		table = new Table(group, SWT.BORDER | SWT.NONE | SWT.FULL_SELECTION
				| SWT.HIDE_SELECTION | SWT.VIRTUAL);
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
		final TableColumn[] columns = table.getColumns();
		for (int i = 0; i < columns.length; i++) {
			TableItem item = new TableItem(table, SWT.NONE);
			for (int j = 0; j < columns.length; j++) {
				// item.setText(j,""+i);//设置默认值
			}
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
							} else {// 如果点击的是不存在的行。那么新增一行
								TableItem item = new TableItem(table, SWT.NONE);
								TableItem[] items = table.getItems();
							}
						}
						final int col1 = column;
						System.out.println(col1);

						// 其他的修改都是用文本框
						final Text txt = new Text(table, SWT.NONE);
						txt.setText(tableitem.getText(col1));// 取得当前单元格里的值
						txt.forceFocus();
						editor.setEditor(txt, tableitem, col1);
						txt.addModifyListener(new ModifyListener() {
							@Override
							public void modifyText(ModifyEvent e) {
								// Text text = (Text)editor.getEditor();
								System.out.println(txt.getText());
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