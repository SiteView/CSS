package com.siteview.css.topo.wizard.scan;

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
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.*;

/**
 * ��ͬ��������
 * 
 * @author zhangxinnan
 * 
 */
public class CommunitySetupWizard extends WizardPage {
	private Table table;
	private TableEditor editor = null;

	protected CommunitySetupWizard() {
		super("Some wizard Page");
		setTitle("ɨ����� -->" + "ɨ�跶Χ -->" + "�ų���Χ -->" + "��ͬ������ -->" + "ɨ������");
		setMessage("WizardPage Message");
	}

	public void createControl(Composite parent) {
		Display display = parent.getDisplay();
		Shell shell = new Shell(display);
		shell.setLayout(new RowLayout(SWT.VERTICAL));
		final Table table1 = new Table(shell, SWT.BORDER | SWT.MULTI);
		table1.setHeaderVisible(true);
		table1.setLinesVisible(true);
		for (int i = 0; i < 4; i++) {
			TableColumn column = new TableColumn(table1, SWT.NONE);
			column.setText("Column " + i);
		}

		// ������ɫ
		// display = parent.getDisplay();
		// COLOR_SYSTEM_RED = display.getSystemColor(SWT.COLOR_RED);
		// Ⱥ��
		Group group = new Group(parent, SWT.NONE);
		group.setText("��ͬ������");
		RowLayout layout = new RowLayout(4);
		group.setLayout(layout);
		// GridData layoutData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		// layoutData.verticalIndent = 2;
		// group.setLayoutData(layoutData);

		// �������
		Label get = new Label(group, SWT.NONE);
		get.setText("GET");
		// GridData layoutD = new GridData();
		// layoutD.verticalIndent = 15;

		Text text = new Text(group, SWT.NONE);
		// GridData data = new GridData();
		// text.setLayoutData(data);

		Label set = new Label(group, SWT.NONE);
		set.setText("SET");

		Text text2 = new Text(group, SWT.NONE);

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
		col1.setWidth(260);
		TableColumn col2 = new TableColumn(table, SWT.LEFT);
		col2.setText("��ֹIP��ַ");
		col2.setWidth(260);
		TableColumn col3 = new TableColumn(table, SWT.LEFT);
		col3.setText("GET");
		col3.setWidth(80);

		// ��ӱ������
		final TableColumn[] columns = table.getColumns();
		for (int i = 0; i < columns.length; i++) {
			TableItem item = new TableItem(table, SWT.NONE);
			for (int j = 0; j < columns.length; j++) {
				// item.setText(j,""+i);//����Ĭ��ֵ
			}
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