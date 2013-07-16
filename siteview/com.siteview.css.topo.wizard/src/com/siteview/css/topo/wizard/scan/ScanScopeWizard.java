package com.siteview.css.topo.wizard.scan;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
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

import com.siteview.snmp.model.Pair;

/**
 * ɨ���������
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
	 * ɨ�跶Χ���б�
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
		setTitle("ɨ����� -->" + "ɨ�跶Χ -->" + "�ų���Χ -->" + "��ͬ������ -->" + "ɨ������");
		setMessage("����ʼip��ַָ��ɨ�跶Χ�������ñ�ʾȫ��Χɨ��");
	}

	public  TableItem getTableItem(){
		return item;
	}
	final String[] strArray = new String[5];
	/**
	 * ����������ͼ
	 */
	public void createControl(Composite parent) {
		// Ⱥ��
		group = new Group(parent, SWT.NONE);
		group.setText("ɨ�跶Χ");
		group.setLayout(new FillLayout());
		GridData layoutData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		layoutData.verticalIndent = 15;
		group.setLayoutData(layoutData);
		// tabel����
		table = new Table(group, SWT.BORDER | SWT.NONE | SWT.FULL_SELECTION
				| SWT.VIRTUAL);
		
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
		// �޸�table
		{
			table.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDoubleClick(MouseEvent e) {
					try {
						table.getSelection();
						// ��ձ༭��
						Control c = editor.getEditor();
						if (c != null) {
							c.dispose();
						}
						// �õ�ѡ�е���
						Point point = new Point(e.x, e.y);
						table.getSelectionIndex();
						
						final TableItem tableitem = table.getItem(point);
						TableItem ti = table.getItem(table.getTopIndex());
						// // �õ�ѡ�е���
						int column = -1;
						for (int i = 0; i < table.getColumnCount(); i++) {
							Rectangle rec = tableitem.getBounds(i);
							if (rec.contains(point)) {
								column = i;
							} else {// ���������ǲ����ڵ��С���ô����һ��
								new TableItem(table, SWT.NONE);
							}
						}

						// System.out.println("�����" + table.getItems().length);
						final int col1 = column;

						// �������޸Ķ������ı���
						final Text txt = new Text(table, SWT.NONE);
						txt.setText(tableitem.getText(col1));// ȡ�õ�ǰ��Ԫ�����ֵ
						editor.setEditor(txt, tableitem, col1);
						
						txt.addModifyListener(new ModifyListener() {
							@Override
							public void modifyText(ModifyEvent e) {
								// ���col1==0Ϊ��һ��
								tableitem.setText(col1, txt.getText());// ��һ�����������
								// System.out.println(tableitem.getText(0)+"----"+tableitem.getText(1));
								for (int i = 0; i < strArray.length; i++) {
									strArray[i] = "0:"+tableitem.getText(0) + "@" + tableitem.getText(1);
								}
								ScanScopeWizard.this.getWizard()
								.getDialogSettings()
								.put("Array", strArray);
								// ���Դ�����
								ScanScopeWizard.this.getWizard()
										.getDialogSettings()
										.put("��ʼID", tableitem.getText(0));
								ScanScopeWizard.this.getWizard()
										.getDialogSettings()
										.put("��ֹID", tableitem.getText(1));
							}

						});
						txt.selectAll();
						txt.forceFocus();

						// ��table����ʱtext editor��ʧ ʵ��ˢ��Ч��
						tableitem
								.addDisposeListener(new org.eclipse.swt.events.DisposeListener() {
									public void widgetDisposed(
											org.eclipse.swt.events.DisposeEvent e) {
										txt.dispose();
									}
								});
						// ��ȡ��������ֵ
						// TableItem[] tables = table.getItems();
						// for (int i = 0; i < tables.length; i++) {
						// System.out.println(tables[i].getText(0)+"-------------"+tables[i].getText(1));
						// // ������ͳһ����wizard�����ͨ�ô洢��DialogSettings����ֵ��
						// ScanScopeWizard.this.getWizard().getDialogSettings().put("",
						// "");
						// }
					} catch (Exception e2) {
						e2.getStackTrace();
					}
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