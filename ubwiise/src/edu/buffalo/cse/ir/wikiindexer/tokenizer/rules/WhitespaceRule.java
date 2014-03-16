package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
//import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.EnglishStemmer.Stemmer;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;
@RuleClass(className = RULENAMES.WHITESPACE)
public class WhitespaceRule implements TokenizerRule {

	@Override
	public void apply(TokenStream stream) throws TokenizerException {
		stream.reset();
			if (stream != null) 
			{
			while(stream.hasNext())
			{		//ArrayList<String> temp=new ArrayList<String>();
					//Scanner S1=new Scanner(stream.next());
			String[] tempArray=(stream.next()).split("\\s+");

			//while(S1.hasNext())	
				//	temp.add(S1.next());
					//stream.append(S1.next());
				//i++;
				if(tempArray!=null&&tempArray.length>0&&stream.hasPrevious())
					{
					stream.previous();
					
					//String[] s= temp.toArray();
					stream.set(tempArray);
					if(stream.hasNext())
					stream.next();
			}
			}
		
			stream.reset();	
			}


//Collection<String> temp1=stream.getAllTokens();
//for(String s:temp)
//	System.out.println("start re"+s);
//stream.reset();
				
			}

		
		// TODO Auto-generated method stub

	}


