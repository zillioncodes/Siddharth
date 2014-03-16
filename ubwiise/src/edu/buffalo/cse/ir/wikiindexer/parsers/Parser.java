/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.parsers;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Collection;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import edu.buffalo.cse.ir.wikiindexer.wikipedia.WikipediaDocument;
import edu.buffalo.cse.ir.wikiindexer.wikipedia.WikipediaParser;

/**
 * @author nikhillo
 *
 */
public class Parser {

	/* */
	private final Properties props;

	// No of useful xml tags in the document for initializing
	private final int NO_OF_TAGS = 7;

	private static final String ERROR1 = "DUMP FILE IS EMPTY OR IS NOT AVAILABLE";
	private static final String ERROR2 = "INVALID FILE NAME";

	private enum WikiXmlTags {
		PAGE, // Signifies page tag
		TITLE, // Title
		ID, // Document Id
		USERNAME, // Author
		IP, // Author
		TIMESTAMP, // Publish Date
		TEXT // Text Content
	}

	// Marker for tags
	private boolean[] tagMarker = new boolean[NO_OF_TAGS];

	// String Buffer for storing the TEXT content
	StringBuffer sb;

	// Ignoring other ID tags such as Parent Id , revision Id etc
	private boolean isIgnoreOtherIds;

	/**
	 * 
	 * @param idxConfig
	 * @param parser
	 */
	public Parser(Properties idxProps) {
		props = idxProps;
	}
	
	/* TODO: Implement this method */
	/**
	 * 
	 * @param filename
	 * @param docs
	 */
	public void parse(String filename, Collection<WikipediaDocument> docs) {
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		// saxParserFactory.setNamespaceAware(true);
		try {
			if (null == filename || "".equals(filename)) {
				System.out.println(ERROR1);
				return;
			}
			File xmlDump = new File(filename);
			if (null == xmlDump || !xmlDump.exists()) {
				System.out.println(ERROR2);
				return;
			}
			// Create instance of SAXParser from SAXParserFactory
			SAXParser saxParser = saxParserFactory.newSAXParser();
			// Parser Helper class which overrides the call back methods
			DefaultHandler handler = new WikiSAXHandler(docs);
			saxParser.parse(filename, handler);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/*
	 * Default Handler class with overridden Call back events for SAXParser
	 */
	class WikiSAXHandler extends DefaultHandler {
		int idFromXml;
		String timestampFromXml = null;
		String authorFromXml = null;
		String ttl = null;
		Collection<WikipediaDocument> docs = null;

		private WikiSAXHandler(Collection<WikipediaDocument> docs) {
			this.docs = docs;
		}

		/**
		 * Overridden methods of the DefaultHandler. Call back methods for event
		 * processing such as starting and ending of tag.
		 */
		@Override
		public void startDocument() throws SAXException {
			// TODO Auto-generated method stub
			super.startDocument();
		}

		@Override
		public void endDocument() throws SAXException {
			// TODO Auto-generated method stub
			super.endDocument();
		}

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {

			if (qName.equalsIgnoreCase(WikiXmlTags.PAGE.toString())) {
				tagMarker[WikiXmlTags.PAGE.ordinal()] = true;
			} else if (qName.equalsIgnoreCase(WikiXmlTags.TITLE.toString())) {
				tagMarker[WikiXmlTags.TITLE.ordinal()] = true;
			} else if (qName.equalsIgnoreCase(WikiXmlTags.ID.toString())) {
				if (!isIgnoreOtherIds) {
					tagMarker[WikiXmlTags.ID.ordinal()] = true;
					isIgnoreOtherIds = true;
				}
			} else if (qName.equalsIgnoreCase(WikiXmlTags.USERNAME.toString())) {
				tagMarker[WikiXmlTags.USERNAME.ordinal()] = true;
			} else if (qName.equalsIgnoreCase(WikiXmlTags.IP.toString())) {
				tagMarker[WikiXmlTags.IP.ordinal()] = true;
			} else if (qName.equalsIgnoreCase(WikiXmlTags.TIMESTAMP.toString())) {
				tagMarker[WikiXmlTags.TIMESTAMP.ordinal()] = true;
			} else if (qName.equalsIgnoreCase(WikiXmlTags.TEXT.toString())) {
				tagMarker[WikiXmlTags.TEXT.ordinal()] = true;
				sb = new StringBuffer();
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {

			if (qName.equalsIgnoreCase(WikiXmlTags.PAGE.toString())) {
				tagMarker[WikiXmlTags.PAGE.ordinal()] = false;
				/*
				 * Set the value as false after each document. This is done so
				 * that the ID is not skipped in the next document.
				 */
				isIgnoreOtherIds = false;

				/*
				 * Create new WikiPediaDocument instance. The instantiation is
				 * delayed till the </page> tag is encountered
				 */
				WikipediaDocument wikiDoc = null;
				WikipediaParser wikipediaParser = null;
				try {
					wikiDoc = new WikipediaDocument(idFromXml,
							timestampFromXml, authorFromXml, ttl);
					wikipediaParser = new WikipediaParser();
					wikipediaParser.initWikiParse(wikiDoc, sb.toString());
					add(wikiDoc, docs);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					wikiDoc = null;
					wikipediaParser = null;
				}

				// De-initialize the variables and references before next
				// iteration.
				clearParserState();

			} else if (qName.equalsIgnoreCase(WikiXmlTags.TEXT.toString())) {
				tagMarker[WikiXmlTags.TEXT.ordinal()] = false;
			}
		}

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {

			if (tagMarker[WikiXmlTags.TITLE.ordinal()]) {
				ttl = new String(ch, start, length);
				tagMarker[WikiXmlTags.TITLE.ordinal()] = false;
			}
			if (tagMarker[WikiXmlTags.ID.ordinal()]) {
				idFromXml = Integer.parseInt(new String(ch, start, length));
				tagMarker[WikiXmlTags.ID.ordinal()] = false;
			}
			if (tagMarker[WikiXmlTags.USERNAME.ordinal()]) {
				authorFromXml = new String(ch, start, length);
				tagMarker[WikiXmlTags.USERNAME.ordinal()] = false;
			}
			if (tagMarker[WikiXmlTags.IP.ordinal()]) {
				authorFromXml = new String(ch, start, length);
				tagMarker[WikiXmlTags.IP.ordinal()] = false;
			}
			if (tagMarker[WikiXmlTags.TIMESTAMP.ordinal()]) {
				timestampFromXml = new String(ch, start, length);
				tagMarker[WikiXmlTags.TIMESTAMP.ordinal()] = false;
			}
			if (tagMarker[WikiXmlTags.TEXT.ordinal()]) {
				sb.append(new String(ch, start, length));
			}

		}

		private void clearParserState() {
			sb = null;
			idFromXml = 0;
			authorFromXml = null;
			timestampFromXml = null;
			ttl = null;
		}

	};

	/**
	 * Method to add the given document to the collection.
	 * PLEASE USE THIS METHOD TO POPULATE THE COLLECTION AS YOU PARSE DOCUMENTS
	 * For better performance, add the document to the collection only after
	 * you have completely populated it, i.e., parsing is complete for that document.
	 * @param doc: The WikipediaDocument to be added
	 * @param documents: The collection of WikipediaDocuments to be added to
	 */
	private synchronized void add(WikipediaDocument doc, Collection<WikipediaDocument> documents) {
		documents.add(doc);
	}
}
