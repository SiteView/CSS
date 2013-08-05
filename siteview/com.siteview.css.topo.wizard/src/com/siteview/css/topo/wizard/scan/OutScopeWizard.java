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
 * �ų���Χ��
 * 
 * @author zhangxinnan
 * 
 */
public class OutScopeWizard extends WizardPage {

	private Table table;
	private TableEditor editor = null;
	private List<Pair<String,String>> filterList = new ArrayList<Pair<String,String>>();
	
	public void setFilterList(List<Pair<String, String>> filterList) {
		this.filterList = filterList;
	}
	public Table getTable(){
		return this.table;
	}
	protected OutScopeWizard() {
		super("Some wizard Page");
		setTitle("ɨ����� -->" + "ɨ�跶Χ -->" + "�ų���Χ -->" + "��ͬ������ -->" + "ɨ������");
		setMessage("����ʼIP��ַ����ֹIP��ַָ��ɨ���ų��ķ�Χ���Թ����豸�ٶ������ַ��������Σ���ʡɨ��ʱ�䡣");
	}

	@SuppressWarnings("unused")
	public void createControl(Composite parent) {
		// Ⱥ��
		Group group = new Group(parent, SWT.NONE);
		group.setText("�ų���Χ");
		group.setLayout(new FillLayout());
		GridData layoutData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		layoutData.verticalIndent = 15;
		group.setLayoutData(layoutData);
		// tabel����
		table = new Table(group, SWT.BORDER | SWT.NONE | SWT.FULL_SELECTION
				| SWT.HIDE_SELECTION | SWT.VIRTUAL);
		table.setHeaderVisible(true);// ����
		table.setLinesVisible(true);// ����߿ɼ�
		// �༭������
		editor = new TableEditor(table);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		// ������
		TableColumn col1 = new TableColumn(table, SWT.LEFT);
		col1.setText("��ʼIP��ַ");
		col1.setWidth(300);
		TableColumn col2 = new TableColumn(table, SWT.LEFT);
		col2.setText("��ֹIP��ַ");
		col2.setWidth(300);

		// ��ӱ������
		if(!filterList.isEmpty()){
			table.clearAll();
			for(Pair<String, String> p : filterList){
				TableItem item = new TableItem(table, SWT.NONE);
				item.setText(new String[]{p.getFirst(),p.getSecond()});
				table.showItem(item);
			}
		}else{
			TableItem item = new TableItem(table, SWT.NONE);
		}
		

		// �޸�table
		{
			table.addMouseListener(new MouseAdapter() {
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
						for (int i = 0; i < table.getColumnCount(); i++) {
							Rectangle rec = tableitem.getBounds(i);
							if (rec.contains(point)) {
								column = i;
							} else {// ���������ǲ����ڵ��С���ô����һ��
								TableItem item = new TableItem(table, SWT.NONE);
								TableItem[] items = table.getItems();
							}
						}
						final int col1 = column;
						System.out.println(col1);

						// �������޸Ķ������ı���
						final Text txt = new Text(table, SWT.NONE);
						txt.setText(tableitem.getText(col1));// ȡ�õ�ǰ��Ԫ�����ֵ
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
					}
					;
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