package com.siteview.nnm.main.mib;


import java.io.*;
import java.util.*;

public class DwSnmpMibOutputHandler
{

	boolean doLog=false;
	boolean autoScroll=true;
	static BufferedWriter outfile;

	public DwSnmpMibOutputHandler() {
	}

	public void setAutoScroll(boolean as) {
		autoScroll=as;
	}


	public void setLogging(boolean log) {

		try {
			if(log==true) {
				String strFileName=getLogFileName();
				outfile=new BufferedWriter(new FileWriter(strFileName,true));
				outfile.write("\n**********************************************************\n");
				outfile.write("MIB Browser Started at : " + new Date());
				outfile.write("\n**********************************************************\n");
				System.out.println("Output log file: "+ strFileName);
				this.doLog=true;

				java.util.Timer  tmr=new java.util.Timer(true);
				class SnmpTimerTask extends java.util.TimerTask {
					public void run() {
						try	{
							outfile.flush();
						} catch (Exception e) {
							System.out.println("Error in writing to log file: " + e);
						}
					}
				};
				long lFlushTime=getFlushTime();
				System.out.println("Log will be refreshed every " + lFlushTime/1000 + " seconds.");
				tmr.schedule(new SnmpTimerTask(),lFlushTime,lFlushTime);


				Thread thrdFlush=new Thread(new Runnable() {
					public void run() {
						try	{
							System.out.println("Have a nice day !!");
							outfile.write("\n**********************************************************\n");
							outfile.write("MIB Browser Stopped at : " + new Date());
							outfile.write("\n**********************************************************\n");
							outfile.flush();
							outfile.close();
						} catch (Exception e) {
							System.out.println("Error while writing to log file: "+ e);
						}
					}
				});
				Runtime.getRuntime().addShutdownHook(thrdFlush);

			} else outfile.close();
		}catch(Exception e) {
			System.out.println("Error : Cannot log" + e.toString());
		//	doLog=false;
			return;
		}
		doLog=true;
	}

	private String getLogFileName() {
		String strFileName=System.getProperty("logfilename");
		if(strFileName==null) {
			strFileName="mibbrowser.log";
		}
		return strFileName;
	}

	private long getFlushTime() {
		long lTime=0;
		String strTime=System.getProperty("logrefreshtime");
		if(strTime!=null) {
			try	{
				lTime=Long.parseLong(strTime);
				lTime=lTime*1000;
			} catch (Exception e) {
				System.out.println("Invalid value for log refresh time. default will be used.");
			}
		}

		if(lTime<1000) { // minimum must be 1 second.
			lTime=60*1000; // default is 1 minute.
		}
		return lTime;
	}

}
