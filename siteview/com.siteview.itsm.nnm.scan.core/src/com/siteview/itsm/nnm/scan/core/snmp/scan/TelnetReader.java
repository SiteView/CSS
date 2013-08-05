package com.siteview.itsm.nnm.scan.core.snmp.scan;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.net.telnet.EchoOptionHandler;
import org.apache.commons.net.telnet.InvalidTelnetOptionException;
import org.apache.commons.net.telnet.SuppressGAOptionHandler;
import org.apache.commons.net.telnet.TelnetClient;
import org.apache.commons.net.telnet.TelnetOption;
import org.apache.commons.net.telnet.TerminalTypeOptionHandler;

public class TelnetReader {

	private TelnetClient tc = new TelnetClient();
	private TelnetClientEngine engine;
	private TelnetReceivedHandler handler;
	private OutputStream outstr_ = null;
	private boolean terminal = false;
	public static final int DEFAULT_PORT = 23;

	private String serverName = "";
	private String serverIPAdress = "";
	private String password = "";
	private int serverPort = 23;

	private class TelnetClientEngine implements Runnable {
		private TelnetReader stc;
		private Thread engineThread;

		public TelnetClientEngine(TelnetReader stc) {
			this.stc = stc;
		}

		public void begin() {
			engineThread = new Thread(this);
			engineThread.start();
		}

		public void irrupt() throws Exception {
			engineThread.interrupt();
			engineThread.join();
		}

		public boolean end() {
			try {
				stc.getTC().disconnect();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}

		public void run() {
			outstr_ = stc.getTC().getOutputStream();
			InputStream instr = stc.getTC().getInputStream();
			try {
				byte[] buff = new byte[1024];
				int ret_read = 0;
				do {
					ret_read = instr.read(buff);
					if (ret_read > 0) {
						handler.callback(buff, ret_read);
					}
				} while (ret_read >= 0);
			} catch (Exception e) {
				// logger.warn("Exception: ", e);
				e.printStackTrace();
			} finally {
				if (terminal)
					;
				else {
					this.begin();
				}
			}
		}
	}

	public TelnetReader(String name, String address,String pwd, int port) {
		this.serverName = name;
		this.serverIPAdress = address;
		this.serverPort = port;
		this.password = pwd;
	}
	public TelnetReader(String address) {
//		this("",address,DEFAULT_PORT);
	}

	public void setHandler(TelnetReceivedHandler handler) {
		this.handler = handler;
	}
	public void excute(TelnetReceivedHandler handler){
		start();
		
	}
	public boolean start() {
		if (!setOption())
			return false;
		if (!connect())
			return false;
		engine = new TelnetClientEngine(this);
		engine.begin();

		return true;
	}

	public void stop() {
		try {
			this.terminal = true;
			this.removeOption();
			engine.end();

			return;
		} catch (Exception e) {
			// logger.error("Exception:", e);
			e.printStackTrace();
		}

		// logger.info("Shut down Telnet Client Thread failed, so force to kill.");

		try {
			engine.irrupt();
		} catch (Exception e) {
			// logger.error("Exception:", e);
			e.printStackTrace();
		}
	}

	private boolean connect() {
		// logger.info("Connect to " + serverName + ":" + serverIPAdress + ", "
		// + serverPort + ".");
		try {
			tc.connect(serverIPAdress, serverPort);
		} catch (Exception e) {
			// logger.warn("Exception:", e);
			e.printStackTrace();
			return false;
		}
		// logger.info("Connected to " + serverName + " successful.");
		return true;
	}

	private boolean setOption() {
		TerminalTypeOptionHandler ttopt = new TerminalTypeOptionHandler("",
				false, false, true, false);
		EchoOptionHandler echoopt = new EchoOptionHandler(true, false, true,
				false);
		SuppressGAOptionHandler gaopt = new SuppressGAOptionHandler(true, true,
				true, true);

		try {
			tc.addOptionHandler(ttopt);
			tc.addOptionHandler(echoopt);
			tc.addOptionHandler(gaopt);
		} catch (InvalidTelnetOptionException e) {
			// logger.warn("Exception: ", e);
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	private void removeOption() throws Exception {
		tc.deleteOptionHandler(TelnetOption.TERMINAL_TYPE);
		tc.deleteOptionHandler(TelnetOption.ECHO);
		tc.deleteOptionHandler(TelnetOption.SUPPRESS_GO_AHEAD);
	}

	private TelnetClient getTC() {
		return tc;
	}

	public boolean send(byte[] byteArray) {
		try {
			outstr_.write(byteArray, 0, byteArray.length);
			outstr_.flush();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
