package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;


	import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
	import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;
	@RuleClass(className = RULENAMES.DELIM)
	public class DelimitorRule  implements TokenizerRule {

		@Override
		public void apply(TokenStream stream) throws TokenizerException {
			//int i=0;
			stream.reset();
			Delimitor Ap=new Delimitor();	
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
		class Delimitor
		{
			public Delimitor()
			{
				
			}
			
			public String Apply(String string)
			{
			// \\ and ^ 
			//	string=string.replaceAll("(^|\\s)([-])(\\S)", "");// extra space

				
				string=string.replaceAll("([_])"," ");

				// extra space
			//string=string.replaceAll("(^|\\s)(-+)", "$1");// extra space
			//string=string.replaceAll("(^|\\s)(-+)($|\\s)", "$1$3");// extra space
			//string=string.replaceAll("(^)(-+)($)", "");//
	return string;
		
		}
		}
			

		}


