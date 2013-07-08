package com.siteview.snmp.scan;

import java.io.*;
import java.net.*;
import java.nio.channels.*;
import java.util.*;
import java.util.regex.*;

public class Ping {
	static int DAYTIME_PORT = 13;
	static int port = DAYTIME_PORT;

	

	public static void main(String[] args) throws InterruptedException,
			IOException {
//		args = new String[] { "8888", "192.168.0.248" };
//		if (args.length < 1) {
//			System.err.println("Usage: java Ping [port] host...");
//			return;
//		}
//		int firstArg = 0;
//		if (Pattern.matches("[0-9]+", args[0])) {
//			port = Integer.parseInt(args[0]);
//			firstArg = 1;
//		}
//		Printer printer = new Printer();
//		printer.start();
//		Connector connector = new Connector(printer);
//		connector.start();
//		LinkedList targets = new LinkedList();
//		for (int i = firstArg; i < args.length; i++) {
//			Target t = new Target(args[i]);
//			targets.add(t);
//			connector.add(t);
//		}
//		Thread.sleep(2000);
//		connector.shutdown();
//		connector.join();
//		for (Iterator i = targets.iterator(); i.hasNext();) {
//			Target t = (Target) i.next();
//			if (!t.shown)
//				t.show();
//		}
	}
}
