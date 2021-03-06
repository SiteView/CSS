/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.csstudio.opibuilder.converter.model.EdmAttribute;
import org.csstudio.opibuilder.converter.model.EdmEntity;
import org.csstudio.opibuilder.converter.model.EdmException;


/**
 * Parser class which parses data on EdmColors.
 * 
 * @author Matevz
 *
 */
public class EdmColorsListParser extends EdmParser {

	static Logger log = Logger.getLogger("org.csstudio.opibuilder.converter.parser.EdmColorsListParser");

	/**
	 * Parses data in given file name into constructed EdmColorsListParser instance.
	 * @param fileName Edm colors list file.
	 * @throws EdmException if parsing error occurs.
	 */
	public EdmColorsListParser(String fileName) throws EdmException {
		super(fileName);
		
		String data = edmData.toString();
		parseStaticColors(getRoot(), data);
		parseMenuMap(getRoot(), data);
	}

	/**
	 * Parses static colors to EdmAttributes on parent.
	 * 
	 * @param parent EdmEntity which will contain EdmColorsList data.
	 * @param data Data containing EdmColors.
	 * @throws EdmException if there is an invalid format given. If parsing is robust,
	 * 		no exception is thrown, but erroneous color definition is ignored.
	 */
	private void parseStaticColors(EdmEntity parent, String data) throws EdmException  {

		Pattern p = Pattern.compile("static (.*)");	// get each line that starts with "static"
		Matcher m = p.matcher(data);

		while (m.find()) {
			String colorData = m.group(1);
			log.debug("Parsing color from data: " + m.group());

			Pattern p1 = Pattern.compile("\\S+");  // each word
			Matcher m1 = p1.matcher(colorData);

			/*
			 * Parses each word in line. First is attribute name (index),
			 * then font name and then triple integers for primary color
			 * and optional another three integers for blinking color.
			 */
			EdmAttribute a = new EdmAttribute();
			String colorName = "";
			boolean error = false;
			int i = 1;
			while (m1.find()) {
				String word = m1.group();

				if (i == 1)
					colorName = word;
				else if (i == 2) { // second word --> color name

					if (!word.startsWith("\"")) { // must start with quote
						if (robust)
							error = true;
						else
							throw new EdmException(EdmException.STRING_FORMAT_ERROR,
									"String value does not start with quote at line: " + colorData);
					}
						
					else {
						StringBuilder nameValue = new StringBuilder(word);
						if (!nameValue.toString().endsWith("\"")) { // when there are more words in name
							try {
								do {	// iterates until there is final quote found
									m1.find();
									word = m1.group().trim();

									nameValue.append(" " + word);
								} while (!word.endsWith("\""));
							}
							catch (Exception e) {
								if (robust)
									error = true;
								else 
									throw new EdmException(EdmException.STRING_FORMAT_ERROR,
											"String value does not end with quote at line: " + colorData);
							}
						}
						a.appendValue(nameValue.toString());

					}
				}
				else if (Pattern.matches("^\\d*$", word)) {	// if word is a number

					StringBuilder colorValue = new StringBuilder(word);
					for (int c = 2; c <= 3; c++) {
						m1.find();
						word = m1.group();
						if (Pattern.matches("^\\d*$", word))
							colorValue.append(" " + word);
						else {
							if (robust)
								error = true;
							else
								throw new EdmException(EdmException.COLOR_FORMAT_ERROR,
										"Wrong color input at line: " + colorData);
						}
					}
					a.appendValue(colorValue.toString());
				}
				i = i + 1;
				
			}

			if (!robust)
				parent.addAttribute(colorName, a);
			else {
				if (!error) {
					try {
						parent.addAttribute(colorName, a);
					}
					catch (Exception e) {
						log.error("Error when parsing color attribute. Attribute skipped.");
					}	
				}
				else 
					log.error("Error when parsing color attribute. Attribute skipped.");
			}
		}
	}

	/**
	 * Parses menumap order of colors into ordered EdmEntity objects on parent.
	 * 
	 * @param parent EdmEntity which will contain data.
	 * @param data Data containing EdmColors.
	 * @throws EdmException if there is an invalid format given. If parsing is robust,
	 * 		no exception is thrown, but erroneous color definition is ignored.
	 */
	private void parseMenuMap(EdmEntity parent, String data) throws EdmException  {

		Pattern p = Pattern.compile("menumap\\s*[{](.*)[}]", Pattern.DOTALL);	// get menumap content
		Matcher m = p.matcher(data);
		
		if (m.find()) {
			p = Pattern.compile("\\s*[\"](.*)[\"]");	// get menumap content
			Matcher nameMatcher = p.matcher(m.group(1));
			while (nameMatcher.find()) {
				parent.addSubEntity(new EdmEntity(nameMatcher.group(1)));
			}
		} else {
			log.warn("Warning: no menumap found in colors definition file.");
		}
	}
}
