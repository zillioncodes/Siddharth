package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;
	import java.util.ArrayList;
	import java.util.Collection;
	import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
	import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
	import java.util.regex.Matcher;
	import java.util.regex.Pattern;
	//import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.EnglishStemmer.Stemmer;
	import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;
	@RuleClass(className = RULENAMES.CAPITALIZATION)
	public class CapitalizationRule  implements TokenizerRule {

		@Override
		public void apply(TokenStream stream) throws TokenizerException {
			String regexC="((^|([.|?|!])(\\s+))([A-Z])([a-z]+))|((^|([.|?|!])(\\s+))([A-Z])(\\s|$))";
			Pattern p=Pattern.compile(regexC);
			
			stream.reset();
		//	StopWords st=new StopWords();
			if (stream != null) 
				{
				while(stream.hasNext())
				{
				
					String tempInitial=stream.next();
					Matcher m=p.matcher(tempInitial);
					StringBuffer buff = new StringBuffer();
					while(m.find())
					{//System.out.println(m.group());
						m.appendReplacement(buff, m.group().toLowerCase());
						//System.out.println(tempFinal);
					}
					m.appendTail(buff);
					tempInitial = buff.toString();
			//	String tempFinal=tempInitial.replaceAll(regexC, "$3$4");
			//	tempFinal=tempFinal.toLowerCase();
			//	tempFinal=tempInitial.replaceAll(regexC, "$1$2"+tempFinal);
				if(!tempInitial.equals(stream.previous()))
				{
					stream.set(tempInitial);
					stream.next();
				}
				else
					stream.next();
					}
			

				}		
				
				
				}//null

					
		}
	
		
	
			

		





		



	




