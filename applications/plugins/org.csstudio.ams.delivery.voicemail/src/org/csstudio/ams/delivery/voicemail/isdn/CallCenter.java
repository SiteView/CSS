
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

package org.csstudio.ams.delivery.voicemail.isdn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Markus Moeller
 *
 */
public class CallCenter {
    
    private static final Logger LOG = LoggerFactory.getLogger(CallCenter.class);
    
    /** The receiver of a call */
    private CapiReceiver receiver;
    
    /** The class that makes a call */
    private CapiCaller caller;
    
    /** Flag that indicates if the CallCenter is initialized and active */
    private boolean active;
    
    /** Number of retries for a call */
    private final int RETRY = 3;
    
    public CallCenter() throws CallCenterException {
        try {
            receiver = new CapiReceiver();
            receiver.start();
            caller = new CapiCaller();
            active = true;
        } catch(CapiReceiverException cre) {
            active = false;
            throw new CallCenterException(cre);
        } catch(CapiCallerException cce) {
            active = false;
            throw new CallCenterException(cce);
        }
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void makeCall(String telephoneNumber,
                         String message,
                         String textType,
                         String chainIdAndPos,
                         String waitUntil) throws CallCenterException {
        
        CallInfo callInfo;
        long waitTime = 0;
        int type;
        int callCount = 0;
        
        try {
            type = Integer.parseInt(textType);
        } catch(NumberFormatException nfe) {
            type = 0;
            LOG.error("Text type is invalid: " + textType);
            throw new CallCenterException("Text type is invalid: " + textType);
        }

        if(waitUntil != null) {
            if(waitUntil.trim().length() > 0) {
                try {
                    waitTime = Long.parseLong(waitUntil);
                } catch(NumberFormatException nfe) {
                    waitTime = 0;
                    LOG.warn("Wait time is invalid: " + waitUntil);
                    
                    // Throw only an exception if the alarm needs to be confirmed
                    if(TextType.ALARM_WCONFIRM.getTypeNumber() == type) {
                        throw new CallCenterException("Waiting time for alarm with confirmation is invalid: " + waitUntil);
                    }
                }
            }
        }
        
        switch(type) {
            
            case 1: // TEXTTYPE_ALARM_WOCONFIRM
                
                try {
                    do {
                        callInfo = null;
                        callInfo = caller.makeCallWithoutReply(telephoneNumber, message);
                        callCount++;
                    } while((!callInfo.isSuccess()) && (callCount < RETRY));
                } catch(CapiCallerException cce) {
                    throw new CallCenterException(cce);
                }

                break;
                
            case 2: // TEXTTYPE_ALARM_WCONFIRM

                try {
                    do {
                        callInfo = null;
                        callInfo = caller.makeCallWithReply(telephoneNumber, message, chainIdAndPos);
                        callCount++;
                    } while((!callInfo.isSuccess()) && (System.currentTimeMillis() < waitTime));
                } catch(CapiCallerException cce) {
                    throw new CallCenterException(cce);
                }
                
                LOG.debug("Confirmation code: " + callInfo.getConfirmationCode());
               
                break;
                
            case 3: // TEXTTYPE_ALARMCONFIRM_OK
            case 4: // TEXTTYPE_ALARMCONFIRM_NOK
            case 5: // TEXTTYPE_STATUSCHANGE_OK
            case 6: // TEXTTYPE_STATUSCHANGE_NOK                
        }
    }
    
    public void closeCallCenter() {
        
        if (receiver != null) {
            receiver.close();
        }
        
        if (caller != null) {
            caller.close();
        }
    }    
}
