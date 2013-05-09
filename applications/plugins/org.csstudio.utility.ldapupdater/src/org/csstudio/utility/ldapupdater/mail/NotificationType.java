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
package org.csstudio.utility.ldapupdater.mail;

import javax.annotation.Nonnull;

import org.csstudio.utility.ldap.treeconfiguration.LdapFieldsAndAttributes;
import org.csstudio.utility.ldapupdater.UpdaterLdapConstants;

/**
 * Mail templates for notification purposes of the LDAP update process.
 *
 * @author bknerr 24.03.2010
 */
public enum NotificationType {

    UNALLOWED_CHARS("Forbidden character in LDAP entry",
                    "The LDAP entry contains at least one forbidden character\n" +
                    "(not permitted are: " + LdapFieldsAndAttributes.FORBIDDEN_SUBSTRINGS + ")"),

    BOOT_DIR_FILE_MISMATCH("File mismatch between in boot directory!",
                           "Existing " + UpdaterLdapConstants.BOOT_FILE_SUFFIX +
                           " and " + UpdaterLdapConstants.RECORDS_FILE_SUFFIX + " do not match!"),

    IP_ADDRESS_NOT_UNIQUE("IP Address has been in use by another IOC.",
                          "The newly added IOC will hold this IP address from now on.\n" +
                          "Older IOCs' IP address attribute is reset to empty string in LDAP."),

    UNKNOWN_IOCS_IN_LDAP("IOC file missing!",
                         "The LDAP contains IOC entries, for which a corresponding IOC file could not be identified:"),

    IP_ADDRESS_NOT_SET_IN_LDAP("LDAP attribute " + LdapFieldsAndAttributes.ATTR_VAL_IOC_IP_ADDRESS + " not correctly set.",
                               "LDAP contains IOCs for which the attribute is missing."),

    UNKNOWN_ERROR ("Unknown Throwable!",
                   "");

    private static final String SUBJECT_PREFIX = "[LDAP Updater]: ";

    private final String _subject;
    private final String _text;

    /**
     * Constructor.
     */
    private NotificationType(@Nonnull final String subject,
                             @Nonnull final String text) {
        _subject = subject;
        _text = text;
    }

    /**
     * Getter.
     * @return the static subject
     */
    @Nonnull
    public String getSubject() {
        return SUBJECT_PREFIX + _subject;
    }

    /**
     * Getter.
     * @return the static body text
     */
    @Nonnull
    public String getText() {
        return _text;
    }

}
