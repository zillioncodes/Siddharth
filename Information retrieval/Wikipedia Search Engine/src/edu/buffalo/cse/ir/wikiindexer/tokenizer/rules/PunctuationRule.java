package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

	import java.util.ArrayList;
	import java.util.Collection;
	import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
	import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
	import java.util.regex.Matcher;
	import java.util.regex.Pattern;
	//import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.EnglishStemmer.Stemmer;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;
	@RuleClass(className = RULENAMES.PUNCTUATION)
	public class PunctuationRule  implements TokenizerRule {

		@Override
		public void apply(TokenStream stream) throws TokenizerException {
			//int i=0;
			stream.reset();
			Punctuation Ap=new Punctuation();	
			if (stream != null) 
				{
				while(stream.hasNext())
				{String tempIntial=stream.next();
					String tempFinal = Ap.Apply(tempIntial);
					if(!tempFinal.equals(tempIntial))
					{
						stream.previous();
						stream.set(tempFinal);
						stream.next();
					}
				}		
				}

					
		}
		class Punctuation
		{
			public Punctuation()
			{
				
			}
			
			public String Apply(String string)
			{
				//Matcher match = pattern1.matcher(string);
		

		//Pattern patternNot = Pattern.compile("(n)(')(t)", Pattern.DOTALL);
//		Patterns.add(patternNot);

//		Pattern patternAre = Pattern.compile(
		//	String T1="(!*|[?]*|[.]*)(!*|[?]*|[.]*)(!+|[?]+|[.]+)(\\s+|$)";//,"$4";
				String T1="(!+|[?]+|[.]+)(\\s+|$)";//,"$4";
			Pattern patternSP = Pattern.compile(T1);
			Matcher match = patternSP.matcher(string);
			while(match.find())
			{
				string=match.replaceAll("$2");
				match.reset(string);
			}
			/////////////////////////////////////////////////////////////
//			String T3="(\\s)(\\s|$)";
//			Pattern p=Pattern.compile(T3);
//			match = p.matcher(string);
//			
//
//			 while(match.find())
//				{
//					string=match.replaceAll("$2");
//				}
		//	string=string.replaceAll("(!|[?]|[.])(\\s+|$)","$2");

		
	return string;
		
		}
		}
			

		}



