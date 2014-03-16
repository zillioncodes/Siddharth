/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.wikipedia;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author nikhillo This class implements Wikipedia markup processing. Wikipedia
 *         markup details are presented here:
 *         http://en.wikipedia.org/wiki/Help:Wiki_markup It is expected that all
 *         methods marked "todo" will be implemented by students. All methods
 *         are static as the class is not expected to maintain any state.
 */
public class WikipediaParser {

	/* TODO */
	/**
	 * Method to parse section titles or headings. Refer:
	 * http://en.wikipedia.org/wiki/Help:Wiki_markup#Sections
	 * 
	 * @param titleStr
	 *            : The string to be parsed
	 * @return The parsed string with the markup removed
	 */
	public static String parseSectionTitle(String titleStr) {
		if (null == titleStr || titleStr.isEmpty()) {
			return titleStr;
		}
		Pattern pattern = IParserRegex.sectionTitlePattern;
		Matcher match = pattern.matcher(titleStr);
		if (null != match && match.find()) {
			titleStr = match.replaceAll("$1$2");
		}
		return titleStr;
	}

	/* TODO */
	/**
	 * Method to parse list items (ordered, unordered and definition lists).
	 * Refer: http://en.wikipedia.org/wiki/Help:Wiki_markup#Lists
	 * 
	 * @param itemText
	 *            : The string to be parsed
	 * @return The parsed string with markup removed
	 */
	public static String parseListItem(String itemText) {
		if (null == itemText || itemText.isEmpty()) {
			return itemText;
		}
		/*
		 * Pattern pattern = IParserRegex.listPattern; Matcher match =
		 * pattern.matcher(itemText); if (null != match) { itemText =
		 * match.replaceAll("$1$3"); }
		 */
		StringBuilder parsedText = new StringBuilder("");
		String[] itemTexts = itemText.split("\n");
		for (String item : itemTexts) {
			if (null != item && !item.isEmpty()) {
				if (item.startsWith("*")) {
					item = item.replaceAll("^\\*+\\s*", "").trim();
				} else if (item.startsWith("#")) {
					item = item.replaceAll("^\\#+\\s*", "").trim();
				} else if (item.startsWith(";")) {
					item = item.replaceAll(";+\\s*", "");
					if (item.contains(":")) {
						item = item.substring(0, item.indexOf(":")).trim()
								+ "\n"
								+ item.substring(item.indexOf(":") + 1).trim();
					}
				} else if (item.startsWith(":")) {
					item = item.replaceAll("^\\:+\\s*", "").trim();
				}
			}
			parsedText.append(item).append("\n");
		}

		itemText = parsedText.substring(0, parsedText.length() - 1);
		return itemText;
	}

	/* TODO */
	/**
	 * Method to parse text formatting: bold and italics. Refer:
	 * http://en.wikipedia.org/wiki/Help:Wiki_markup#Text_formatting first point
	 * 
	 * @param text
	 *            : The text to be parsed
	 * @return The parsed text with the markup removed
	 */
	public static String parseTextFormatting(String text) {
		if (null == text || text.isEmpty()) {
			return text;
		}

		text = text.replaceAll("'''''", "");
		text = text.replaceAll("'''", "");
		text = text.replaceAll("''", "");
		return text;
	}

	/* TODO */
	/**
	 * Method to parse *any* HTML style tags like: <xyz ...> </xyz> For most
	 * cases, simply removing the tags should work.
	 * 
	 * @param text
	 *            : The text to be parsed
	 * @return The parsed text with the markup removed.
	 */
	public static String parseTagFormatting(String text) {

		if (null == text || text.isEmpty()) {
			return text;
		}

		// Whether to handle &quot;, &amp; etc since SAX Parser will
		// automatically convert these
		Pattern[] patterns = IParserRegex.tagPatterns;

		int i = 0;
		Matcher match = null;
		while (i < patterns.length) {
			match = patterns[i].matcher(text);
			if (null != match) {
				text = match.replaceAll("");
			}
			i++;
		}
		text = text.trim();
		text = text.replaceAll("  ", " ");
		text = text.replaceAll(" \\.", "\\.");
		return text;
	}

	/* TODO */
	/**
	 * Method to parse wikipedia templates. These are *any* {{xyz}} tags For
	 * most cases, simply removing the tags should work.
	 * 
	 * @param text
	 *            : The text to be parsed
	 * @return The parsed text with the markup removed
	 */
	public static String parseTemplates(String text) {
		if (null == text || text.isEmpty()) {
			return text;
		}
		Pattern pattern = IParserRegex.templatePattern;
		int i = 0;
		Matcher match = pattern.matcher(text);
		while (i < 3) {
			if (null != match) {
				text = match.replaceAll("");
			}
			match.reset(text);
			i++;
		}
		
		pattern = IParserRegex.templateImagePattern;
		match.reset();
		match = pattern.matcher(text);
		if (null != match) {
			text = match.replaceAll("");
		}

		return text;
	}

	/* TODO */
	/**
	 * Method to parse links and URLs. Refer:
	 * http://en.wikipedia.org/wiki/Help:Wiki_markup#Links_and_URLs
	 * 
	 * @param text
	 *            : The text to be parsed
	 * @return An array containing two elements as follows - The 0th element is
	 *         the parsed text as visible to the user on the page The 1st
	 *         element is the link url
	 */
	public static String[] parseLinks(String text) {
		text = parseTextFormatting(text);
		text = parseTagFormatting(text);
		text = parseTemplates(text);
		text = parseListItem(text);
		ArrayList<String> linkList = new ArrayList<String>();
		String[] link = new String[2];
		link[0] = link[1] = "";
		String subSequence = null;
		if (null == text || text.isEmpty()) {
			return link;
		}
		linkList.add(stripLinkMarkup(text));
		Pattern pattern = IParserRegex.linkUrlPattern;
		Matcher match = pattern.matcher(text);
		if (null != match) {
			while (match.find()) {
				subSequence = match.group(1);
				
				if (null != subSequence) {
					if (subSequence.contains("|")) {
						link = processRenamedLinks(subSequence.trim());
					} else {
						link[0] = subSequence;
						if (text.contains(":")) {
							link[1] = "";
						} else {
							link[1] = transformWikiURL(subSequence);
						}
					}
					linkList.add(link[1]);
				}
				subSequence = null;
			}
		}
		pattern = IParserRegex.linkPatterns[IParserRegex.linkPatterns.length - 2];
		match.reset();
		match = pattern.matcher(text);
		if (null != match) {
			while (match.find()) {
				linkList.add("");
			}
		}
		String[] strinList = new String[linkList.size()];
		return linkList.toArray(strinList);
	}

	/**
	 * Method to process Rename links which contains a seperator '|'
	 * 
	 * @param text
	 * @return An array containing two elements as follows - The 0th element is
	 *         the parsed text as visible to the user on the page The 1st
	 *         element is the link url
	 */
	private static String[] processRenamedLinks(String text) {
		String[] links = new String[2];
		String url = "";
		boolean isExternalNS = false;
		if (text.indexOf("|") == text.length() - 1) {
			if (text.contains(":")) {
				text = text.substring(text.indexOf(":") + 1, text.indexOf("|"));
				isExternalNS = true;
			}
			if (text.contains("(") && text.contains(")")) {
				url = text;
				text = text.substring(0, text.indexOf("("));
			}
			if (text.contains(",")) {
				url = text;
				text = text.substring(0, text.indexOf(","));
			}
			if (url.indexOf("|") != -1) {
				url = url.substring(0, url.indexOf("|"));
			}

			links[0] = text;
			if (isExternalNS) {
				links[1] = "";
			} else {
				links[1] = transformWikiURL(url);
			}

		} else {
			links = text.split("\\|");
			String temp = links[1];
			if (text.contains(":")) {
				links[1] = "";
				links[0] = text.substring(text.lastIndexOf("|") + 1);
			} else {
				links[1] = transformWikiURL(links[0]);
			}
			links[0] = temp;
		}
		return links;
	}

	/**
	 * Method to transform wikiURLs to a complete URL
	 * 
	 * @param text
	 * @return complete original URL which links to another wiki page
	 */
	private static String transformWikiURL(String wikiURL) {
		if (null != wikiURL && !wikiURL.isEmpty()) {
			wikiURL = wikiURL.replaceAll("\\s", "_");
			wikiURL = Character.toUpperCase(wikiURL.charAt(0))
					+ wikiURL.substring(1);
		} else {
			wikiURL = "";
		}
		return wikiURL;
	}

	/**
	 * Method which actually strips the markup from the text content
	 * 
	 * @param text
	 * @return string with markup removed
	 */
	public static String stripLinkMarkup(String text) {
		if (null == text || text.isEmpty()) {
			return text;
		}
		Pattern[] patterns = IParserRegex.linkPatterns;
		int i = 0;
		Matcher match = patterns[i].matcher(text);
		while (i < patterns.length) {
			match.usePattern(patterns[i]);
			if (null != match) {
				text = match.replaceAll("$3");
			}
			match.reset(text);
			i++;
		}
		return text;
	}

	/**
	 * Method which actually strips the markup from the text content
	 * 
	 * @param text
	 * @return string with markup removed
	 */
	public static WikipediaDocument splitSections(WikipediaDocument wikiDoc,
			String text) {
		String title = "Default";

		// Collect Category for each document
		text = collectCategory(wikiDoc, text);
		// Parse markup
		String[] linkList = parseLinks(text);
		for (int i = 1; i < linkList.length; i++) {
			if(null != linkList[i] && !linkList[i].isEmpty())
				wikiDoc.addLink(linkList[i]);
		}
		text = linkList[0];
		String[] splitText = text.split("\n");

		StringBuilder eachSection = new StringBuilder();

		for (String str : splitText) {
			if (null != str && !str.isEmpty()) {
				
				if (str.startsWith("=") && str.endsWith("=")) {
					wikiDoc.addSection(title, eachSection.toString());
					title = parseSectionTitle(str);
					eachSection = new StringBuilder();
				} else {
					eachSection.append(str).append("\n");
				}
			}
		}
		
		wikiDoc.addSection(title, eachSection.toString());
		return wikiDoc;
	}

	private static String collectCategory(WikipediaDocument wikiDoc, String text) {
		Pattern pattern = IParserRegex.categoryPattern;
		List<String> categories = new ArrayList<String>();
		String tempCategory = "";
		Matcher match = pattern.matcher(text);
		if (null != match) {
			while (match.find()) {
				tempCategory = match.group(3);
				categories.add(tempCategory);
			}
		}
		text = text.replaceAll(pattern.pattern(), "");
		wikiDoc.addCategories(categories);
		return text;
	}

	/**
	 * Checks for the valid strings in the section text
	 * 
	 * @param str
	 * @return returns whether the string can be stored on the section text or
	 *         not
	 */
	private static boolean isCategory(String str) {
		if (null != str && !str.isEmpty() && !str.equals("")
				&& str.toLowerCase().contains("category:")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Method to invoke static methods in WikipediaParser
	 * 
	 * @param doc
	 *            WikipediaDocument instance passed from Parser class in order
	 *            to access the protected methods of WikipediaDocument
	 * @param text
	 */
	public WikipediaDocument initWikiParse(WikipediaDocument doc, String text) {
		

		/*
		 * String[] linkList = parseLinks(intermediateStr);
		 * 
		 * for (int i = 1; i < linkList.length; i++) { doc.addLink(linkList[i]);
		 * } intermediateStr = linkList[0];
		 */
		/*
		 * intermediateStr = "==Default==\n"; Pattern pattern =
		 * IParserRegex.sectionPattern; Matcher match =
		 * pattern.matcher(intermediateStr); if (null != match) {
		 * while(match.find()) { intermediateStr =
		 * parseSectionTitle(intermediateStr); doc.addSection(match.group(1),
		 * match.group(3)); } }
		 */
		doc = splitSections(doc, text);
		return doc;
	}

}
