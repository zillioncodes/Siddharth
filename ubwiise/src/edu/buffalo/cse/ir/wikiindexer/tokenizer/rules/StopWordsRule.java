package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;
	import java.util.ArrayList;
	import java.util.Collection;
	import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
	import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
	import java.util.regex.Matcher;
	import java.util.regex.Pattern;
	//import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.EnglishStemmer.Stemmer;
	import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;
	@RuleClass(className = RULENAMES.STOPWORDS)
	public class StopWordsRule   implements TokenizerRule {

		@Override
		public void apply(TokenStream stream) throws TokenizerException {
			//int i=0;
//			StopWords st=new StopWords();
			stream.reset();
			if (stream != null) 
				{
				while(stream.hasNext())
				{
					String tempIntial=stream.next();
					tempIntial=tempIntial.toLowerCase();
					if(StopWords.mp.containsKey(tempIntial))
					{
						stream.previous();
						stream.remove();
							
					}

				}		
				
				
				}//null

					
		}
	
		}
			

		





		



	



