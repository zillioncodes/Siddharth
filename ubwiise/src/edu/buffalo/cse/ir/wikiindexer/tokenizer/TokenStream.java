/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.tokenizer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class represents a stream of tokens as the name suggests.
 * It wraps the token stream and provides utility methods to manipulate it
 * @author nikhillo
 *
 */
public class TokenStream implements Iterator<String>{
	private int CurrentIndex=0;
	private ArrayList<String> TextString= new ArrayList<String>();
	/**
	 * Default constructor
	 * @param bldr: THe stringbuilder to seed the stream
	 */
	public TokenStream(StringBuilder bldr) {
		if(bldr != null && bldr.toString()!=null&&!((bldr.toString()).trim().equals("")))
TextString.add(bldr.toString());
		
	}
	
	/**
	 * Overloaded constructor
	 * @param bldr: THe stringbuilder to seed the stream
	 */
	public TokenStream(String string) {
		if(string!=null&&!(string.trim().equals("")))
TextString.add(string);
		
	}
	
	
	/**
	 * Method to append tokens to the stream
	 * @param tokens: The tokens to be appended
	 */
	public void append(String... tokens) {
      
		if(tokens!=null)
       {
		for (String s : tokens) {
			if(s!=null&&!(s.trim().equals("")))
            TextString.add(s);
        }
       }
	
		
	}
	
	/**
	 * Method to retrieve a map of token to count mapping
	 * This map should contain the unique set of tokens as keys
	 * The values should be the number of occurrences of the token in the given stream
	 * @return The map as described above, no restrictions on ordering applicable
	 */
	public Map<String, Integer> getTokenMap() {
		Map<String, Integer> textMap = new HashMap<String, Integer>();
		Integer prevNumOccurances = 1;
		if (TextString == null || TextString.isEmpty()) {
			return null;
		}

		for (String s : TextString) {
			if (null != s && !s.isEmpty()) {
				prevNumOccurances = textMap.get(s);
				if (null != prevNumOccurances && prevNumOccurances != 0) {
					prevNumOccurances++;
				} else {
					prevNumOccurances = 1;
				}
				textMap.put(s, prevNumOccurances);
				prevNumOccurances = 0;
			}
		}

		return textMap;
	}
	
	/**
	 * Method to get the underlying token stream as a collection of tokens
	 * @return A collection containing the ordered tokens as wrapped by this stream
	 * Each token must be a separate element within the collection.
	 * Operations on the returned collection should NOT affect the token stream
	 */
	/*
	 * MyNote: Cloned the Arraylist and sorted it, then returned the reference.
	 */
	public Collection<String> getAllTokens() {
		ArrayList<String> SortedList=new ArrayList<String>();
		//SortedList=Collections.sort(TextString);
	if(TextString!=null&&TextString.size()>0)
	{
		for(String s:TextString)
			SortedList.add(s);
		//Collections.sort(SortedList);
		return SortedList;
	}
	else
		return null;
	
	}
	
	/**
	 * Method to query for the given token within the stream
	 * @param token: The token to be queried
	 * @return: THe number of times it occurs within the stream, 0 if not found
	 */
	public int query(String token) {

		Map<String,Integer> temp;
		temp=this.getTokenMap();
		if(token!=null&&temp!=null&&temp.size()>0&&temp.get(token)!=null)
		return temp.get(token);
		else 
			return 0;
	}
	
	/**
	 * Iterator method: Method to check if the stream has any more tokens
	 * @return true if a token exists to iterate over, false otherwise
	 */
	public boolean hasNext() {
		if((TextString.size()-1>=CurrentIndex)&&(TextString!=null)&&(TextString.size()>0))
		return true;
		else 
			return false;
		//return false;
	}
	
	

	/**
	 * Iterator method: Method to check if the stream has any more tokens
	 * @return true if a token exists to iterate over, false otherwise
	 */
	public boolean hasPrevious() {

		return CurrentIndex > 0 && CurrentIndex < TextString.size() + 1;
	}
	
	/**
	 * Iterator method: Method to get the next token from the stream
	 * Callers must call the set method to modify the token, changing the value
	 * of the token returned by this method must not alter the stream
	 * @return The next token from the stream, null if at the end
	 */
	public String next() {
		if(CurrentIndex<TextString.size())
		return TextString.get(CurrentIndex++);
		else
			return null;
	}
	
	/**
	 * Iterator method: Method to get the previous token from the stream
	 * Callers must call the set method to modify the token, changing the value
	 * of the token returned by this method must not alter the stream
	 * @return The next token from the stream, null if at the end
	 */
	public String previous() {
		if(CurrentIndex>0)
		return TextString.get(--CurrentIndex);	
		else
			return null;

	}
	
	/**
	 * Iterator method: Method to remove the current token from the stream
	 */
	public void remove() {
	if(TextString!=null&&TextString.size()>0)
	{
		if(CurrentIndex>=0&&CurrentIndex<TextString.size())
		TextString.remove(CurrentIndex);
	//	CurrentIndex--;
	}
		
	}
	
	/**
	 * Method to merge the current token with the previous token, assumes whitespace
	 * separator between tokens when merged. The token iterator should now point
	 * to the newly merged token (i.e. the previous one)
	 * @return true if the merge succeeded, false otherwise
	 */
	public boolean mergeWithPrevious() {
		
		if(CurrentIndex>0&&TextString!=null&&TextString.size()>1)
		{
		String temp=TextString.get(CurrentIndex-1)+" "+TextString.get(CurrentIndex);
		TextString.remove(CurrentIndex);
		TextString.remove(CurrentIndex-1);
		TextString.add(CurrentIndex-1,temp);
		CurrentIndex--;
		return true;
		}
		else
		return false;
	}
	
	/**
	 * Method to merge the current token with the next token, assumes whitespace
	 * separator between tokens when merged. The token iterator should now point
	 * to the newly merged token (i.e. the current one)
	 * @return true if the merge succeeded, false otherwise
	 */
	public boolean mergeWithNext() {
		if(CurrentIndex<TextString.size()-1&&TextString!=null)
		{
		String temp=TextString.get(CurrentIndex)+" "+TextString.get(CurrentIndex+1);
		TextString.remove(CurrentIndex+1);
		TextString.remove(CurrentIndex);
		TextString.add(CurrentIndex,temp);
		
		return true;
		}
		else
		return false;
		
	}
	
	/**
	 * Method to replace the current token with the given tokens
	 * The stream should be manipulated accordingly based upon the number of tokens set
	 * It is expected that remove will be called to delete a token instead of passing
	 * null or an empty string here.
	 * The iterator should point to the last set token, i.e, last token in the passed array.
	 * @param newValue: The array of new values with every new token as a separate element within the array
	 */
	public void set(String... newValue) {
		int counter=CurrentIndex;
		if(CurrentIndex<TextString.size()&&TextString!=null&&TextString.size()>0&&newValue!=null&&newValue.length>0)
		{
		// TextString.remove(CurrentIndex);
		for (String s : newValue) 
		{
			if(s!=null&&s.length()>0&&!(s.trim().equals("")))
			{  
				TextString.add(++counter,s);
			//	counter++;
				//CurrentIndex++;
			}
		
			

        }
		if(counter>CurrentIndex)
			
		{
		TextString.remove(CurrentIndex);
		CurrentIndex=counter-1;
		}
	/*	if(CurrentIndex==TextString.size())
		for (String s1 : newValue) {
			
		if(s1!=null&s1!="")
            TextString.add(CurrentIndex,s1);
		CurrentIndex++;*/
		
		}
	}
	
	/**
	 * Iterator method: Method to reset the iterator to the start of the stream
	 * next must be called to get a token
	 */
	public void reset() {
		CurrentIndex=0;
		
	}
	
	/**
	 * Iterator method: Method to set the iterator to beyond the last token in the stream
	 * previous must be called to get a token
	 */
	public void seekEnd() {
		CurrentIndex=TextString.size();
		}
	
	/**
	 * Method to merge this stream with another stream
	 * @param other: The stream to be merged
	 */
	/*My Note:
	 Appending at end of the present TokenStream and setting the pointer to point 
	 at the start of the joint token stream.
	 Pointing at the first token.
	 */
	public void merge(TokenStream other) {
		int TempSize=TextString.size();
		
		if(other!=null)
		{
			other.reset();

		while(other.hasNext())
		{
			TextString.add(TempSize,other.next());
			TempSize++;
			//}
		}
		CurrentIndex=0;
		}
	}

}
