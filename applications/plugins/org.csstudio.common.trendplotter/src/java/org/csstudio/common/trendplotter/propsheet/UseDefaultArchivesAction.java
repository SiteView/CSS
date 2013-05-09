/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.common.trendplotter.propsheet;

import org.csstudio.common.trendplotter.Activator;
import org.csstudio.common.trendplotter.Messages;
import org.csstudio.common.trendplotter.model.PVItem;
import org.csstudio.common.trendplotter.preferences.Preferences;
import org.csstudio.swt.xygraph.undo.OperationsManager;
import org.eclipse.jface.action.Action;

/** Action that configures PVs to use default archive data sources.
 *  @author Kay Kasemir
 */
public class UseDefaultArchivesAction extends Action
{
    final private OperationsManager operations_manager;
    final private PVItem pvs[];

    /** Initialize
     *  @param shell Parent shell for dialog
     *  @param pvs PVs that should use default archives
     */
    public UseDefaultArchivesAction(final OperationsManager operations_manager,
            final PVItem pvs[])
    {
        super(Messages.UseDefaultArchives,
              Activator.getDefault().getImageDescriptor("icons/archive.gif")); //$NON-NLS-1$
        this.operations_manager = operations_manager;
        this.pvs = pvs;
    }

    @Override
    public void run()
    {
        new AddArchiveCommand(operations_manager, pvs, Preferences.getArchives(), true);
    }
}
