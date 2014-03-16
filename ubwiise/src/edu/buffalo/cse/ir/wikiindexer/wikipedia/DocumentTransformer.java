/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.wikipedia;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import edu.buffalo.cse.ir.wikiindexer.indexer.INDEXFIELD;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.Tokenizer;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;

/**
 * A Callable document transformer that converts the given WikipediaDocument
 * object into an IndexableDocument object using the given Tokenizer
 * 
 * @author nikhillo
 * 
 */
public class DocumentTransformer implements Callable<IndexableDocument> {

	Map<INDEXFIELD, Tokenizer> tokenizerMap;
	WikipediaDocument doc;

	/**
	 * Default constructor, DO NOT change
	 * 
	 * @param tknizerMap
	 *            : A map mapping a fully initialized tokenizer to a given field
	 *            type
	 * @param doc
	 *            : The WikipediaDocument to be processed
	 */
	public DocumentTransformer(Map<INDEXFIELD, Tokenizer> tknizerMap,
			WikipediaDocument doc) {
		this.tokenizerMap = tknizerMap;
		this.doc = doc;
	}

	/**
	 * Method to trigger the transformation
	 * 
	 * @throws TokenizerException
	 *             Inc ase any tokenization error occurs
	 */
	public IndexableDocument call() throws TokenizerException {
		IndexableDocument indexableDoc = new IndexableDocument(doc.getTitle());
		TokenStream tokenStream = new TokenStream(doc.getAuthor());

		tokenizerMap.get(INDEXFIELD.AUTHOR).tokenize(tokenStream);
		indexableDoc.addField(INDEXFIELD.AUTHOR, tokenStream);

		List<String> catList = doc.getCategories();
		String[] strinList = new String[catList.size()];
		tokenStream = new TokenStream("");
		tokenStream.append(catList.toArray(strinList));
		tokenizerMap.get(INDEXFIELD.CATEGORY).tokenize(tokenStream);
		indexableDoc.addField(INDEXFIELD.CATEGORY, tokenStream);

		Set<String> linkSet = doc.getLinks();
		
		strinList = new String[linkSet.size()];
		tokenStream = new TokenStream("");
		tokenStream.append(linkSet.toArray(strinList));
		tokenizerMap.get(INDEXFIELD.LINK).tokenize(tokenStream);
		indexableDoc.addField(INDEXFIELD.LINK, tokenStream);

		List<WikipediaDocument.Section> sections = doc.getSections();
		strinList = new String[2 * sections.size()];
		tokenStream = new TokenStream("");
		int i = 0;
		for (WikipediaDocument.Section section : sections) {
			strinList[i] = section.getTitle();
			strinList[i + 1] = section.getText();
			i = 0;
			tokenStream.append(strinList);
		}
		tokenizerMap.get(INDEXFIELD.TERM).tokenize(tokenStream);
		indexableDoc.addField(INDEXFIELD.TERM, tokenStream);
		strinList = null;
		doc = null;
		return indexableDoc;
	}

}
