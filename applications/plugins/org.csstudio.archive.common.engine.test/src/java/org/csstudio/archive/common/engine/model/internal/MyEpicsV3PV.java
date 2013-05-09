// CHECKSTYLE OFF : |
/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.common.engine.model.internal;


import gov.aps.jca.Channel;
import gov.aps.jca.Channel.ConnectionState;
import gov.aps.jca.Monitor;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.event.ConnectionEvent;
import gov.aps.jca.event.ConnectionListener;
import gov.aps.jca.event.GetEvent;
import gov.aps.jca.event.GetListener;
import gov.aps.jca.event.MonitorEvent;
import gov.aps.jca.event.MonitorListener;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.data.values.IMetaData;
import org.csstudio.data.values.IValue;
import org.csstudio.platform.libs.epics.EpicsPlugin.MonitorMask;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVListener;
import org.csstudio.utility.pv.epics.Activator;
import org.csstudio.utility.pv.epics.DBR_Helper;
import org.csstudio.utility.pv.epics.PVContext;
import org.csstudio.utility.pv.epics.RefCountedChannel;
import org.eclipse.core.runtime.PlatformObject;

/**
 * EPICS ChannelAccess implementation of the PV interface.
 *
 * Copied to handle monitor mask per channel...
 *
 *  @see PV
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class MyEpicsV3PV extends PlatformObject
            implements PV, ConnectionListener, MonitorListener {

    /** Use plain mode?
     *  @see #EPICS_V3_PV(String, boolean)
     */
    final boolean plain;

    /** Channel name. */
    final String name;

    private enum State
    {
        /** Nothing happened, yet */
        Idle,
        /** Trying to connect */
        Connecting,
        /** Got basic connection */
        Connected,
        /** Requested MetaData */
        GettingMetadata,
        /** Received MetaData */
        GotMetaData,
        /** Subscribing to receive value updates */
        Subscribing,
        /** Received Value Updates
         *  <p>
         *  This is the ultimate state!
         */
        GotMonitor,
        /** Got disconnected */
        Disconnected
    }

    State state = State.Idle;

    /** PVListeners of this PV */
    final private CopyOnWriteArrayList<PVListener> listeners
                                    = new CopyOnWriteArrayList<PVListener>();

    /** JCA channel. LOCK <code>this</code> on change. */
    private RefCountedChannel channel_ref = null;

    /** Either <code>null</code>, or the subscription identifier.
     *  LOCK <code>this</code> on change */
    private Monitor subscription = null;

    /** isConnected?
     *  <code>true</code> if we are currently connected
     *  (based on the most recent connection callback).
     *  <p>
     *  EPICS_V3_PV also runs notifyAll() on <code>this</code>
     *  whenever the connected flag changes to <code>true</code>.
     */
    private volatile boolean connected = false;

    /** Meta data obtained during connection cycle. */
    IMetaData meta = null;

    /** Most recent 'live' value. */
    private IValue value = null;

    /** isRunning?
     *  <code>true</code> if we want to receive value updates.
     */
    private volatile boolean running = false;

    /** Listener to the get... for meta data */
    private final GetListener meta_get_listener = new GetListener()
    {
        @Override
        public void getCompleted(final GetEvent event)
        {   // This runs in a CA thread
            if (event.getStatus().isSuccessful())
            {
                state = State.GotMetaData;
                final DBR dbr = event.getDBR();
                meta = DBR_Helper.decodeMetaData(dbr);
                Activator.getLogger().log(Level.FINEST, "{0} meta: {1}", new Object[] { name, meta });
            }
            else
            {
                Activator.getLogger().log(Level.WARNING, "{0} meta data get error: {1}",
                        new Object[] { name, event.getStatus().getMessage() });
            }
            // Subscribe, but outside of callback (JCA deadlocks)
            PVContext.scheduleCommand(new Runnable()
            {
                @Override
                public void run()
                {
                    subscribe();
                }
            });
        }
    };

    /** Listener to a get-callback for data. */
    private class GetCallbackListener implements GetListener
    {
        /** The received meta data/ */
        IMetaData meta1 = null;

        /** The received value. */
        IValue value1 = null;

        /** After updating <code>meta</code> and <code>value</code>,
         *  this flag is set, and then <code>notify</code> is invoked
         *  on <code>this</code>.
         */
        boolean got_response = false;

        /**
         * Constructor.
         */
        public GetCallbackListener() {
            // TODO Auto-generated constructor stub
        }
        synchronized public void reset()
        {
            got_response = false;
        }


        @Override
        public void getCompleted(final GetEvent event)
        {   // This runs in a CA thread
            if (event.getStatus().isSuccessful())
            {
                final DBR dbr = event.getDBR();
                meta1 = DBR_Helper.decodeMetaData(dbr);
                try
                {
                    value1 = DBR_Helper.decodeValue(plain, meta1, dbr);
                }
                catch (final Exception ex)
                {
                    Activator.getLogger().log(Level.WARNING, "PV " + name, ex);
                    value1 = null;
                }
                Activator.getLogger().log(Level.FINEST, "{0} meta: {1}, value {2}", new Object[] { name, meta1, value1 });
            }
            else
            {
                meta1 = null;
                value1 = null;
            }
            synchronized (this)
            {
                got_response = true;
                this.notifyAll();
            }
        }
    }
    private final GetCallbackListener get_callback = new GetCallbackListener();

    /** Generate an EPICS PV.
     *  @param name The PV name.
     */
    public MyEpicsV3PV(final String name)
    {
        this(name, PVContext.monitor_mask);
    }
    public MyEpicsV3PV(final String name,
                       final boolean plain)
    {
        this(name, PVContext.monitor_mask, plain);
    }
    public MyEpicsV3PV(final String name,
                       final MonitorMask mask)
    {
        this(name, mask, false);
    }

    /** Generate an EPICS PV.
     *  @param name The PV name.
     *  @param mask the monitorMask, if omitted {@link PVContext#monitor_mask} is taken.
     *  @param plain When <code>true</code>, only the plain value is requested.
     *               No time etc.
     *               Some PVs only work in plain mode, example: "record.RTYP".
     */
    public MyEpicsV3PV(final String name,
                       final MonitorMask mask,
                       final boolean plain)
    {
        this.name = name;
        this._monitorMask = mask;
        this.plain = plain;
        Activator.getLogger().finer(name + " created as EPICS_V3_PV");
    }

    /** Use finalize as last resort for cleanup, but give warnings. */
    @Override
    protected void finalize() throws Throwable
    {
        super.finalize();
        if (channel_ref != null)
        {
            Activator.getLogger().warning("EPICS_V3_PV " + name + " not properly stopped");
            try
            {
                stop();
            }
            catch (final Throwable ex)
            {
                Activator.getLogger().log(Level.WARNING, name + " finalize error", ex);
            }
        }
        Activator.getLogger().finer(name + " finalized.");
    }

    /** @return Returns the name. */
    @Override
    public String getName()
    {
        return MyEpicsPVFactory.PREFIX + "://" + name;
    }

    /** {@inheritDoc} */
    @Override
    public IValue getValue(final double timeout_seconds) throws Exception
    {
        final long end_time = System.currentTimeMillis() +
                                (long)(timeout_seconds * 1000);
        // Try to connect (NOP if already connected)
        connect();
        // Wait for connection
        while (! connected)
        {   // Wait...
            final long remain = end_time - System.currentTimeMillis();
            if (remain <= 0) {
                throw new Exception("PV " + name + " connection timeout");
            }
            synchronized (this)
            {
                this.wait(remain);
            }
        }
        // Reset the callback data
        get_callback.reset();
        // Issue the 'get'
        final DBRType type = DBR_Helper.getCtrlType(plain,
                                      channel_ref.getChannel().getFieldType());
        Activator.getLogger().log(Level.FINEST, "{0} get-callback as {1}", new Object[] { name, type.getName() });
        channel_ref.getChannel().get(
                        type, channel_ref.getChannel().getElementCount(),
                        get_callback);
        // Wait for value callback
        synchronized (get_callback)
        {
            while (! get_callback.got_response)
            {   // Wait...
                final long remain = end_time - System.currentTimeMillis();
                if (remain <= 0) {
                    throw new Exception("PV " + name + " value timeout");
                }
                get_callback.wait(remain);
            }
        }
        value = get_callback.value1;
        return get_callback.value1;
    }

    /** {@inheritDoc} */
    @Override
    public IValue getValue()
    {
        return value;
    }

    /** {@inheritDoc} */
    @Override
    public void addListener(final PVListener listener)
    {
        listeners.add(listener);
         if (running && isConnected()) {
            listener.pvValueUpdate(this);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void removeListener(final PVListener listener)
    {
        listeners.remove(listener);
    }

    /** Try to connect to the PV.
     *  OK to call more than once.
     */
    private void connect() throws Exception
    {
        state = State.Connecting;
        // Already attempted a connection?
        synchronized (this)
        {
            if (channel_ref == null) {
                channel_ref = PVContext.getChannel(name, MyEpicsV3PV.this);
            }
        }
        if (channel_ref.getChannel().getConnectionState()
            == ConnectionState.CONNECTED)
        {
            Activator.getLogger().log(Level.FINEST, "{0} is immediately connected", name);
            handleConnected(channel_ref.getChannel());
        }
    }

    /** Disconnect from the PV.
     *  OK to call more than once.
     */
    private void disconnect()
    {
        // Releasing the _last_ channel will close the context,
        // which waits for the JCA Command thread to exit.
        // If a connection or update for the channel happens at that time,
        // the JCA command thread will send notifications to this PV,
        // which had resulted in dead lock:
        // This code locked the PV, then tried to join the JCA Command thread.
        // JCA Command thread tried to lock the PV, so it could not exit.
        // --> Don't lock while calling into the PVContext.
        RefCountedChannel channel_ref_copy;
        synchronized (this)
        {
            // Never attempted a connection?
            if (channel_ref == null) {
                return;
            }
            channel_ref_copy = channel_ref;
            channel_ref = null;
            connected = false;
        }
        try
        {
            PVContext.releaseChannel(channel_ref_copy, this);
        }
        catch (final Throwable e)
        {
            e.printStackTrace();
        }
        fireDisconnected();
    }

    private final MonitorMask _monitorMask;

    /** Subscribe for value updates. */
    void subscribe()
    {
        synchronized (this)
        {
            // Prevent multiple subscriptions.
            if (subscription != null) {
                return;
            }
            // Late callback, channel already closed?
            final RefCountedChannel ch_ref = channel_ref;
            if (ch_ref == null) {
                return;
            }
            final Channel channel = ch_ref.getChannel();
            final Logger logger = Activator.getLogger();
            try
            {
                // TODO Instead of another channel.addMonitor(),
                //      the RefCountedChannel should maintain a single
                //      subscription to the underlying CAJ/JCA channel.
                //      So even with N PVs for the same channel, it's
                //      only one subscription on the network instead of
                //      N subscriptions.
                final DBRType type = DBR_Helper.getTimeType(plain,
                                        channel.getFieldType());

                //final MonitorMask mask = PVContext.monitor_mask;

                logger.log(Level.FINER, "{0} subscribed as {1} ({2})",
                        new Object[] { name, type.getName(), _monitorMask.name() } );
                state = State.Subscribing;
                subscription = channel.addMonitor(type,
                       channel.getElementCount(),
                       _monitorMask.getMask(), this);
            }
            catch (final Exception ex)
            {
                logger.log(Level.SEVERE, name + " subscribe error", ex);
            }
        }
    }

    /** Unsubscribe from value updates. */
    void unsubscribe()
    {
        Monitor sub_copy;
        // Atomic access
        synchronized (this)
        {
            sub_copy = subscription;
            subscription = null;
        }
        if (sub_copy == null) {
            return;
        }
        try
        {
            sub_copy.clear();
        }
        catch (final Exception ex)
        {
            Activator.getLogger().log(Level.SEVERE, name + " unsubscribe error", ex);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void start() throws Exception
    {
        if (running) {
            return;
        }
        running = true;
        connect();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isRunning()
    {   return running;  }

    /** {@inheritDoc} */
    @Override
    public boolean isConnected()
    {
        return connected;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isWriteAllowed()
    {
        return connected && channel_ref.getChannel().getWriteAccess();
    }

    /** {@inheritDoc} */
    @Override
    public String getStateInfo()
    {
        return state.toString();
    }

    /** {@inheritDoc} */
    @Override
    public void stop()
    {
        running = false;
        unsubscribe();
        disconnect();
    }

    /** {@inheritDoc} */
    @Override
    public void setValue(final Object new_value) throws Exception
    {
        if (!isConnected()) {
            throw new Exception(name + " is not connected");
        }
        // Send strings as strings..
        if (new_value instanceof String) {
            channel_ref.getChannel().put((String)new_value);
        } else
        {   // other types as double.
            if (new_value instanceof Double)
            {
                final double val = ((Double)new_value).doubleValue();
                channel_ref.getChannel().put(val);
            }
            else if (new_value instanceof Double [])
            {
                final Double dbl[] = (Double [])new_value;
                final double val[] = new double[dbl.length];
                for (int i=0; i<val.length; ++i) {
                    val[i] = dbl[i].doubleValue();
                }
                channel_ref.getChannel().put(val);
            }
            else if (new_value instanceof Integer)
            {
                final int val = ((Integer)new_value).intValue();
                channel_ref.getChannel().put(val);
            }
            else if (new_value instanceof Integer [])
            {
                final Integer ival[] = (Integer [])new_value;
                final int val[] = new int[ival.length];
                for (int i=0; i<val.length; ++i) {
                    val[i] = ival[i].intValue();
                }
                channel_ref.getChannel().put(val);
            } else {
                throw new Exception("Cannot handle type "
                                    + new_value.getClass().getName());
            }
        }
    }

    /** ConnectionListener interface. */
    @Override
    public void connectionChanged(final ConnectionEvent ev)
    {
        // This runs in a CA thread
        if (ev.isConnected())
        {   // Transfer to JCACommandThread to avoid deadlocks
            // The connect event can actually happen 'right away'
            // when the channel is created, before we even get to assign
            // the channel_ref. So use the channel from the event, not
            // the channel_ref which might still be null.
            PVContext.scheduleCommand(new Runnable()
            {
                @Override
                public void run()
                {
                    handleConnected((Channel) ev.getSource());
                }
            });
        }
        else
        {
            Activator.getLogger().log(Level.FINEST, "{0} disconnected", name);
            state = State.Disconnected;
            connected = false;
            PVContext.scheduleCommand(new Runnable()
            {
                @Override
                public void run()
                {
                    unsubscribe();
                    fireDisconnected();
                }
            });
        }
    }

    /** PV is connected.
     *  Get meta info, or subscribe right away.
     */
    void handleConnected(final Channel channel)
    {
        Activator.getLogger().log(Level.FINEST, "{0} connected ({1})", new Object[] { name, state.name() });
        if (state == State.Connected) {
            return;
        }
        state = State.Connected;

        // If we're "running", we need to get the meta data and
        // then subscribe.
        // Otherwise, we're done.
        if (!running)
        {
            synchronized (this)
            {
                connected = true;
                meta = null;
                    this.notifyAll();
                return;
            }
        }
        // else: running, get meta data, then subscribe
        try
        {
            DBRType type = channel.getFieldType();
            if (! (plain || type.isSTRING()))
            {
                state = State.GettingMetadata;
                Activator.getLogger().fine("Getting meta info for type "
                                    + type.getName());
                if (type.isDOUBLE()  ||  type.isFLOAT()) {
                    type = DBRType.CTRL_DOUBLE;
                } else if (type.isENUM()) {
                    type = DBRType.LABELS_ENUM;
                } else {
                    type = DBRType.CTRL_SHORT;
                }
                channel.get(type, 1, meta_get_listener);
                return;
            }
        }
        catch (final Exception ex)
        {
            Activator.getLogger().log(Level.SEVERE, name + " connection handling error", ex);
            return;
        }

        // Meta info is not requested, not available for this type,
        // or there was an error in the get call.
        // So reset it, then just move on to the subscription.
        meta = null;
        subscribe();
    }

   /** MonitorListener interface. */
    @Override
    public void monitorChanged(final MonitorEvent ev)
    {
        //INumericMetaData metaData = (INumericMetaData) DBR_Helper.decodeMetaData(ev.getDBR());

        Activator.getLogger().log(Level.FINEST, "meta: {0}", new Object[] { ev.getDBR().getClass().getName() });

        final Logger log = Activator.getLogger();
        // This runs in a CA thread.
        // Ignore values that arrive after stop()
        if (!running)
        {
            log.finer(name + " monitor while not running (" + state.name() + ")");
            return;
        }

        synchronized (this) {
            if (subscription == null)
            {
                log.finer(name + " monitor while not subscribed (" + state.name() + ")");
                return;
            }
        }

        if (! ev.getStatus().isSuccessful())
        {
            log.warning(name + " monitor error :" + ev.getStatus().getMessage());
            return;
        }

        state = State.GotMonitor;
        try
        {
            value =
                DBR_Helper.decodeValue(plain, meta, ev.getDBR());
            if (!connected) {
                connected = true;
            }
            // Logging every received value is expensive and chatty.
            log.log(Level.FINEST, "{0} monitor: {1} ({2})",
                    new Object[] { name, value, value.getClass().getName() });
            fireValueUpdate();
        }
        catch (final Exception ex)
        {
            log.log(Level.WARNING, name + " monitor value error", ex);
        }
    }

    /** Notify all listeners. */
    private void fireValueUpdate()
    {
        for (final PVListener listener : listeners) {
            listener.pvValueUpdate(this);
        }
    }

    /** Notify all listeners. */
    void fireDisconnected()
    {
        for (final PVListener listener : listeners) {
            listener.pvDisconnected(this);
        }
    }

    @Override
    public String toString()
    {
        return "EPICS_V3_PV '" + name + "'";
    }
}
// CHECKSTYLE ON : |
