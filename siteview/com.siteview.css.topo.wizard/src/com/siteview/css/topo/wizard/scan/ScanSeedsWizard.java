package com.siteview.css.topo.wizard.scan;

import java.util.ArrayList;
import java.util.List;

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
 * ɨ��������
 * 
 * @author zhangxinnan
 * 
 */
public class ScanSeedsWizard extends WizardPage {

	private Table table;
	private TableEditor editor = null;
	private List<String> seedList = new ArrayList<String>();
	
	public void setSeedList(List<String> seedList) {
		this.seedList = seedList;
	}
	public Table getSeedsTable(){
		return table;
	}
	protected ScanSeedsWizard() {
		super("Some wizard Page");
		setTitle("ɨ����� -->" + "ɨ�跶Χ -->" + "�ų���Χ -->" + "��ͬ������ -->" + "ɨ������");
		setMessage("ɨ������ӵ�ַһ���ú����豸�ĵ�ַ��");
	}

	public void createControl(Composite parent) {
		// Ⱥ��
		Group group = new Group(parent, SWT.NONE);
		group.setText("ɨ������");
		group.setLayout(new FillLayout());
		GridData layoutData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		layoutData.verticalIndent = 15;
		group.setLayoutData(layoutData);
		// tabel����
		table = new Table(group, SWT.BORDER | SWT.NONE | SWT.FULL_SELECTION
				| SWT.HIDE_SELECTION | SWT.VIRTUAL);
		table.setHeaderVisible(true);// ����
		table.setLinesVisible(true);// �����߿ɼ�
		table.setSize(200, 200);
		// �༭������
		editor = new TableEditor(table);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		// ������
		TableColumn col1 = new TableColumn(table, SWT.LEFT);
		col1.setText("ɨ������");
		col1.setWidth(600);

		// ���ӱ�������
		if(!seedList.isEmpty()){
			table.clearAll();
			for(String s : seedList){
				TableItem iter = new TableItem(table, SWT.NONE);
				iter.setText(s);
				table.showItem(iter);
			}
		}else{
			for(int i=0;i<1;i++){
				TableItem item = new TableItem(table, SWT.NONE);
				item.setText("");
				table.showItem(item);
			}
		}
		// �޸�table
		{
			table.addMouseListener(new MouseAdapter() {
				@SuppressWarnings("unused")
				@Override
				public void mouseDoubleClick(MouseEvent e) {
					try {
						// ��ձ༭��
						Control c = editor.getEditor();
						if (c != null) {
							c.dispose();
						}
						// �õ�ѡ�е���
						Point point = new Point(e.x, e.y);
						final TableItem tableitem = table.getItem(point);
						// // �õ�ѡ�е���
						int column = -1;
						TableItem tItems = table.getItem(table.getSelectionIndex());
						
						for (int i = 0; i < table.getColumnCount(); i++) {
							Rectangle rec = tableitem.getBounds(i);
							if (rec.contains(point)) {
								column = i;
								TableItem item = new TableItem(table, SWT.NONE);
								TableItem[] items = table.getItems();
							} else {// ���������ǲ����ڵ��С���ôҲ����һ��
								TableItem item = new TableItem(table, SWT.NONE);
								TableItem[] items = table.getItems();
							}
						}
						final int col1 = column;
						// �������޸Ķ������ı���
						final Text txt = new Text(table, SWT.NONE);
						txt.setText(tItems.getText());// ȡ�õ�ǰ��Ԫ�����ֵ
						txt.forceFocus();
						editor.setEditor(txt, tItems, col1);
						txt.addModifyListener(new ModifyListener() {
							@Override
							public void modifyText(ModifyEvent e) {
								tableitem.setText(col1, txt.getText());

							}
						});
						// ��table����ʱtext editor��ʧ ʵ��ˢ��Ч��
						tableitem
								.addDisposeListener(new org.eclipse.swt.events.DisposeListener() {
									public void widgetDisposed(
											org.eclipse.swt.events.DisposeEvent e) {
										txt.dispose();
									}
								});
					} catch (Exception e2) {
						e2.getStackTrace();
						System.err.println(e2.getMessage());
					};
				}
			});
		}

		// �Ҽ�ɾ���˵�
		{
			Menu menu1 = new Menu(group);
			table.setMenu(menu1);
			MenuItem menuitem1 = new MenuItem(menu1, SWT.PUSH);
			menuitem1.setText("ɾ��");
			menuitem1.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					MessageBox mbox = new MessageBox(new Shell(),
							SWT.DIALOG_TRIM | SWT.ICON_INFORMATION);
					mbox.setText("ɾ���ɹ�");
					mbox.setMessage("ɾ����" + table.getSelectionCount() + "����¼");
					table.remove(table.getSelectionIndices());
					mbox.open();
				}
			});
		}
		setControl(group);
	}
}