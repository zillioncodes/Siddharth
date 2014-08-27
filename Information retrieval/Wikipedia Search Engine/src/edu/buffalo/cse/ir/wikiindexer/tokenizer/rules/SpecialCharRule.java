package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;


	import java.util.ArrayList;
	import java.util.Collection;
	import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
	import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
	import java.util.regex.Matcher;
	import java.util.regex.Pattern;
	//import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.EnglishStemmer.Stemmer;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;
	@RuleClass(className = RULENAMES.SPECIALCHARS)
	public class SpecialCharRule  implements TokenizerRule {

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
						String[] tempArray=tempFinal.split("[#~#]");
						stream.previous();
						stream.set(tempArray);
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
			// \\ and ^ 
			//	string=string.replaceAll("(^|\\s)([-])(\\S)", "");// extra space

				
				String T1="([~]|[#]|[$]|[%]|[&]|[:]|[;]|[<]|[>]|[|]|[_]|[/]|[)]|[(]|[=]|[\\\\]|[\"]|[,]|[”]|[“]|[\\[\\]\\{\\}])(\\s*)";
				//string=string.replaceAll("([a-z])([-])([a-z])", "$1 $3");// extra space

				String T2="([@]|[*]|[\\^]|[+])";
				Pattern p=Pattern.compile(T1);
				Matcher match = p.matcher(string);
				while(match.find())
				{
					string=match.replaceAll("$2");
				}
				 p=Pattern.compile(T2);
				 match = p.matcher(string);
				while(match.find())
				{
					string=match.replaceAll(" ");
				}
				//////////////////////////////////////////////////////////
				String T3="(\\s)(\\s|$)";
				 p=Pattern.compile(T3);
				match = p.matcher(string);
				

				 while(match.find())
					{
						string=match.replaceAll("$2");
					}
				// extra space
			//string=string.replaceAll("(^|\\s)(-+)", "$1");// extra space
			//string=string.replaceAll("(^|\\s)(-+)($|\\s)", "$1$3");// extra space
			//string=string.replaceAll("(^)(-+)($)", "");//
	return string;
		
		}
		}
			

		}

