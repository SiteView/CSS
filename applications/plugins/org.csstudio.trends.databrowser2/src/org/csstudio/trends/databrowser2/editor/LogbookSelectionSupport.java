/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.editor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.csstudio.logbook.Attachment;
import org.csstudio.logbook.AttachmentBuilder;
import org.csstudio.logbook.LogEntryBuilder;
import org.csstudio.logbook.LogbookBuilder;
import org.csstudio.swt.xygraph.figures.XYGraph;
import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;

/** Helper for supporting the logbook
 * 
 *  <p>Added as selection provider to the plot's context menu.
 *  
 *  <p>Sets the selection to a {@link LogbookBuilder}.
 *  
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class LogbookSelectionSupport implements ISelectionProvider
{
    final private XYGraph graph;
    
    public LogbookSelectionSupport(final XYGraph graph)
    {
        this.graph = graph;
    }

    // Simple selection provider, ignores listeners
    @Override
    public void addSelectionChangedListener(final ISelectionChangedListener listener) 
    {
        // NOP
    }

    @Override
    public void removeSelectionChangedListener(final ISelectionChangedListener listener)
    {
        // NOP
    }
    
    @Override
    public void setSelection(ISelection selection)
    {
        // NOP
    }
    
    @Override
    public ISelection getSelection()
    {
        try
        {
            final Attachment image_attachment = createImageAttachment();
            final String text = Messages.LogentryDefaultTitle + "\n" + Messages.LogentryDefaultBody;
            
            final LogEntryBuilder entry = LogEntryBuilder.withText(text)
                    .attach(AttachmentBuilder.attachment(image_attachment));
            
            return new StructuredSelection(entry);
        }        
        catch (Exception ex)
        {
            ExceptionDetailsErrorDialog.openError(null, Messages.Error, ex);
            return null;
        }
    }

    /** @return Logbook attachment for the plot's image  
     *  @throws Exception on error
     */
    private Attachment createImageAttachment() throws Exception
    {
        // Dump image into buffer
        ImageLoader loader = new ImageLoader();
        Image image = graph.getImage();
        loader.data = new ImageData[] { image.getImageData() };
        image.dispose();
        image = null;
        
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        loader.save(buf, SWT.IMAGE_PNG);
        buf.close();
        final byte[] image_bits = buf.toByteArray();
        buf = null;
        loader = null;
        
        // Attachment provides input stream
        return new Attachment()
        {
            @Override
            public Boolean getThumbnail()
            {
                return true;
            }
            
            @Override
            public InputStream getInputStream()
            {
                try
                {
                    return new ByteArrayInputStream(image_bits);
                }
                catch (Exception ex)
                {
                    return null;
                }
            }
            
            @Override
            public Long getFileSize()
            {
                return (long) image_bits.length;
            }
            
            @Override
            public String getFileName()
            {
                return "plot.png";
            }
            
            @Override
            public String getContentType()
            {
                return "image/png";
            }
        };
    }
}
