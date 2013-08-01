package com.siteview.nnm.main.mib;

public interface DwMibParserInterface
{
	void newMibParseToken(MenuTreeRecord rec);
	void parseMibError(String s);
}
