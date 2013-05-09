/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.alarm.table.preferences.archive;

import org.csstudio.alarm.table.JmsLogsPlugin;
import org.csstudio.alarm.table.internal.localization.Messages;
import org.csstudio.alarm.table.preferences.PreferenceColumnTableEditor;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 */

public class ArchiveViewPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	public ArchiveViewPreferencePage() {
		super(GRID);
		setPreferenceStore(JmsLogsPlugin.getDefault().getPreferenceStore());
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public void createFieldEditors() {
		PreferenceColumnTableEditor preferenceColumnTableEditor = new PreferenceColumnTableEditor();
        preferenceColumnTableEditor.init(ArchiveViewPreferenceConstants.P_STRINGArch,
                                         "Column Settings - " + Messages.LogArchiveViewerPreferencePage_columnNamesMessageKeys,
                                         getFieldEditorParent());
		// preferenceTopicTableEditor.setColumnTableReference(preferenceColumnTableEditor);
		addField(preferenceColumnTableEditor);
//		preferenceColumnTableEditor.setSelectionToColumnEditor(0, "");

		//		addField(new ListEditor(ArchiveViewPreferenceConstants.P_STRINGArch, ArchiveViewPreferenceConstants.P_STRINGArch + ": ", getFieldEditorParent()){ //$NON-NLS-1$
		//
		// public String[] parseString(String stringList){
		//				return stringList.split(";"); //$NON-NLS-1$
		// }
		//
		// public String getNewInputObject(){
		//				InputDialog inputDialog = new InputDialog(getFieldEditorParent().getShell(), Messages.LogArchiveViewerPreferencePage_newColumnName, Messages.LogArchiveViewerPreferencePage_column, "", null); //$NON-NLS-1$
		// if (inputDialog.open() == Window.OK) {
		// return inputDialog.getValue();
		// }
		// return null;
		// }
		//
		// public String createList(String[] items){
		//				String temp = ""; //$NON-NLS-1$
		// for(int i = 0; i < items.length;i++){
		//					temp = temp + items[i] + ";"; //$NON-NLS-1$
		// }
		// return temp;
		// }
		//
		// });
		Group group = new Group(getFieldEditorParent(), SWT.NONE);
		Composite com = new Composite(group, SWT.NONE);
		GridLayout layout = GridLayoutFactory.swtDefaults().extendedMargins(6, 6, 6, 6).create();
		group.setLayout(layout);
		com.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		StringFieldEditor date = new StringFieldEditor(
				ArchiveViewPreferenceConstants.DATE_FORMAT,
				Messages.LogArchiveViewerPreferencePage_dateFormat,
				com);
//		date.getLabelControl(getFieldEditorParent()).setToolTipText(
//				Messages.LogArchiveViewerPreferencePage_javaDateFormat);
		addField(date);
		StringFieldEditor answerSize = new StringFieldEditor(
				ArchiveViewPreferenceConstants.MAX_ANSWER_SIZE,
				Messages.LogArchiveViewerPreferencePage_maxAnswerSize,
				com);
//		answerSize.getLabelControl(getFieldEditorParent()).setToolTipText(
//				Messages.LogArchiveViewerPreferencePage_javaDateFormat);
		addField(answerSize);
		StringFieldEditor answerSizeExport = new StringFieldEditor(
				ArchiveViewPreferenceConstants.MAX_ANSWER_SIZE_EXPORT,
				Messages.LogArchiveViewerPreferencePage_maxAnswerSizeExport,
				com);
//		answerSize.getLabelControl(getFieldEditorParent()).setToolTipText(
//				Messages.LogArchiveViewerPreferencePage_javaDateFormat);
		addField(answerSizeExport);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */

	public void performApply() {
	}

	public void init(final IWorkbench workbench) {
	}
}