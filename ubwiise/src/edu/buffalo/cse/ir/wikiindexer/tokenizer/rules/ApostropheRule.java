package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;


import java.util.ArrayList;
import java.util.Collection;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.EnglishStemmer.Stemmer;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;
@RuleClass(className = RULENAMES.APOSTROPHE)
public class ApostropheRule implements TokenizerRule {

	@Override
	public void apply(TokenStream stream) throws TokenizerException {
		//int i=0;
		stream.reset();
		Apostrophe Ap=new Apostrophe();	
		if (stream != null) 
			{
			while(stream.hasNext())
			{String tempIntial=stream.next();
				String tempFinal = Ap.apostropheApply(tempIntial);
				if(!tempFinal.equals(tempIntial))
				{
					String[] tempArray=tempFinal.split("[#&#]");
					stream.previous();
					stream.set(tempArray);
					stream.next();
				}
			}		
			}

				
	}
	class Apostrophe
	{
		public Apostrophe()
		{
			
		}
		
		public String apostropheApply(String string)
		{//ArrayList<Pattern> Patterns=new ArrayList<Pattern>();
	//		Pattern patternIs = Pattern.compile("(')(s)", Pattern.DOTALL);
	//	Patterns.add(patternIs);
			//Matcher match = pattern1.matcher(string);
	//	Pattern patternHave = Pattern.compile("(')(ve)", Pattern.DOTALL);
		//Matcher match = pattern2.matcher(string);
	//	Patterns.add(patternHave);
	
//	Pattern patternWould = Pattern.compile("(')(d)", Pattern.DOTALL);
//	Patterns.add(patternWould);

//	Pattern patternWill = Pattern.compile("(')(ll)", Pattern.DOTALL);
//	Patterns.add(patternWill);

	//Pattern patternNot = Pattern.compile("(n)(')(t)", Pattern.DOTALL);
//	Patterns.add(patternNot);

//	Pattern patternAre = Pattern.compile("(')(re)", Pattern.DOTALL);
//	Patterns.add(patternAre);
//Special Cases : shant & wont and aint
			//Cases of 's  then this
		//Special Cases:
			
			//=string.replaceAll("(won't)", "will#&#not");
			String pwont="(won't)";
			Pattern p= Pattern.compile(pwont);
			Matcher m=p.matcher(string);
			while(m.find())
			{
				string=m.replaceAll("will#&#not");
			}
			
			 pwont="(shan't)";
			 p= Pattern.compile(pwont);
			 m=p.matcher(string);
			while(m.find())
			{
				string=m.replaceAll("shall#&#not");
			}
			
		//	String psant="(shan't)";
String paint="(ain't)";
pwont="(ain't)";
p= Pattern.compile(pwont);
m=p.matcher(string);
while(m.find())
{
	string=m.replaceAll("is#&#not");
}

		//	string=string.replaceAll("(shan't)", "shall#&#not");
		//	string=string.replaceAll("(ain't)", "is#&#not");
pwont="(')(ve)(\\s|$)";
p= Pattern.compile(pwont);
m=p.matcher(string);
while(m.find())
{
	string=m.replaceAll("#&#have$3");
}

pwont="(')(d)(\\s|$)";
p= Pattern.compile(pwont);
m=p.matcher(string);
while(m.find())
{
	string=m.replaceAll("#&#would$3");
}
	//	string=string.replaceAll("(')(ve)(\\s|$)", "#&#have$3");
	//	string=string.replaceAll("(')(d)(\\s|$)", "#&#would$3");
pwont="(')(ll)(\\s|$)";
p= Pattern.compile(pwont);
m=p.matcher(string);
while(m.find())
{
	string=m.replaceAll("#&#will$3");
}	


//string=string.replaceAll("(')(ll)(\\s|$)", "#&#will$3");

pwont="(n)(')(t)(\\s|$)";
p= Pattern.compile(pwont);
m=p.matcher(string);
while(m.find())
{
	string=m.replaceAll("#&#not$4");
}
		//string=string.replaceAll("(n)(')(t)(\\s|$)", "#&#not$4");
pwont="(')(re)(\\s|$)";
p= Pattern.compile(pwont);
m=p.matcher(string);
while(m.find())
{
	string=m.replaceAll("#&#are$3");
}

	//	string=string.replaceAll("(')(re)(\\s|$)", "#&#are$3");

pwont="(\\s{0,1}')(em)(\\s|$)";
p= Pattern.compile(pwont);
m=p.matcher(string);
while(m.find())
{
	string=m.replaceAll("#&#them$3");
}

	//	string=string.replaceAll("(')(em)(\\s|$)", "#&#them");

		// Implementation of 's is remaining and I'm
pwont="(\\s|^)(it|It|what|What|who|Who|Where|where|he|He|that|That|she|She)('s)(\\s|$)";
p= Pattern.compile(pwont);
m=p.matcher(string);
while(m.find())
{
	string=m.replaceAll("$1$2 is$4");
}

	//	string=string.replaceAll("(\\s|^)(it|It|what|What|who|Who|Where|where|he|He|that|That|she|She)('s)(\\s|$)","$1$2 is$4");
pwont="(\\s|^)(Let|let)('s)(\\s|$)";
p= Pattern.compile(pwont);
m=p.matcher(string);
while(m.find())
{
	string=m.replaceAll("$1$2#&#us$4");
}
	//	string=string.replaceAll("(\\s|^)(Let|let)('s)(\\s|$)","$1$2#&#us$4");
pwont="(')(s)(\\s|$)";
p= Pattern.compile(pwont);
m=p.matcher(string);
while(m.find())
{
	string=m.replaceAll("$3");
}
	//	string=string.replaceAll("(')(s)(\\s|$)","$3");
pwont="(\\s|^)(I)('m)(\\s|$)";
p= Pattern.compile(pwont);
m=p.matcher(string);
while(m.find())
{
	string=m.replaceAll("$1$2#&#am$4");
}

		//string=string.replaceAll("(\\s|^)(I)('m)(\\s|$)","$1$2#&#am$4");
pwont="(')";
p= Pattern.compile(pwont);
m=p.matcher(string);
while(m.find())
{
	string=m.replaceAll("");
}

		
		//Should be at the end
	//	string=string.replaceAll("(')", "");

	//	string=string.replaceAll("([A-Z])(\\w+)('s)(\\s|$)", "$1$2$4");
		
		
return string;
	
	}
	}
		

	}

