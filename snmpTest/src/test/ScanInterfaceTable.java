package test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.rmi.CORBA.UtilDelegate;

import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.CommunityTarget;
import org.snmp4j.MessageException;
import org.snmp4j.PDU;
import org.snmp4j.PDUv1;
import org.snmp4j.ScopedPDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.UserTarget;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.log.LogFactory;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.mp.StateReference;
import org.snmp4j.mp.StatusInformation;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.TSM;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.Counter32;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.SMIConstants;
import org.snmp4j.smi.TcpAddress;
import org.snmp4j.smi.TimeTicks;
import org.snmp4j.smi.TlsAddress;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.tools.console.SnmpRequest;
import org.snmp4j.transport.AbstractTransportMapping;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.transport.TLSTM;
import org.snmp4j.util.PDUFactory;
import org.snmp4j.util.TableEvent;
import org.snmp4j.util.TableListener;
import org.snmp4j.util.TableUtils;

public class ScanInterfaceTable implements CommandResponder, PDUFactory {

	public static final int DEFAULT = 0;
	  public static final int WALK = 1;
	  public static final int LISTEN = 2;
	  public static final int TABLE = 3;
	  public static final int CVS_TABLE = 4;
	  public static final int TIME_BASED_CVS_TABLE = 5;
	  public static final int SNAPSHOT_CREATION = 6;
	  public static final int SNAPSHOT_DUMP = 7;

	  Target target;
	  Address address;
	  OID authProtocol;
	  OID privProtocol;
	  OctetString privPassphrase;
	  OctetString authPassphrase;
	  OctetString community = new OctetString("public");
	  OctetString authoritativeEngineID;
	  OctetString contextEngineID;
	  OctetString contextName = new OctetString();
	  OctetString securityName = new OctetString();
	  OctetString localEngineID = new OctetString(MPv3.createLocalEngineID());

	  TimeTicks sysUpTime = new TimeTicks(0);
	  OID trapOID = SnmpConstants.coldStart;

	  PDUv1 v1TrapPDU = new PDUv1();

	  int version = SnmpConstants.version3;
	  int engineBootCount = 0;
	  int retries = 1;
	  int timeout = 1000;
	  int pduType = PDU.GETNEXT;
	  int maxRepetitions = 10;
	  int nonRepeaters = 0;
	  int maxSizeResponsePDU = 65535;
	  Vector<VariableBinding> vbs = new Vector<VariableBinding>();
	  File snapshotFile;

	  protected int operation = DEFAULT;

	  int numDispatcherThreads = 2;

	  boolean useDenseTableOperation = false;

	  // table options
	  OID lowerBoundIndex, upperBoundIndex;
	private Target createTarget() {
	    if (version == SnmpConstants.version3) {
	      UserTarget target = new UserTarget();
	      if (authPassphrase != null) {
	        if (privPassphrase != null) {
	          target.setSecurityLevel(SecurityLevel.AUTH_PRIV);
	        }
	        else {
	          target.setSecurityLevel(SecurityLevel.AUTH_NOPRIV);
	        }
	      }
	      else {
	        target.setSecurityLevel(SecurityLevel.NOAUTH_NOPRIV);
	      }
	      target.setSecurityName(securityName);
	      if (authoritativeEngineID != null) {
	        target.setAuthoritativeEngineID(authoritativeEngineID.getValue());
	      }
	      if (address instanceof TlsAddress) {
	        target.setSecurityModel(TSM.SECURITY_MODEL_TSM);
	      }
	      return target;
	    }
	    else {
	      CommunityTarget target = new CommunityTarget();
	      target.setCommunity(community);
	      return target;
	    }
	  }
	public void init(int version,String addressString){
		this.version = version;
		address = GenericAddress.parse(addressString);
		operation = TABLE;
		community = new OctetString("siteview");
		
		vbs.add(new VariableBinding(OIDConstants.INTERFACE_ifTable));
	}
	
	public static void main(String[] args) {
		String address = "udp:192.168.0.248/161";
		int version = SnmpConstants.version2c;
		ScanInterfaceTable test = new ScanInterfaceTable();
		test.init(version, address);
		try {
			test.table();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void table() throws IOException{
		Snmp snmp = createSnmpSession();
	    this.target = createTarget();
	    target.setVersion(version);
	    target.setAddress(address);
	    target.setRetries(2);
	    target.setTimeout(2000);
	    snmp.listen();

	    TableUtils tableUtils = new TableUtils(snmp, this);
	    tableUtils.setMaxNumRowsPerPDU(maxRepetitions);
	    Counter32 counter = new Counter32();

	    OID[] columns = new OID[vbs.size()];
	    for (int i=0; i<columns.length; i++) {
	      columns[i] = (vbs.get(i)).getOid();
	    }
	    long startTime = System.nanoTime();
	    synchronized (counter) {

	      TableListener listener;
	      if (operation == TABLE) {
	        listener = new TextTableListener();
	      }
	      else {
	        listener = new CVSTableListener(System.nanoTime());
	      }
	      if (useDenseTableOperation) {
	        tableUtils.getDenseTable(target, columns, listener, counter,
	                                 lowerBoundIndex, upperBoundIndex);
	      }
	      else {
	        tableUtils.getTable(target, columns, listener, counter,
	                            lowerBoundIndex, upperBoundIndex);
	      }
	      try {
	        counter.wait(timeout);
	      }
	      catch (InterruptedException ex) {
	        Thread.currentThread().interrupt();
	      }
	    }
	    System.out.println("Table received in "+
	                       (System.nanoTime()-startTime)/1000000+" milliseconds.");
	    snmp.close();
	}
	 private Snmp createSnmpSession() throws IOException {
		    AbstractTransportMapping transport;
		    if (address instanceof TlsAddress) {
		      transport = new TLSTM();
		    }
		    else if (address instanceof TcpAddress) {
		      transport = new DefaultTcpTransportMapping();
		    }
		    else {
		      transport = new DefaultUdpTransportMapping();
		    }
		    // Could save some CPU cycles:
		    // transport.setAsyncMsgProcessingSupported(false);
		    Snmp snmp =  new Snmp(transport);
		    ((MPv3)snmp.getMessageProcessingModel(MPv3.ID)).
		        setLocalEngineID(localEngineID.getValue());

		    if (version == SnmpConstants.version3) {
		      USM usm = new USM(SecurityProtocols.getInstance(),
		                        localEngineID,
		                        engineBootCount);
		      SecurityModels.getInstance().addSecurityModel(usm);
		      addUsmUser(snmp);
		      SecurityModels.getInstance().addSecurityModel(
		          new TSM(localEngineID, false));
		    }
		    return snmp;
		  }
	@Override
	public PDU createPDU(Target target) {
		PDU request;
	    if (target.getVersion() == SnmpConstants.version3) {
	      request = new ScopedPDU();
	      ScopedPDU scopedPDU = (ScopedPDU)request;
	      if (contextEngineID != null) {
	        scopedPDU.setContextEngineID(contextEngineID);
	      }
	      if (contextName != null) {
	        scopedPDU.setContextName(contextName);
	      }
	    }
	    else {
	      if (pduType == PDU.V1TRAP) {
	        request = v1TrapPDU;
	      }
	      else {
	        request = new PDU();
	      }
	    }
	    request.setType(pduType);
	    return request;
	}
	@Override
	public void processPdu(CommandResponderEvent e) {
		PDU command = e.getPDU();
	    if (command != null) {
	      System.out.println(command.toString());
	      if ((command.getType() != PDU.TRAP) &&
	          (command.getType() != PDU.V1TRAP) &&
	          (command.getType() != PDU.REPORT) &&
	          (command.getType() != PDU.RESPONSE)) {
	        command.setErrorIndex(0);
	        command.setErrorStatus(0);
	        command.setType(PDU.RESPONSE);
	        StatusInformation statusInformation = new StatusInformation();
	        StateReference ref = e.getStateReference();
	        try {
	          e.getMessageDispatcher().returnResponsePdu(e.getMessageProcessingModel(),
	                                                     e.getSecurityModel(),
	                                                     e.getSecurityName(),
	                                                     e.getSecurityLevel(),
	                                                     command,
	                                                     e.getMaxSizeResponsePDU(),
	                                                     ref,
	                                                     statusInformation);
	        }
	        catch (MessageException ex) {
	          System.err.println("Error while sending response: "+ex.getMessage());
	          LogFactory.getLogger(SnmpRequest.class).error(ex);
	        }
	      }
	    }
	}
	class TextTableListener implements TableListener {

	    private boolean finished;

	    public void finished(TableEvent event) {
	      System.out.println();
	      System.out.println("Table walk completed with status "+event.getStatus()+
	                         ". Received "+
	                         event.getUserObject()+" rows.");
	      finished = true;
	      synchronized (event.getUserObject()) {
	        event.getUserObject().notify();
	      }
	    }

	    public boolean next(TableEvent event) {
	      System.out.println("Index = "+event.getIndex()+":");
	      for (int i=0; i<event.getColumns().length; i++) {
	        System.out.println(event.getColumns()[i]);
	      }
	      System.out.println();
	      ((Counter32)event.getUserObject()).increment();
	      return true;
	    }

	    public boolean isFinished() {
	      return finished;
	    }

	  }
	class CVSTableListener implements TableListener {

	    private long requestTime;
	    private boolean finished;

	    public CVSTableListener(long time) {
	      this.requestTime = time;
	    }

	    public boolean next(TableEvent event) {
	      if (operation == TIME_BASED_CVS_TABLE) {
	        System.out.print(requestTime);
	        System.out.print(",");
	      }
	      System.out.print("\""+event.getIndex()+"\",");
	      for (int i=0; i<event.getColumns().length; i++) {
	        Variable v = event.getColumns()[i].getVariable();
	        String value = v.toString();
	        switch (v.getSyntax()) {
	          case SMIConstants.SYNTAX_OCTET_STRING: {
	            StringBuffer escapedString = new StringBuffer(value.length());
	            StringTokenizer st = new StringTokenizer(value, "\"", true);
	            while (st.hasMoreTokens()) {
	              String token = st.nextToken();
	              escapedString.append(token);
	              if (token.equals("\"")) {
	                escapedString.append("\"");
	              }
	            }
	          }
	          case SMIConstants.SYNTAX_IPADDRESS:
	          case SMIConstants.SYNTAX_OBJECT_IDENTIFIER:
	          case SMIConstants.SYNTAX_OPAQUE: {
	            System.out.print("\"");
	            System.out.print(value);
	            System.out.print("\"");
	            break;
	          }
	          default: {
	            System.out.print(value);
	          }
	        }
	        if (i+1 < event.getColumns().length) {
	          System.out.print(",");
	        }
	      }
	      System.out.println();
	      return true;
	    }

	    public void finished(TableEvent event) {
	      finished = true;
	      synchronized (event.getUserObject()) {
	        event.getUserObject().notify();
	      }
	    }

	    public boolean isFinished() {
	      return finished;
	    }

	  }
	private void addUsmUser(Snmp snmp) {
	    snmp.getUSM().addUser(securityName, new UsmUser(securityName,
	                                                    authProtocol,
	                                                    authPassphrase,
	                                                    privProtocol,
	                                                    privPassphrase));
	  }
}
