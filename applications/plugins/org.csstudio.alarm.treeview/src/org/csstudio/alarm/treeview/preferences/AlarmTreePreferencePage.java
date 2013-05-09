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
package org.csstudio.alarm.treeview.preferences;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.alarm.treeview.AlarmTreePlugin;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Preference page for the alarm tree.
 */
public class AlarmTreePreferencePage extends FieldEditorPreferencePage implements
        IWorkbenchPreferencePage {
    
    /**
     * Creates a new alarm tree preference page.
     */
    public AlarmTreePreferencePage() {
        super(GRID);
        setPreferenceStore(AlarmTreePlugin.getDefault().getPreferenceStore());
        setDescription("Alarm tree preferences");
    }
    
    /**
     * Creates the field editors. Field editors are abstractions of
     * the common GUI blocks needed to manipulate various types
     * of preferences. Each field editor knows how to save and
     * restore itself.
     */
    @Override
    public final void createFieldEditors() {
        addField(new StringFieldEditor(AlarmTreePreference.JMS_QUEUE.getKeyAsString(),
                                       "Topics:",
                                       newJmsGroup()));
        addField(new StringFieldEditor(AlarmTreePreference.ALARM_DISPLAY_ALIAS.getKeyAsString(),
                                       "Alias:",
                                       newAlarmAliasGroup()));
    }
    
    @Nonnull
    private Group newJmsGroup() {
        final Group result = newGroup();
        result.setText("JMS Settings");
        result.setLayoutData(newGridData());
        result.setLayout(newGridLayout());
        return result;
    }

    @Nonnull
    private Group newAlarmAliasGroup() {
        final Group result = newGroup();
        result.setText("Alarm Display Alias");
        result.setLayoutData(newGridData());
        result.setLayout(newGridLayout());
        return result;
    }
    
    @Nonnull
    private Group newGroup() {
        return new Group(getFieldEditorParent(), SWT.SHADOW_ETCHED_IN);
    }
    
    @Nonnull
    private GridLayout newGridLayout() {
        return new GridLayout(2, false);
    }
    
    @Nonnull
    private GridData newGridData() {
        return new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1);
    }
    
    @Override
    public void init(@CheckForNull final IWorkbench workbench) {
        // Nothing to do
    }
    
}
