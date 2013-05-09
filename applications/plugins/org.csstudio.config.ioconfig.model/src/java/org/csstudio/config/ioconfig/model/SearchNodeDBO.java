/*
 * Copyright (c) 2007 Stiftung Deutsches Elektronen-Synchrotron,
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
/*
 * $Id: SearchNode.java,v 1.1 2009/08/26 07:08:57 hrickens Exp $
 */
package org.csstudio.config.ioconfig.model;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.1 $
 * @since 04.06.2009
 */
@Entity
@Table(name = "ddb_view_search_node")
public class SearchNodeDBO extends DBClass {
    
    private String _name;
    private String _epicsAddressString;
    private String _ioName;
    private Integer _parentId;
    
    @CheckForNull
    public String getEpicsAddressString() {
        return _epicsAddressString;
    }
    @CheckForNull
    public String getIoName() {
        return _ioName;
    }
    @CheckForNull
    public String getName() {
        return _name;
    }
    @Column(name = "parent_id", nullable=true)
    @CheckForNull
    public Integer getParentId() {
        return _parentId;
    }
    public void setEpicsaddressstring(@Nonnull final String epicsAddressString) {
        _epicsAddressString = epicsAddressString;
    }
    public void setEpicsAddressString(@Nonnull final String epicsAddressString) {
        _epicsAddressString = epicsAddressString;
    }
    public void setIoName(@Nonnull final String ioName) {
        _ioName = ioName;
    }
    public void setName(@Nonnull final String name) {
        _name = name;
    }
    public void setParentId(@Nonnull final Integer parentId) {
        _parentId = parentId;
    }
    
    
    
    
    
}
