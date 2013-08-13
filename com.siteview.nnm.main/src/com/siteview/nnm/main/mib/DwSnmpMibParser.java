package com.siteview.nnm.main.mib;

import java.io.*;
/**
 * mib文件转换成树节点
 * @author haiming.wang
 *
 */
public class DwSnmpMibParser 
{
	
	String fileName;
	MenuTreeRecord currentRec;
	String prevToken,tokenVal="xx",tokenVal2="";
	PipedReader pr;
	PipedWriter pw;
	BufferedReader r;
	FileReader inFile;
	public static int TT_EOL=StreamTokenizer.TT_EOL;
	DwSnmpMibOutputHandler output;
	DwMibParserInterface tokens;
	
	
	DwSnmpMibParser(String fileName,DwMibParserInterface intf)		
	{
		this.fileName = fileName;	
		this.tokens=intf;
	}				
	
	
	int parseMibFile()	
	{
		
		StreamTokenizer st;
		
		try {
			inFile= new FileReader (new File(fileName));
			r = new BufferedReader(inFile);			
			st = new StreamTokenizer(r);				
		}
		catch (Exception e) {
			System.out.println("File open error : Cannot open file.\n" + e.toString());
			return -1;
		}
		
		System.out.println("\t**************************\t" + fileName);
		st.resetSyntax();		
		st.eolIsSignificant(true);
		st.wordChars(33,126);
		
		String t1="a";
		int parseStatus=0;
		int parseStatusTemp=0;
		
		try {
			while (getNextToken(st).trim().length() > 0 || st.ttype == TT_EOL) {
				t1=getTokenVal(st);
				switch (parseStatus) {
				case 0: { 
						currentRec=new MenuTreeRecord();
						currentRec.init();
						currentRec.type = "mib";
						if(t1.indexOf("IMPORT")!=-1) { // Skip till ;
							parseStatus =100;
						}
						else						
						if(t1.equals("MODULE-IDENTITY")) {
							currentRec.name = prevToken;
							parseStatus =1;
						}
						else						
						if(t1.equals("OBJECT-TYPE")) {
							String temp=new String (prevToken.trim());
							if (!temp.isEmpty()) {
								temp = temp.substring(0, 1);
								if (temp.toLowerCase().equals(temp)) {
									parseStatus = 1;
									currentRec.name = prevToken;
								}
							}
							//System.out.print("*"+prevToken);
						}
						else if(t1.indexOf("OBJECT-GROUP")!=-1) { // Skip till ;
							String temp=new String (prevToken.trim());
							temp=temp.substring(0,1);
							if (temp.toLowerCase().equals(temp)) {
								parseStatus=1;
								currentRec.name=prevToken;		
							}
						}												
						else if(t1.equals("OBJECT")) {
							currentRec.name=prevToken;							
							parseStatus=2;
						}						
						else if(t1.equals("::="))
						{
							
							currentRec.name=prevToken;
							parseStatus=9; // Its a variable
						}					
						continue;
					}						
					
						
				case 1:	{        // GET " ::= " Token
						if(t1.equals("::=") )
							parseStatus=3;
						else if(t1.equals("SYNTAX") )
							parseStatus=5;
						else if(t1.indexOf("ACCESS")!= -1 )
							parseStatus=6;
						else if(t1.equals("STATUS") )
							parseStatus=7;
						else if(t1.equals("DESCRIPTION") )
							parseStatus=8;	
						else if(t1.equals("INDEX") )
							parseStatus=11;	
						else if(t1.equals("OBJECTS") )
							parseStatus=14;						
						continue;
					}						
						
				case 2:	{		// GET "IDENTIFIER "  else reset
						if(t1.equals("IDENTIFIER")) {
							parseStatus = 1; // GET ::= next
						}
						else {
							parseStatus=0;							
						}
						continue;
					}
						
				case 3: {		// get Group Name
						if(t1.trim().startsWith("{") || t1.trim().length()==0) continue;
						currentRec.parent=t1;
						parseStatus =4;  // next=GET Number.
						continue;				
					}
						
				case 4:	{		// Get sub-Group number
						try {							
							if(t1.trim().endsWith(")")) { // for chained server entries
									String numStr="";
									MenuTreeRecord newRec=new MenuTreeRecord();							
									numStr=t1.substring(t1.indexOf((int)'(')+1,t1.indexOf((int)')'));
									//System.out.println("Adding T1: " + t1 + "  Number Str : " + numStr);
								try {
									newRec.number =Integer.parseInt(numStr.trim());
								}
								catch(Exception ne2) {
									System.out.println("Error in line " + st.lineno()); 
									continue;
								}
								newRec.name = t1.substring(0, t1.indexOf("("));
								newRec.parent = currentRec.parent;
								currentRec.parent = newRec.name;
								addToken(newRec);
							continue;
							}							
							currentRec.number = Integer.parseInt(t1.trim());
							//排除mib节点。使用mib-2节点
							if(!currentRec.parent.equals("mib") && !currentRec.name.equals("mib"))
								addToken(currentRec);
							parseStatus=0;
							continue;			
						}
						catch(NumberFormatException ne) { 
							System.out.println("Error in getting number.."+t1+"\n" + ne.toString());						
						}
					}					
					
				case 5: {		
						if(t1.indexOf((int)'{') != -1) {
							parseStatus =12;
							currentRec.syntax =currentRec.syntax.concat(" " +t1);
							continue;
						}
								
						if (st.ttype==TT_EOL || st.ttype==st.TT_EOF) {
							currentRec.syntax =currentRec.syntax.concat(t1);						
							if(parseStatusTemp==1 ) {
								if(currentRec.syntax.indexOf('{') != -1){
									parseStatus=12;
									continue;
								}
								// See if it is a table. if so, set recordtype to 1
								if(currentRec.syntax.trim().startsWith("SEQUENCE") ) {
									currentRec.recordType=1; 
									currentRec.tableEntry=1;
								}
								parseStatus =1;
								parseStatusTemp =0;
							}							
							continue;
						}						
						currentRec.syntax = currentRec.syntax.concat(" " + t1);
						if(currentRec.syntax.trim().length()>0) parseStatusTemp=1;
						continue;
							
							
					}
				case 6: {		// Get Access Mode Data till EOL
						if (st.ttype==TT_EOL) {
							parseStatus=1;
							continue;
						}
						currentRec.access = currentRec.access.concat(" " + t1);										
						continue;

					}
				case 7: {		// Get Status data till EOL
						if (st.ttype==TT_EOL) {
							parseStatus=1;
							continue;
						}
						currentRec.status = currentRec.status.concat(" " + t1);
						continue;

					}
						
				case 8: {		// Get Description till EOL
						if (st.ttype==st.TT_EOF) {
							break;
						}							
						currentRec.description = currentRec.description.concat(" " + t1 );					
						if(t1.trim().length()!=0) parseStatus=1;
						continue;
					}
				case 9: {		// Record is a variable
						currentRec.recordType = currentRec.recVariable;	
						if(t1.indexOf((int)'{') != -1) {
							parseStatus =10;
							currentRec.syntax =currentRec.syntax.concat(" " + t1);
							continue;
						}
					
						if (st.ttype==TT_EOL || st.ttype==st.TT_EOF) {
							currentRec.syntax =currentRec.syntax.concat(t1);
							if(parseStatusTemp==1 ) {
								if(currentRec.syntax.indexOf('{') != -1){
									parseStatus=10;
									continue;
								}
								// See if it is a table. if so, set recordtype to 1
								if(currentRec.syntax.trim().startsWith("SEQUENCE") ) {
									currentRec.recordType=1; 
								}
								
								addToken(currentRec);
								parseStatus=0;
								parseStatusTemp =0;
							}							
							continue;
						}
						currentRec.syntax = currentRec.syntax.concat(" " + t1);
						if(currentRec.syntax.trim().length()>0) parseStatusTemp=1;
						continue;
					}
				case 10: {			// Variable Data in { } 
				 		 currentRec.syntax=currentRec.syntax.concat(t1);				
						 if(t1.indexOf((int)'}') != -1) {
							 parseStatus =0;
							 parseStatusTemp =0;
							// See if it is a table. if so, set recordtype to 1
							 if(currentRec.syntax.trim().startsWith("SEQUENCE")) {
								 currentRec.recordType=1; 
							 }							
							 addToken(currentRec);
							 //System.out.println(currentRec.name +"  " + currentRec.syntax );
							 continue;
						 }
						 continue;
					}
						 
				case 11: {			// INDEX (For tables)
							 
						if(t1.trim().startsWith("{")) continue;
						if(t1.indexOf((int)'}') != -1) {
							parseStatus = 1;  
							continue;
						}
						currentRec.index=currentRec.index.concat(t1);												
						continue;
					}
						 
						 
				case 12: {
 						currentRec.syntax=currentRec.syntax.concat(t1);
						if(t1.indexOf((int)'}') != -1) {
							parseStatus =1;
							parseStatusTemp =0;
							
							// See if it is a table. if so, set recordtype to 1
							if(currentRec.syntax.trim().startsWith("SEQUENCE")) {
								 currentRec.recordType=1; 
								 currentRec.tableEntry=1;
							}
						}		
						 //if(t1.indexOf((int)'}') != -1)
						continue;
					}
				// case 13: Not used because unlucky :(
				case 14 : {   // OBJECT-GROUP OBJECTS...
						currentRec.syntax=currentRec.syntax.concat(t1);
						if(t1.indexOf((int)'}') != -1) {
							parseStatus =1;
						}		
						 //if(t1.indexOf((int)'}') != -1)
						continue;
					}						 
				case 100: {
					    if(t1.indexOf(';')!= -1) parseStatus =0;
					}
				case 101: {
						if(t1.indexOf('}')!= -1) parseStatus =0;
					}
				
				}
			}			
		}	
		catch (Exception e)	{
			e.printStackTrace();
			System.out.println("Error in parsing.. \n" + e.toString());
		}		
		return 0;
	}	
	
	
	// returns the next non blank token
	String getNextToken(StreamTokenizer st)
	{
		String tok="";	
		prevToken=getTokenVal(st);
		
		while(tok.equals(""))
		{		
			try	{				
				if(!tokenVal.equals("xx")) return(tokenVal);
				if(!tokenVal2.equals("")) {
					setTokenVal(tokenVal2);
					tokenVal2="";
					return tokenVal;
				}
				if(st.nextToken()!= StreamTokenizer.TT_EOF) {
					if(st.ttype==TT_EOL) return getTokenVal(st);
					if(st.ttype==StreamTokenizer.TT_WORD  )  {
						tok=st.sval;
						// if { is combined with something, seperate them
						if(tok.startsWith("{")) {
							if(tok.trim().length() !=1) {
								setTokenVal("{");
								tokenVal2=new String(tok.substring(1));
								return ("{");						
							}
						}
						if(tok.endsWith("}")) {
							if(tok.trim().length() !=1) {
								setTokenVal(tok.replace('}',' '));
								tokenVal2="}";
								return tok.replace('}',' ');						
							}
						}
						
						// Get "Quoted Text" as whole tokens :)
						if(tok.startsWith("\"")) {
							String strQuote =new String(tok);
							st.nextToken();
							tok=getTokenVal(st);
							while(tok!=null &&  tok.indexOf('"')==-1 ) {
								String temp=getTokenVal(st);
								if(temp.trim().length()>0) strQuote=strQuote.concat(" "+temp);
								if(st.nextToken()==st.TT_EOF) return tok;
								tok=getTokenVal(st);
							}
							strQuote=strQuote.concat(getTokenVal(st));
							if(strQuote.trim().length() > 0) tokenVal =strQuote;
						}
							
						if(tok.equals("--")) {
							while(st.ttype != st.TT_EOL )
								st.nextToken();							
							break;
						}				
						
						if(st.ttype == TT_EOL) return(" "); //st.ttype ;
						else
							continue;
					}
					else if(st.ttype == StreamTokenizer.TT_NUMBER ) {
						tok=String.valueOf(st.nval ); 
						if(tok.trim().length()>0) {
							return tok;
						}
						else
							continue;
					}
					else tok="";
				}
				else return "";				
			}
			catch (Exception e) {
				if(e.getMessage().startsWith("Write end dead") != true)
					System.out.println("Error in reading file..." + e.toString());			
				return "";
			}		
		}	
		return tok;	
	}
	
	void setTokenVal(String t) {
		tokenVal=t;
	}
					 
	String getTokenVal(StreamTokenizer st)
	{
		try {		
			if(tokenVal != "xx") {
				String temp=tokenVal.toString();
				tokenVal="xx";
				return temp;
			}
			if(st.ttype == TT_EOL) return String.valueOf('\n');
			if(st.ttype==StreamTokenizer.TT_WORD  )  
				return(st.sval);
			else if(st.ttype == StreamTokenizer.TT_NUMBER )			
				return(String.valueOf ((int)st.nval) ); 
			else return ("");
		}
		catch(Exception e)	{
			System.out.println("Error in retrieving token value..\n" + e.toString());
			
		}
		return("");
	}
					  
	
	void setOutput(DwSnmpMibOutputHandler output) {
		this.output=output;
	}
	
	
	void addToken(MenuTreeRecord rec) {
		tokens.newMibParseToken(rec);
	}
/*	
	public static void main(String mainArgs[])
	{
		if(mainArgs.length ==0 ) return;
		DwSnmpMibParser sp=new DwSnmpMibParser(mainArgs[0]);
		sp.parseMibFile();
	}
	*/
}
