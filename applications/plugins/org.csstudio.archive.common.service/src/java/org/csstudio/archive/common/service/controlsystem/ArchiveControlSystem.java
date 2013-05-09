/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.archive.common.service.controlsystem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.domain.desy.system.ControlSystem;
import org.csstudio.domain.desy.system.ControlSystemType;

/**
 * Archive control system bean.
 *
 * @author bknerr
 * @since 17.02.2011
 */
public class ArchiveControlSystem implements IArchiveControlSystem {

    private final ArchiveControlSystemId _id;
    private final String _name;
    private final ControlSystemType _type;

    /**
     * Constructor.
     */
    public ArchiveControlSystem(@Nonnull final String name,
                                @Nonnull final ControlSystemType type) {
        this(ArchiveControlSystemId.NONE, name, type);

    }
    /**
     * Constructor.
     */
    public ArchiveControlSystem(@Nonnull final ArchiveControlSystemId id,
                                @Nonnull final String name,
                                @Nonnull final ControlSystemType type) {
        _id = id;
        _name = name;
        _type = type;
    }

    /**
     * Constructor.
     */
    public ArchiveControlSystem(@Nonnull final ArchiveControlSystemId id,
                                @Nonnull final ControlSystem system) {
        _id = id;
        _name = system.getName();
        _type = system.getType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public ArchiveControlSystemId getId() {
        return _id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public String getName() {
        return _name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public ControlSystemType getType() {
        return _type;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = prime + (_id == null ? 0 : _id.hashCode());
        result = prime * result + (_name == null ? 0 : _name.hashCode());
        result = prime * result + (_type == null ? 0 : _type.hashCode());
        return result;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(@Nullable final Object obj) {
        if (!(obj instanceof ArchiveControlSystem)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        final ArchiveControlSystem other = (ArchiveControlSystem) obj;
        if (!_id.equals(other._id)) {
            return false;
        }
        if (!_name.equals(other._name)) {
            return false;
        }
        if (!_type.equals(other._type)) {
            return false;
        }
        return true;
    }
}
