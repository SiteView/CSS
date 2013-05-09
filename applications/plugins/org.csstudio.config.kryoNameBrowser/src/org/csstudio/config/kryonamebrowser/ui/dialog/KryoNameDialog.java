package org.csstudio.config.kryonamebrowser.ui.dialog;

import org.csstudio.config.kryonamebrowser.logic.KryoNameBrowserLogic;
import org.csstudio.config.kryonamebrowser.ui.UIModelBridge;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Base for dialogs that deal with name entry.
 * 
 * 
 * @author Alen Vrecko
 * 
 */
public abstract class KryoNameDialog extends TitleAreaDialog {

	public boolean isCallUpdate() {
		return callUpdate;
	}

	// package private
	Text desc;
//	Combo process;
	Combo subfunction;
	Combo function;
	Combo object;
	Combo subplant3;
	Combo subplant2;
	Combo subplant1;
	Combo plant;
	Text kryoNum;
	Text subplant3No;
	Text subplant2No;
	Text plantNo;
	Text subplant1No;
	KryoNameBrowserLogic logic;
	Label nameLabel;
	UIModelBridge bridge;
	boolean callUpdate;

	public KryoNameDialog(Shell parentShell) {
		super(parentShell);

	}

	public void setLogic(KryoNameBrowserLogic logic) {
		this.logic = logic;

	}

	protected Control createContents(Composite parent) {
		Control contents = super.createContents(parent);
		// Set the title
		setTitle(getTitle());
		// Set the message
		setMessage(getDescription(), IMessageProvider.INFORMATION);
		return contents;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		// return super.createDialogArea(parent);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		parent.setLayout(layout);

		nameLabel = new Label(parent, SWT.NONE);
		final GridData gd_nameLabel = new GridData(SWT.FILL, SWT.CENTER, true,
				false);
		nameLabel.setLayoutData(gd_nameLabel);
		// nameLabel.setFont(SWTResourceManager.getFont("", 20, SWT.NONE));

		final Group plants = new Group(parent, SWT.NONE);
		final GridData gd_plants = new GridData(SWT.FILL, SWT.CENTER, true,
				false);
		gd_plants.widthHint = 200;
		plants.setLayoutData(gd_plants);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		plants.setLayout(gridLayout);
		new Label(plants, SWT.NONE);

		final Label plantLabel = new Label(plants, SWT.NONE);
		plantLabel
				.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		plantLabel.setText("Plant");

		final Label noLabel = new Label(plants, SWT.NONE);
		noLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
		noLabel.setText("No");

		Label label1 = new Label(plants, SWT.SHADOW_NONE);
		label1.setLayoutData(new GridData());
		label1.setText("Plant");

		plant = new Combo(plants, SWT.READ_ONLY);
		final GridData gd_plant = new GridData(SWT.FILL, SWT.CENTER, true,
				false);
		gd_plant.widthHint = 200;
		plant.setLayoutData(gd_plant);
		plantNo = new Text(plants, SWT.BORDER);
		plantNo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		Label label2 = new Label(plants, SWT.NONE);
		label2.setLayoutData(new GridData());
		label2.setText("Sub plant 1");

		subplant1 = new Combo(plants, SWT.READ_ONLY);
		final GridData gd_subplant1 = new GridData(SWT.FILL, SWT.CENTER, true,
				false);
		subplant1.setLayoutData(gd_subplant1);

		subplant1No = new Text(plants, SWT.BORDER);
		subplant1No.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false));

		final Label subPlant2Label = new Label(plants, SWT.NONE);
		subPlant2Label.setLayoutData(new GridData());
		subPlant2Label.setText("Sub plant 2");

		subplant2 = new Combo(plants, SWT.READ_ONLY);
		final GridData gd_subplant2 = new GridData(SWT.FILL, SWT.CENTER, true,
				false);
		subplant2.setLayoutData(gd_subplant2);

		subplant2No = new Text(plants, SWT.BORDER);
		final GridData gd_subplant2No = new GridData(SWT.FILL, SWT.CENTER,
				true, false);
		subplant2No.setLayoutData(gd_subplant2No);

		final Label subPlant3Label = new Label(plants, SWT.NONE);
		subPlant3Label.setLayoutData(new GridData());
		subPlant3Label.setText("Sub plant 3");

		subplant3 = new Combo(plants, SWT.READ_ONLY);
		final GridData gd_subplant3 = new GridData(SWT.FILL, SWT.CENTER, true,
				false);
		subplant3.setLayoutData(gd_subplant3);

		subplant3No = new Text(plants, SWT.BORDER);
		final GridData gd_subplant3No = new GridData(SWT.FILL, SWT.CENTER,
				true, false);
		subplant3No.setLayoutData(gd_subplant3No);
		new Label(parent, SWT.NONE);

		final Group objects = new Group(parent, SWT.NONE);
		objects.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		final GridLayout gridLayout_1 = new GridLayout();
		gridLayout_1.numColumns = 2;
		objects.setLayout(gridLayout_1);

		final Label objectLabel = new Label(objects, SWT.NONE);
		objectLabel.setText("Object");

		object = new Combo(objects, SWT.READ_ONLY);
		final GridData gd_object = new GridData(SWT.FILL, SWT.CENTER, true,
				false);
		object.setLayoutData(gd_object);

		final Label functionLabel = new Label(objects, SWT.NONE);
		functionLabel.setText("Function");

		function = new Combo(objects, SWT.READ_ONLY);
		final GridData gd_function = new GridData(SWT.FILL, SWT.CENTER, true,
				false);
		function.setLayoutData(gd_function);

		final Label subFunctionLabel = new Label(objects, SWT.NONE);
		subFunctionLabel.setText("Sub function");

		subfunction = new Combo(objects, SWT.READ_ONLY);
		final GridData gd_subfunction = new GridData(SWT.FILL, SWT.CENTER,
				true, false);
		subfunction.setLayoutData(gd_subfunction);

		final Group proc = new Group(parent, SWT.NONE);
		proc.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		final GridLayout gridLayout_2 = new GridLayout();
		gridLayout_2.numColumns = 3;
		proc.setLayout(gridLayout_2);

//		final Label label_1 = new Label(proc, SWT.NONE);
//		new Label(proc, SWT.NONE);

		final Label noLabel_1 = new Label(proc, SWT.NONE);
		noLabel_1.setText("Kryo Num");

//		final Label processLabel = new Label(proc, SWT.NONE);
//		processLabel.setText("Process");
//
//		process = new Combo(proc, SWT.READ_ONLY);
//		final GridData gd_process = new GridData(SWT.FILL, SWT.CENTER, true,
//				false);
//		process.setLayoutData(gd_process);

		kryoNum = new Text(proc, SWT.BORDER);
		kryoNum.setTextLimit(4);
		final GridData gd_kryoNum = new GridData(SWT.FILL, SWT.CENTER, true,
				false);
		kryoNum.setLayoutData(gd_kryoNum);

		final Group group = new Group(parent, SWT.NONE);
		group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		final GridLayout gridLayout_3 = new GridLayout();
		group.setLayout(gridLayout_3);

		final Label descriptionLabel = new Label(group, SWT.NONE);
		final GridData gd_descriptionLabel = new GridData();
		descriptionLabel.setLayoutData(gd_descriptionLabel);
		descriptionLabel.setText("Description");

		desc = new Text(group, SWT.MULTI | SWT.BORDER);
		desc.setTextLimit(200);
		final GridData gd_desc = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd_desc.heightHint = 60;
		gd_desc.widthHint = 300;
		desc.setLayoutData(gd_desc);

		bridge = new UIModelBridge();
		bridge.registerPlant(plant, plantNo);
		bridge.registerSubPlant1(subplant1, subplant1No);
		bridge.registerSubPlant2(subplant2, subplant2No);
		bridge.registerSubPlant3(subplant3, subplant3No);
		bridge.registerObject(object);
		bridge.registerFunction(function);
		bridge.registerSubfunction(subfunction);
		bridge.registerDescription(desc);
		bridge.registerKryoNumber(kryoNum);
//		bridge.registerProcess(process);
		bridge.setLogic(logic);
		bridge.bind();

		bridge.addListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}

			public void widgetSelected(SelectionEvent e) {
				nameLabel.setText(bridge.calculateName());
			}
		});

		return parent;
	}

	protected void createButtonsForButtonBar(Composite parent) {
		((GridLayout) parent.getLayout()).numColumns += 2;
		getButton(parent);

		Button closeButton = new Button(parent, SWT.PUSH);
		closeButton.setText("Close");
		closeButton.setFont(JFaceResources.getDialogFont());
		closeButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				close();
			}
		});
	}

	protected abstract void getButton(Composite parent);

	protected abstract String getTitle();

	protected abstract String getDescription();

}
