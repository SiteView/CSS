/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.utility.ldapupdater.action;

import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration.IOC;

import java.io.File;
import java.util.Map;

import javax.annotation.Nonnull;

import org.csstudio.domain.desy.service.osgi.OsgiServiceUnavailableException;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.remote.management.CommandParameters;
import org.csstudio.remote.management.CommandResult;
import org.csstudio.remote.management.IManagementCommand;
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration;
import org.csstudio.utility.ldapupdater.LdapUpdaterActivator;
import org.csstudio.utility.ldapupdater.LdapUpdaterUtil;
import org.csstudio.utility.ldapupdater.model.IOC;
import org.csstudio.utility.ldapupdater.preferences.LdapUpdaterPreferencesService;
import org.csstudio.utility.ldapupdater.service.ILdapUpdaterFileService;
import org.csstudio.utility.ldapupdater.service.ILdapUpdaterService;
import org.csstudio.utility.ldapupdater.service.LdapUpdaterServiceException;
import org.csstudio.utility.treemodel.ContentModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Command to start the tidy up mechanism for LDAP.
 * This action scans the IOC file directory for existing IOC record files and compares to the IOC entries in the LDAP system.
 * IOC names found in the LDAP that are not present in the directory are removed from LDAP including all records.
 *
 * @author bknerr
 */
public class TidyUpLdapAction implements IManagementCommand {

    private static final Logger LOG = LoggerFactory.getLogger(TidyUpLdapAction.class);

    private static final String TIDYUP_ACTION_NAME = "LDAP Tidy Up Action";

    /**
     * Singleton approach to make the service public to the action, a pain to test...
     */
    private static final LdapUpdaterUtil UPDATER = LdapUpdaterUtil.INSTANCE;

    /*
     * Singleton approach to make the service public to the action, a pain to test...
     */
    private ILdapUpdaterService _updaterService;
    private ILdapUpdaterFileService _fileService;
    private LdapUpdaterPreferencesService _prefsService;

    /**
     * Constructor.
     */
    public TidyUpLdapAction() {
        try {
            _updaterService = LdapUpdaterActivator.getDefault().getLdapUpdaterService();
            _fileService = LdapUpdaterActivator.getDefault().getLdapUpdaterFileService();
            _prefsService = LdapUpdaterActivator.getDefault().getLdapUpdaterPreferencesService();
        } catch (final OsgiServiceUnavailableException e) {
            LOG.error("LDAP service is not available!");
            throw new RuntimeException("LDAP service is not available!", e);
        }
    }

    /* (non-Javadoc)
     * @see org.csstudio.platform.management.IManagementCommand#execute(org.csstudio.platform.management.CommandParameters)
     */
    @Override
    @Nonnull
    public final CommandResult execute(@Nonnull final CommandParameters parameters) {
            try {
                if (!UPDATER.isBusy()){
                    UPDATER.setBusy(true);
                    tidyUpLdapFromIOCFiles();
                } else{
                    return CommandResult.createMessageResult("ldapUpdater is busy for max. 300 s (was probably started by timer). Try later!");
                }
            } catch (final Exception e) {
                LOG.error("Exception while running ldapUpdater", e);
                return CommandResult.createFailureResult("\"" + e.getCause() + "\"" + "-" + "Exception while running ldapUpdater");
            } finally {
                UPDATER.setBusy(false);
            }
            return CommandResult.createSuccessResult();
    }


    private void tidyUpLdapFromIOCFiles() {

        final TimeInstant startTime = UPDATER.logHeader(TIDYUP_ACTION_NAME);

        final File bootDirectory = _prefsService.getIocDblDumpPath();
        try {
            final ContentModel<LdapEpicsControlsConfiguration> model = _updaterService.retrieveIOCs();
            if (model.isEmpty()) {
                LOG.warn("LDAP search result is empty. No IOCs found.");
                return;
            }
            final Map<String, IOC> iocMapFromFS = _fileService.retrieveIocInformationFromBootDirectory(bootDirectory);

            _updaterService.tidyUpLDAPFromIOCList(model.getByType(IOC), iocMapFromFS);

        } catch (final LdapUpdaterServiceException e) {
            throw new RuntimeException("Retrieval of IOCs from LDAP failed!", e);
        } finally {
            UPDATER.setBusy(false);
            UPDATER.logFooter(TIDYUP_ACTION_NAME, startTime);
        }
    }

}
