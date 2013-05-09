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
package org.csstudio.config.ioconfig.config.view.helper;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.config.ioconfig.model.DocumentDBO;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.pbmodel.GSDFileDBO;
import org.csstudio.config.ioconfig.model.tools.Helper;
import org.csstudio.config.ioconfig.view.DeviceDatabaseErrorDialog;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.2 $
 * @since 19.05.2010
 */
public class ShowFileSelectionListener implements SelectionListener {
    
    /**
     * @author hrickens
     * @author $Author: hrickens $
     * @version $Revision: 1.2 $
     * @since 20.05.2010
     */
    private static final class GSDFileEditDialog extends Dialog {
        private static final Color GREEN = new Color(null, 0, 100, 0);
        private static final Color WHITE = new Color(null, 255, 255, 255);
        private final GSDFileDBO _gsdFile;
        private StyledText _text;
        
        /**
         * Constructor.
         * @param parentShell
         */
        protected GSDFileEditDialog(@Nullable final Shell parentShell,
                                    @Nonnull final GSDFileDBO gsdFile) {
            super(parentShell);
            _gsdFile = gsdFile;
        }
        
        /**
         * @param gsdFile
         * @return
         */
        @Nonnull
        private StyleRange[] makeStyleRanges(@Nonnull final String gsdFile) {
            final List<StyleRange> styleRangeList = new ArrayList<StyleRange>();
            int start = 0;
            do {
                final int indexOf = gsdFile.indexOf(';', start);
                start = indexOf;
                if (indexOf >= 0) {
                    final int end = gsdFile.indexOf('\n', indexOf);
                    styleRangeList.add(new StyleRange(start, end - start, GREEN, WHITE));
                    start = end;
                }
            } while (start >= 0);
            return styleRangeList.toArray(new StyleRange[0]);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        protected void configureShell(@Nonnull final Shell newShell) {
            super.configureShell(newShell);
            newShell.setText(_gsdFile.getName());
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        protected void createButtonsForButtonBar(@Nonnull final Composite parent) {
            createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        @Nonnull
        protected Control createDialogArea(@Nonnull final Composite parent) {
            final Control createDialogArea = super.createDialogArea(parent);
            _text = new StyledText(parent, SWT.MULTI | SWT.LEAD | SWT.BORDER | SWT.H_SCROLL
                                   | SWT.V_SCROLL);
            _text.setEditable(false);
            final GridData create = GridDataFactory.fillDefaults().hint(800, 600).create();
            _text.setLayoutData(create);
            final String gsdFile = _gsdFile.getGSDFile();
            _text.setText(gsdFile);
            final StyleRange[] ranges = makeStyleRanges(gsdFile);
            _text.setStyleRanges(ranges);
            _text.pack();
            createDialogArea.pack();
            return createDialogArea;
        }
        
    }
    
    private static final Logger LOG = LoggerFactory.getLogger(ShowFileSelectionListener.class);
    
    private final TableViewer _parentViewer;
    
    public ShowFileSelectionListener(@Nonnull final TableViewer parentViewer) {
        _parentViewer = parentViewer;
    }
    
    @Override
    public void widgetDefaultSelected(@Nullable final SelectionEvent e) {
        openFileInBrowser();
    }
    
    @Override
    public void widgetSelected(@Nullable final SelectionEvent e) {
        openFileInBrowser();
    }
    
    private void openFileInBrowser() {
        final StructuredSelection selection = (StructuredSelection) _parentViewer.getSelection();
        final Object object = selection.getFirstElement();
        File createTempFile = null;
        try {
            if (object instanceof GSDFileDBO) {
                final GSDFileDBO firstElement = (GSDFileDBO) object;
                final GSDFileEditDialog gsdEditDialog = new GSDFileEditDialog(Display.getDefault()
                                                                              .getActiveShell(), firstElement);
                gsdEditDialog.open();
            } else if (object instanceof DocumentDBO) {
                final DocumentDBO firstElement = (DocumentDBO) object;
                String filename = firstElement.getSubject();
                if (filename == null || filename.length() < 1) {
                    filename = "showTmp";
                }
                createTempFile = File.createTempFile(filename, "." + firstElement.getMimeType());
                Helper.writeDocumentFile(createTempFile, firstElement);
            }
            if (createTempFile != null && createTempFile.isFile()) {
                if (Desktop.isDesktopSupported()) {
                    if (Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                        Desktop.getDesktop().open(createTempFile);
                    }
                }
            }
        } catch (final IOException e) {
            MessageDialog.openError(null, "Can't File create!", e.getMessage());
            LOG.error("Can't File create!", e);
        } catch (final PersistenceException e) {
            DeviceDatabaseErrorDialog.open(null, "Can't read document from database!", e);
            LOG.error("Can't read document from database!", e);
        }
    }
    
}
