
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
 */

package org.csstudio.archive.sdds.server.command;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.archive.sdds.server.util.RawData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.desy.aapi.AapiServerError;

/**
 * @author Markus Moeller
 *
 */
public abstract class AbstractServerCommand {

    /** The logger of this class */
    private static final Logger LOG = LoggerFactory.getLogger(AbstractServerCommand.class);

    /**
     *
     */
    public AbstractServerCommand() {
        // Constructor
    }

    /**
     *
     * @param buffer
     * @throws ServerCommandException
     * @throws CommandNotImplementedException
     */
    @CheckForNull
    public abstract RawData execute(@Nonnull final RawData buffer)
                                    throws ServerCommandException, CommandNotImplementedException;

    /**
     *
     * @param errorNumber
     * @return Byte array containing the error message
     */
    @CheckForNull
    public byte[] createErrorAnswer(@Nonnull final int errorNumber) {

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final DataOutputStream dos = new DataOutputStream(baos);
        byte[] result = null;

        try {
            dos.writeBytes(AapiServerError.getByNumber(errorNumber).toString());
            dos.writeByte('\0');

            result = baos.toByteArray();

        } catch(final IOException ioe) {
            LOG.error("[*** IOException ***]: ", ioe);
        } finally {
            try {
                dos.close();
            } catch (final Exception e) {
                LOG.error("Closing of data output stream failed.", e);
            }
        }

        return result;
    }
}
