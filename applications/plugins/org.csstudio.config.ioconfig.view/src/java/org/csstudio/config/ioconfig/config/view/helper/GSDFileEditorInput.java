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

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.config.ioconfig.model.pbmodel.GSDFileDBO;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.2 $
 * @since 20.05.2010
 */
public class GSDFileEditorInput implements IEditorInput {


    private final GSDFileDBO _gsdFile;

    /**
     * Constructor.
     * @param gsdFile
     */
    public GSDFileEditorInput(@Nonnull final GSDFileDBO gsdFile) {
        super();
        _gsdFile = gsdFile;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exists() {
        return _gsdFile.getId()>0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public ImageDescriptor getImageDescriptor() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public String getName() {
        return _gsdFile.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public IPersistableElement getPersistable() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public String getToolTipText() {
        return String.valueOf(_gsdFile.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public Object getAdapter(@SuppressWarnings("rawtypes") @Nullable final Class adapter) {
        return _gsdFile.getGSDFile();
    }

}
