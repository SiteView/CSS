/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron, Member of the Helmholtz Association, (DESY), HAMBURG,
 * GERMANY. THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. WITHOUT WARRANTY OF ANY KIND, EXPRESSED
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN ANY RESPECT, THE USER
 * ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN
 * ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER. DESY HAS
 * NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING
 * FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE
 * DISTRIBUTION OF THIS PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY AT
 * HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.utility.ldapupdater.service;

import java.util.Map;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration;
import org.csstudio.utility.ldapupdater.model.IOC;
import org.csstudio.utility.ldapupdater.model.Record;
import org.csstudio.utility.treemodel.ContentModel;
import org.csstudio.utility.treemodel.INodeComponent;

/**
 * Ldap updater service.
 *
 * @author bknerr
 * @since 27.04.2011
 */
public interface ILdapUpdaterService {

    void updateLDAPFromIOCList(@Nonnull final Map<String, INodeComponent<LdapEpicsControlsConfiguration>> iocs,
                               @Nonnull final Map<String, IOC> iocMap,
                               @Nonnull final TimeInstant lastHeartBeat) throws LdapUpdaterServiceException;

    /**
     * Tidies LDAP conservatively.
     * Gets an IOC map of valid existing IOCs and removes any entry in LDAP which is not contained in this map.
     *
     * @param iocs IOC entries found in LDAP
     * @param iocMapFromFS IOC entries found in the file system
     * @throws LdapUpdaterServiceException
     */
    void tidyUpLDAPFromIOCList(@Nonnull final Map<String, INodeComponent<LdapEpicsControlsConfiguration>> iocsFromLdap,
                               @Nonnull final Map<String, IOC> iocMapFromFS)
                               throws LdapUpdaterServiceException;

    @CheckForNull
    ContentModel<LdapEpicsControlsConfiguration> retrieveIOCs() throws LdapUpdaterServiceException;


    void removeIocEntryFromLdap(@Nonnull final String iocName,
                                @Nonnull final String facilityName) throws LdapUpdaterServiceException;


    void tidyUpIocEntryInLdap(@Nonnull final String iocName,
                              @Nonnull final String facilityName,
                              @Nonnull final Set<Record> validRecords) throws LdapUpdaterServiceException;
}
