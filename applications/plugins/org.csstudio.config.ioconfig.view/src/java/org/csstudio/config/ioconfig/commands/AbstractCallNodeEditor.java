/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.config.ioconfig.commands;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.config.ioconfig.model.AbstractNodeSharedImpl;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.view.DeviceDatabaseErrorDialog;
import org.csstudio.config.ioconfig.view.MainView;
import org.csstudio.config.ioconfig.view.internal.localization.IOConfigMessages;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.2 $
 * @since 07.06.2010
 */
public abstract class AbstractCallNodeEditor extends AbstractHandler {
    
    private static final Logger LOG = LoggerFactory.getLogger(AbstractCallNodeEditor.class);
    
    /**
     * (@inheritDoc)
     */
    @Override
    @CheckForNull
    public Object execute(@Nonnull final ExecutionEvent event) throws ExecutionException {
        
        final IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
        if (window != null) {
            final IWorkbenchPage page = window.getActivePage();
            
            final AbstractNodeSharedImpl<AbstractNodeSharedImpl<?,?>, AbstractNodeSharedImpl<?,?>> obj = getCallerNode(page);
            
            if (obj != null) {
                try {
                    openNodeEditor(obj, page);
                } catch (final PartInitException e1) {
                    MessageDialog.openError(null, "ERROR", e1.getMessage()); //$NON-NLS-1$
                    LOG.error("Can't open editor!",e1); //$NON-NLS-1$
                } catch (final PersistenceException e2) {
                    LOG.error("Can't open editor!",e2); //$NON-NLS-1$
                    DeviceDatabaseErrorDialog.open(null, IOConfigMessages.ABSTRACT_CALLNODE_EDITOR_DIALOG_ERROR_MSG, e2);
                }
            }
        }
        return null;
    }
    
    /**
     * @return
     */
    @SuppressWarnings("unchecked")
    @CheckForNull
    private AbstractNodeSharedImpl<AbstractNodeSharedImpl<?,?>, AbstractNodeSharedImpl<?,?>> getCallerNode(@Nonnull final IWorkbenchPage page) {
        //TODO: I think that is not the right way to do this.
        final MainView view = (MainView) page.findView(MainView.ID);
        // Get the selection
        final ISelection selection = view.getSite().getSelectionProvider().getSelection();
        if ( selection != null && selection instanceof IStructuredSelection) {
            final Object obj = ((IStructuredSelection) selection).getFirstElement();
            // If we had a selection lets open the editor
            if ( obj != null && obj instanceof AbstractNodeSharedImpl) {
                return (AbstractNodeSharedImpl<AbstractNodeSharedImpl<?,?>, AbstractNodeSharedImpl<?,?>>) obj;
            }
        }
        return null;
    }
    
    
    protected abstract void openNodeEditor(@Nonnull final AbstractNodeSharedImpl<AbstractNodeSharedImpl<?,?>,AbstractNodeSharedImpl<?,?>> parentNode,
                                           @Nonnull IWorkbenchPage page) throws PartInitException, PersistenceException;
    
    
}
