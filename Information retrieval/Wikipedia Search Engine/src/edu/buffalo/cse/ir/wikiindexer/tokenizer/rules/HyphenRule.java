package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;
import java.util.ArrayList;
import java.util.Collection;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.EnglishStemmer.Stemmer;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;
@RuleClass(className = RULENAMES.HYPHEN)
public class HyphenRule  implements TokenizerRule {

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
		public Hyphen()
		{
			
		}
		
		public String Apply(String string)
		{
//string.re
			
	//	string=string.replaceAll("(\\s+|^)([a-z]+)(-+)([a-z])","$1$2 $4");
			String s="(\\s+|^)([a-z]+)(-+)([a-z])";
			Pattern p=Pattern.compile(s);
			Matcher m = p.matcher(string);
			while(m.find())
			{
				string=m.replaceAll("$1$2 $4");
			}
			
			//	string=string.replaceAll("(-+)(\\s|$)", "$2");// extra space

			 s="(-+)(\\s|$)";
			 p=Pattern.compile(s);
			 m = p.matcher(string);
			while(m.find())
			{
				string=m.replaceAll("$2");
			}
			
	//	string=string.replaceAll("(^|\\s)(-{1,})", "$1");// extra space
			 s="(^|\\s)(-{1,})";
			 p=Pattern.compile(s);
			 m = p.matcher(string);
			while(m.find())
			{
				string=m.replaceAll("$1");
			}
	//	string=string.replaceAll("(^|\\s)(-+)($|\\s)", "$1$3");// extra space
			 s="(^|\\s)(-+)($|\\s)";
			 p=Pattern.compile(s);
			 m = p.matcher(string);
			while(m.find())
			{
				string=m.replaceAll("$1$3");
			}
//	//	string=string.replaceAll("(^)(-+)($)", "");//
//			 s="(^)(-+)($)";
//			 p=Pattern.compile(s);
//			 m = p.matcher(string);
//			while(m.find())
//			{
//				string=m.replaceAll("$1$3");
		//	}
return string;
	
	}
	}
		

	}





	


