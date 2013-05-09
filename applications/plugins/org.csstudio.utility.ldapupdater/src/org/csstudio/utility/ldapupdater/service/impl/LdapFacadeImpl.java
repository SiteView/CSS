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
 *
 * $Id$
 */
package org.csstudio.utility.ldapupdater.service.impl;

import static org.csstudio.utility.ldap.service.util.LdapUtils.any;
import static org.csstudio.utility.ldap.service.util.LdapUtils.attributesForLdapEntry;
import static org.csstudio.utility.ldap.service.util.LdapUtils.createLdapName;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration.COMPONENT;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration.FACILITY;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration.IOC;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration.RECORD;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration.UNIT;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration.VIRTUAL_ROOT;
import static org.csstudio.utility.ldap.treeconfiguration.LdapFieldsAndAttributes.ATTR_FIELD_OBJECT_CLASS;
import static org.csstudio.utility.ldap.treeconfiguration.LdapFieldsAndAttributes.ATTR_VAL_IOC_IP_ADDRESS;
import static org.csstudio.utility.ldap.treeconfiguration.LdapFieldsAndAttributes.ATTR_VAL_IOC_OBJECT_CLASS;
import static org.csstudio.utility.ldap.treeconfiguration.LdapFieldsAndAttributes.ATTR_VAL_REC_OBJECT_CLASS;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.ldap.LdapName;

import org.csstudio.domain.desy.net.IpAddress;
import org.csstudio.domain.desy.service.osgi.OsgiServiceUnavailableException;
import org.csstudio.utility.ldap.service.ILdapContentModelBuilder;
import org.csstudio.utility.ldap.service.ILdapSearchResult;
import org.csstudio.utility.ldap.service.ILdapService;
import org.csstudio.utility.ldap.service.LdapServiceException;
import org.csstudio.utility.ldap.service.util.LdapNameUtils;
import org.csstudio.utility.ldap.service.util.LdapUtils;
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration;
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsFieldsAndAttributes;
import org.csstudio.utility.ldap.treeconfiguration.LdapFieldsAndAttributes;
import org.csstudio.utility.ldapupdater.model.IOC;
import org.csstudio.utility.ldapupdater.model.Record;
import org.csstudio.utility.ldapupdater.service.ILdapFacade;
import org.csstudio.utility.ldapupdater.service.ILdapUpdaterServicesProvider;
import org.csstudio.utility.ldapupdater.service.LdapFacadeException;
import org.csstudio.utility.treemodel.ContentModel;
import org.csstudio.utility.treemodel.CreateContentModelException;
import org.csstudio.utility.treemodel.INodeComponent;
import org.csstudio.utility.treemodel.ITreeNodeConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * Implements access to LDAP Service for LdapUpdater application.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 05.05.2010
 */
public class LdapFacadeImpl implements ILdapFacade {


    private static final Logger LOG = LoggerFactory.getLogger(LdapFacadeImpl.class);

    private final ILdapUpdaterServicesProvider _serviceProvider;

    /**
     * Constructor.
     */
    @Inject
    public LdapFacadeImpl(@Nonnull final ILdapUpdaterServicesProvider provider) {
        _serviceProvider = provider;
    }
    @Nonnull
    private ILdapService getLdapService() throws OsgiServiceUnavailableException {
        return _serviceProvider.getLdapService();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean createLdapRecord(@Nonnull final LdapName newLdapName) throws LdapFacadeException {

        final String recordName =
            LdapNameUtils.getValueOfRdnType(newLdapName, RECORD.getNodeTypeName());

        final Attributes afe =
            attributesForLdapEntry(ATTR_FIELD_OBJECT_CLASS, ATTR_VAL_REC_OBJECT_CLASS,
                                   RECORD.getNodeTypeName(), recordName);

        try {
            return getLdapService().createComponent(newLdapName, afe);
        } catch (final OsgiServiceUnavailableException e) {
            throw new LdapFacadeException("Service unavailable on creating LDAP record", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean createLdapIoc(@Nonnull final LdapName newLdapName,
                                 @Nonnull final IOC ioc)
                                 throws LdapFacadeException {

        final IpAddress ipAddress = ioc.getIpAddress();
        //TimeInstant lastBootTime = ioc.getLastBootTime();

        final Attributes afe =
            attributesForLdapEntry(ATTR_FIELD_OBJECT_CLASS, ATTR_VAL_IOC_OBJECT_CLASS,
                                   ATTR_VAL_IOC_IP_ADDRESS, ipAddress.toString());
        // TODO (bknerr) : modify schema and add new attribute
//                                   ATTR_FIELD_LAST_UPDATED, time,

        try {
            return getLdapService().createComponent(newLdapName, afe);
        } catch (final OsgiServiceUnavailableException e) {
            throw new LdapFacadeException("Service unavailable on creating LDAP Ioc", e);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public Map<String, INodeComponent<LdapEpicsControlsConfiguration>> retrieveRecordsForIOC(@Nonnull final LdapName fullIocName)
        throws LdapFacadeException {

        if (fullIocName.size() > 0) {
            final LdapName query = new LdapName(fullIocName.getRdns());
            try {
                final ILdapSearchResult result =
                    getLdapService().retrieveSearchResultSynchronously(query,
                                                                       any(RECORD.getNodeTypeName()),
                                                                       SearchControls.ONELEVEL_SCOPE);
                if (result != null) {
                    final ContentModel<LdapEpicsControlsConfiguration> model =
                            getLdapService().getLdapContentModelForSearchResult(LdapEpicsControlsConfiguration.VIRTUAL_ROOT, result);
                    if (model != null) {
                        return model.getChildrenByTypeAndSimpleName(RECORD);
                    }
                }
            } catch (final OsgiServiceUnavailableException e) {
                throw new LdapFacadeException("Service unavailable on retrieving LDAP records for IOC " + fullIocName, e);
            } catch (final CreateContentModelException e) {
                throw new LdapFacadeException("Content model could not be created on record retrieval for IOC " + fullIocName, e);
            } catch (final LdapServiceException e) {
                throw new LdapFacadeException("Ldap Service exception. " + fullIocName, e);
            }
        }
        return Collections.emptyMap();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public ContentModel<LdapEpicsControlsConfiguration> retrieveRecordsForIOC(@Nonnull final String facilityName,
                                                                              @Nonnull final String iocName)
                                                                              throws LdapFacadeException {

        final LdapName query = createLdapName(IOC.getNodeTypeName(), iocName,
                                              COMPONENT.getNodeTypeName(), LdapEpicsControlsFieldsAndAttributes.ECOM_EPICS_IOC_FIELD_VALUE,
                                              FACILITY.getNodeTypeName(), facilityName,
                                              UNIT.getNodeTypeName(), UNIT.getUnitTypeValue());

        try {
             final ILdapSearchResult result =
                 getLdapService().retrieveSearchResultSynchronously(query,
                                                                    any(RECORD.getNodeTypeName()),
                                                                    SearchControls.ONELEVEL_SCOPE);
             return result != null ? getLdapService().getLdapContentModelForSearchResult(VIRTUAL_ROOT, result) :
                                     null;

        } catch (final OsgiServiceUnavailableException e) {
            throw new LdapFacadeException("Service unavailable on retrieving LDAP records for IOC", e);
        } catch (final CreateContentModelException e) {
            throw new LdapFacadeException("Conten modelt creation on removing IOC from LDAP failed.", e);
        } catch (final LdapServiceException e) {
            throw new LdapFacadeException("LDAP service exception.", e);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void removeIocEntryFromLdap(@Nonnull final String iocName,
                                       @Nonnull final String facilityName) throws LdapFacadeException {

        try {

            final ContentModel<LdapEpicsControlsConfiguration> model = retrieveRecordsForIOC(iocName, facilityName);
            if (model == null) {
                throw new LdapFacadeException("Retrieval of " + facilityName + ":" + iocName + " failed on removal from LDAP.", null);
            }
            final Collection<INodeComponent<LdapEpicsControlsConfiguration>> records =
                model.getChildrenByTypeAndLdapName(RECORD).values();
            for (final INodeComponent<LdapEpicsControlsConfiguration> record : records) {
                final LdapName ldapName = record.getLdapName();

                getLdapService().removeLeafComponent(ldapName);
            }
            getLdapService().removeLeafComponent(createLdapName(IOC.getNodeTypeName(), iocName,
                                                                COMPONENT.getNodeTypeName(), LdapEpicsControlsFieldsAndAttributes.ECOM_EPICS_IOC_FIELD_VALUE,
                                                                FACILITY.getNodeTypeName(), facilityName,
                                                                UNIT.getNodeTypeName(), UNIT.getUnitTypeValue()));
        } catch (final OsgiServiceUnavailableException e) {
            throw new LdapFacadeException("Service unavailable on removing IOC from LDAP.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tidyUpIocEntryInLdap(@Nonnull final String iocName,
                                     @Nonnull final String facilityName,
                                     @Nonnull final Set<Record> validRecords) throws LdapFacadeException {

        try {
            final ContentModel<LdapEpicsControlsConfiguration> model = retrieveRecordsForIOC(iocName, facilityName);

            if (model == null  || model.isEmpty()) {
                LOG.info("Empty content model for this searchRestult - no tidying possible.");
                return;
            }

            removeLeafComponents(validRecords, model);

        } catch (final OsgiServiceUnavailableException e) {
            throw new LdapFacadeException("Service unavailable on tidying IOC entries LDAP.", e);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public <T extends Enum<T> & ITreeNodeConfiguration<T>> ILdapContentModelBuilder<T>
        getLdapContentModelBuilder(@Nonnull final ContentModel<T> model) throws LdapFacadeException {

        try {
            return getLdapService().getLdapContentModelBuilder(model);
        } catch (final OsgiServiceUnavailableException e) {
            throw new LdapFacadeException("Service unavailable on getting content model builder.", e);
        } catch (final LdapServiceException e) {
            throw new LdapFacadeException("LDAP service exception.", e);
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public <T extends Enum<T> & ITreeNodeConfiguration<T>> ILdapContentModelBuilder<T>
    getLdapContentModelBuilder(@Nonnull final T objectClassRoot, @Nonnull final ILdapSearchResult result) throws LdapFacadeException {

        try {
            return getLdapService().getLdapContentModelBuilder(objectClassRoot, result);
        } catch (final OsgiServiceUnavailableException e) {
            throw new LdapFacadeException("Service unavailable on getting content model builder.", e);
        } catch (final LdapServiceException e) {
            throw new LdapFacadeException("LDAP service exception.", e);
        }
    }


    private void removeLeafComponents(@Nonnull final Set<Record> validRecords,
                                      @Nonnull final ContentModel<LdapEpicsControlsConfiguration> model)
            throws OsgiServiceUnavailableException {

        final Map<String, INodeComponent<LdapEpicsControlsConfiguration>> recordsInLdap =
            model.getChildrenByTypeAndSimpleName(RECORD);


        for (final INodeComponent<LdapEpicsControlsConfiguration> record : recordsInLdap.values()) {
            if (!validRecords.contains(new Record(record.getName()))) {

                final LdapName ldapName = record.getLdapName();

                getLdapService().removeLeafComponent(ldapName);
                LOG.info("Tidying: Record {} removed.", record.getName());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public ContentModel<LdapEpicsControlsConfiguration> retrieveIOCs() throws LdapFacadeException {

        try {
            final ILdapSearchResult result =
                getLdapService().retrieveSearchResultSynchronously(LdapUtils.createLdapName(UNIT.getNodeTypeName(), UNIT.getUnitTypeValue()),
                                                                   any(IOC.getNodeTypeName()),
                                                                   SearchControls.SUBTREE_SCOPE);
            if (result == null) {
                return null;
            }
            final ContentModel<LdapEpicsControlsConfiguration> model =
                    getLdapService().getLdapContentModelForSearchResult(VIRTUAL_ROOT, result);
            return model != null ? model : null;
        } catch (final CreateContentModelException e) {
            throw new LdapFacadeException("Content model could not be created to retrieve IOCs.", e);
        } catch (final OsgiServiceUnavailableException e) {
            throw new LdapFacadeException("Service unavailable on retrieving IOCs.", e);
        } catch (final LdapServiceException e) {
            throw new LdapFacadeException("LDAP service exception.", e);
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public INodeComponent<LdapEpicsControlsConfiguration> retrieveIOC(@Nonnull final LdapName iocLdapName)
        throws LdapFacadeException {

            if (iocLdapName.size() > 0) {
                final String iocSimpleName = LdapNameUtils.simpleName(iocLdapName);
                final LdapName query = LdapNameUtils.baseName(iocLdapName);

                try {
                    final ILdapSearchResult result =
                        getLdapService().retrieveSearchResultSynchronously(query,
                                                                           LdapUtils.equ(IOC.getNodeTypeName(), iocSimpleName),
                                                                           SearchControls.ONELEVEL_SCOPE);
                    if (result != null) {
                        final ContentModel<LdapEpicsControlsConfiguration> model =
                            getLdapService().getLdapContentModelForSearchResult(LdapEpicsControlsConfiguration.VIRTUAL_ROOT, result);

                        return model != null ? model.getByTypeAndLdapName(IOC, iocLdapName) : null;
                    }
                } catch (final OsgiServiceUnavailableException e) {
                    throw new LdapFacadeException("Service unavailable on retrieving LDAP records for IOC", e);
                } catch (final CreateContentModelException e) {
                    throw new LdapFacadeException("Content model could not be created for IOC " + iocLdapName.toString(), e);
                } catch (final LdapServiceException e) {
                    throw new LdapFacadeException("LDAP service exception for " + iocLdapName.toString(), e);
                }
            }
            return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void modifyIpAddressAttribute(@Nonnull final LdapName name,
                                         @CheckForNull final IpAddress address) throws LdapFacadeException {

        final BasicAttribute attr = new BasicAttribute(LdapFieldsAndAttributes.ATTR_VAL_IOC_IP_ADDRESS,
                                                       address != null ? address.toString() : null);
        final ModificationItem mod = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, attr);
        try {
            getLdapService().modifyAttributes(name, new ModificationItem[] {mod});
        } catch (final OsgiServiceUnavailableException e) {
            throw new LdapFacadeException("LDAP service unavailable for modifying IP address attribute.", e);
        } catch (final NamingException e) {
            throw new LdapFacadeException("Modification of IP address attribute failed.", e);
        }

    }
}


