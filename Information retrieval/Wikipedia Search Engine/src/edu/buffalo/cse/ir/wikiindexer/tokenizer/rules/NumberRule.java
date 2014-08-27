package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;


	


	import java.util.ArrayList;
	import java.util.Collection;
	import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
	import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
	import java.util.regex.Matcher;
	import java.util.regex.Pattern;
	//import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.EnglishStemmer.Stemmer;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;
	@RuleClass(className = RULENAMES.NUMBERS)
	public class NumberRule  implements TokenizerRule {

		@Override
		public void apply(TokenStream stream) throws TokenizerException {
			//int i=0;
			stream.reset();
			Hyphen Ap=new Hyphen();	
			if (stream != null) 
				{
				while(stream.hasNext())
				{String tempIntial=stream.next();
					String tempFinal = Ap.Apply(tempIntial);
					if((!tempFinal.equals(tempIntial))&&(!tempFinal.trim().equals("")))
					{//System.out.println(""+tempFinal);
						//String[] tempArray=tempFinal.split("[#&#]");
						stream.previous();
						stream.set(tempFinal);
						stream.next();
					}
					if(tempFinal.trim().equals(""))
					{

						stream.previous();
						stream.remove();
							}

				}		
				}

					
		}
		class Hyphen
		{		
		String T1="(^|\\s)(\\d+)([.]|[,])(\\d+)($|\\s|[%])";//,"$1$5");
		String T2="(^|\\s)(\\d{1,7}|\\d{9,})($|\\s|[%]|[/])(\\d{0,})";//,"$1$3");
	//	String T3="(^|\\s)(\\d{1,7})($|\\s)";//,"$1$3");

		
			public Hyphen()
			{
				
			}
			
			public String Apply(String string)
			{
				Pattern p1= Pattern.compile(T1);
				Pattern p2= Pattern.compile(T2);

			Matcher m=p1.matcher(string);
			while(m.find())
			{
				string=m.replaceAll("$1$5");
			}
			 m=p2.matcher(string);
			while(m.find())
			{
				string=m.replaceAll("$1$3");
			}
			String T3="(\\s)(\\s|$)";
			Pattern p=Pattern.compile(T3);
			Matcher match = p.matcher(string);
			

			 while(match.find())
				{
					string=match.replaceAll("$2");
				}
		
			//string=string.replaceAll("(^|\\s)(\\d+)","$1");
			//string=string.replaceAll("(\\d+)($|\\s|[%])","$2");

			//string=string.replaceAll("([@]|[\\^]|[*]|[+]|[-])", "#~#");// extra space
			//string=string.replaceAll("(^|\\s)(-+)", "$1");// extra space
			//string=string.replaceAll("(^|\\s)(-+)($|\\s)", "$1$3");// extra space
			//string=string.replaceAll("(^)(-+)($)", "");//
	return string;
		
		}
		}
			


}
