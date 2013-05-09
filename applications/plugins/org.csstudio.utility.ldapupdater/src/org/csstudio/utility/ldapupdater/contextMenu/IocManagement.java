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
package org.csstudio.utility.ldapupdater.contextMenu;

import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import org.csstudio.domain.desy.service.osgi.OsgiServiceUnavailableException;
import org.csstudio.remote.management.CommandParameters;
import org.csstudio.remote.management.CommandResult;
import org.csstudio.remote.management.IManagementCommand;
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration;
import org.csstudio.utility.ldapupdater.LdapUpdaterActivator;
import org.csstudio.utility.ldapupdater.contextMenu.CommandEnumeration.IocModificationCommand;
import org.csstudio.utility.ldapupdater.model.Record;
import org.csstudio.utility.ldapupdater.service.ILdapUpdaterFileService;
import org.csstudio.utility.ldapupdater.service.ILdapUpdaterService;
import org.csstudio.utility.ldapupdater.service.LdapUpdaterServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * IOC Management Commands for the context menu of the LDAP Updater product.
 *
 * @author bknerr 17.03.2010
 */
public class IocManagement implements IManagementCommand {

    private static final Logger LOG = LoggerFactory.getLogger(IocManagement.class);
    private ILdapUpdaterService _service;
    private ILdapUpdaterFileService _fileService;

    /**
     * Constructor.
     */
    public IocManagement() {
        try {
            _service = LdapUpdaterActivator.getDefault().getLdapUpdaterService();
            _fileService = LdapUpdaterActivator.getDefault().getLdapUpdaterFileService();
        } catch (final OsgiServiceUnavailableException e) {
            LOG.error("Service unavailable.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    @SuppressWarnings("unchecked")
    public final CommandResult execute(@Nonnull final CommandParameters parameters) {

        final Map<String, String> value = (Map<String, String>) parameters.get("ioc");
        final String command = (String) parameters.get("command");

        try {
            return commandDispatchAndExecute(command, value);
        } catch (final LdapUpdaterServiceException e) {
            LOG.error("Command failed due to exception.", e);
            return CommandResult.createFailureResult("Internal LDAP Exception: " + command + " failed.\n" + e.getMessage());
        }
    }

    @Nonnull
    private CommandResult commandDispatchAndExecute(@Nonnull final String command,
                                                    @Nonnull final Map<String, String> value) throws LdapUpdaterServiceException {

        final String iocName = value.get(LdapEpicsControlsConfiguration.IOC.getNodeTypeName());
        final String facilityName = value.get(LdapEpicsControlsConfiguration.FACILITY.getNodeTypeName());


        switch (IocModificationCommand.valueOf(command)) {
            case DELETE :
                _service.removeIocEntryFromLdap(iocName, facilityName);
                return CommandResult.createSuccessResult();
            case TIDY_UP :
                final Set<Record> validRecords = _fileService.getBootRecordsFromIocFile(iocName);
                _service.tidyUpIocEntryInLdap(iocName,
                                              facilityName,
                                              validRecords);
                return CommandResult.createSuccessResult();
            default :
                throw new AssertionError("Unknown Ioc Modification Command: " + command);
        }
    }



}

