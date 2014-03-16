package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;
import java.text.SimpleDateFormat;
import java.util.*;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;
	@RuleClass(className = RULENAMES.DATES)
	public class DateRule   implements TokenizerRule {
		
	
		Date d=new Date();
		

	String[] dateTime= new String[]{"1900","01","01"};
		@Override
		public void apply(TokenStream stream) throws TokenizerException {
			if(stream==null)
				return;
		
			stream.reset();
			SimpleDateFormat parserDate = new SimpleDateFormat("yyyy");

			SimpleDateFormat parser = new SimpleDateFormat("MMMM dd yyyy");
			SimpleDateFormat parser2 = new SimpleDateFormat("dd MMMM yyyy");
			SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm:ss");
		    SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm a");
		    SimpleDateFormat parserUTC = new SimpleDateFormat("HH:mm:ss Z 'on' EEE, dd MMMM yyyy");
		String month="January|February|March|April|May|June|July|August|September|October|November|December|january|february|march|april|may|june|july|august|september|october|november|december";
			final String RegexTZ ="(\\d+)(:)(\\d+)(:)(\\d+)(\\s*)(\\w{1,3})(\\s+)(on|at)(\\s+)(Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday)([,]*)(\\s+)(\\d{1,2})(\\s+)("+month+")(\\s+|[,])(\\d{1,4})";
			  final String RegexMMDD ="((("+month+")(\\s+)(\\d{1,2}))|((\\d{1,2})(\\s+)("+month+")))";

		 
			final String RegexMMDDYY ="("+month+")(\\s+|[,]\\s*)(\\d{1,2})(\\s+|[,]\\s+)(\\d{1,4})";
			final String RegexDDMMYY ="(\\d{1,2})(\\s+)("+month+")(\\s+|[,])(\\d{1,4})";
			final String RegexBC ="(\\s)(\\d+)(\\s*)(BC)";
			final String RegexYear ="(\\s|^)(\\d{4,4})(\\s+|$)";
			final String RegexTwoYear ="(\\s|^)(\\d{4,4})([�])(\\d{1,2})";

			final String RegexDate ="(\\s|^)(\\d{1,2})(:)(\\d{1,2})(\\s*)(AM|PM|am|pm)";
			final String RegexAD ="(\\s|^)(\\d+)(\\s*)(AD|ad)";
while(stream.hasNext()){
	String date=stream.next();
	StringBuffer buff = new StringBuffer();
	/////////////////////////////for date with time with timezone/////////////////
		if(date.matches("(.*)"+RegexTZ+"(.*)"))
		{//System.out.println("date with timezone");
			try{
				SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd HH:mm:ss");

				//String s= date.replaceAll(RegexYear, "$3");
				Pattern p= Pattern.compile(RegexTZ);
				Matcher m=p.matcher(date);
				while(m.find())
				{
					String s =m.group();
					d =  parserUTC.parse(s);
					formatter.setTimeZone(TimeZone.getTimeZone(s.replaceAll(RegexTZ, "$7")));
					String result =formatter.format(d);
					m.appendReplacement(buff, result);
				
				}
				m.appendTail(buff);
				date = buff.toString();
				if(!date.equals(stream.previous()))
				{
				stream.set(date);
				if(stream.hasNext())
				stream.next();
				}
				else
					if(stream.hasNext())
					stream.next();

						
			}
			catch(Exception e)
			{
				e.printStackTrace();
				//System.out.println("wow3");
				}
		}

	
	

			
///////////////////// for Month date Year/////////////////////////////////////
		if(date.matches("(.*)"+RegexMMDDYY+"(.*)"))
		{
		try{
				
				Pattern p= Pattern.compile(RegexMMDDYY);
				Matcher m=p.matcher(date);
				SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
	
				while(m.find())
				{
					String s =m.group().replaceAll(RegexMMDDYY, "$1 $3 $5");
					d =  parser.parse(s);
					String result =formatter.format(d);
				 date=m.replaceAll(result);
				
				}
				
				if(!date.equals(stream.previous()))
				{
				stream.set(date);
				if(stream.hasNext())
				stream.next();
				}
				else
					if(stream.hasNext())
					stream.next();

						
			}
			catch(Exception e)
			{
				e.printStackTrace();
				}
				
		
		}
			
			
///////////////////////////////////////date then month then year/////////////////////////////			
		if(date.matches("(.*)"+RegexDDMMYY+"(.*)"))
		{
				try{
					
					Pattern p= Pattern.compile(RegexDDMMYY);
					Matcher m=p.matcher(date);
					SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		
					while(m.find())
					{
						String s =m.group().replaceAll(RegexMMDDYY, "$1 $3 $5");
						d =  parser2.parse(s);
						String result =formatter.format(d);
					 date=m.replaceAll(result);
					//	System.out.println(date+"3");
					
					}
					
					if(!date.equals(stream.previous()))
					{
					stream.set(date);
					if(stream.hasNext())
					stream.next();
					}
					else
						if(stream.hasNext())
						stream.next();

							
				}
				catch(Exception e)
				{
					e.printStackTrace();
					}
					
			
		}

			
	////////////////////////////////////////////// for only date and month///////////////////
			//System.out.println("only month date");
		if(date.matches("(.*)"+RegexMMDD+"(.*)"))
		{
		try{
					SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");

					//String s= date.replaceAll(RegexYear, "$3");
					Pattern p= Pattern.compile(RegexMMDD);
					Matcher m=p.matcher(date);
					while(m.find())
					{
						String s =m.group()+" 1900";
						date=m.replaceAll(s);						
						//System.out.println(date);
				
					
					}
					
					if(!date.equals(stream.previous()))
					{
					stream.set(date);
					continue;
					}
					else
						if(stream.hasNext())
							stream.next();

							
				}
				catch(Exception e)
				{
					e.printStackTrace();
					//System.out.println("wow3");
					}
			
		}
		
		
	// For BC////////////////////////////////////////////////////////////////////////////////		
			
			
			
			if(date.matches("(.*)"+RegexBC+"(.*)"))
			{
				try{
					SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
					Pattern p= Pattern.compile(RegexBC);
					Matcher m=p.matcher(date);
					buff = new StringBuffer();
					while(m.find())
					{
						String s= m.group().replaceAll(RegexBC, "$2");
						s="01 January "+s;
						d =  parser2.parse(s);
						String result = "-"+formatter.format(d);
						m.appendReplacement(buff, "$1"+result);
					}
					m.appendTail(buff);
					date = buff.toString();
			//		String s= date.replaceAll(RegexBC, "$3");
			//		s="01 January "+s;
			//		d =  parser2.parse(s);
				//			String result = "-"+formatter.format(d);
							
						//	System.out.println(result);

							//String dateFinal=date.replaceAll(RegexBC, "$1$2"+result+"$6");
							//System.out.println(dateFinal+"3");

							if(!stream.previous().equals(date))
							{//date=dateFinal;
								//stream.previous();
								stream.set(date);
								if(stream.hasNext())
									stream.next();
								}
							else stream.next();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				//	System.out.println("wow3");
					}
		}

			
			
			
			

			
			
		
			
/////for date only//////////////////////////////////////////////////////////////////////////			
			if(date.matches("(.*)"+RegexDate+"(.*)"))
			{
			//System.out.println("Wow4");
				try{

					//String s= date.replaceAll(RegexYear, "$3");
					Pattern p= Pattern.compile(RegexDate);
					Matcher m=p.matcher(date);
					buff = new StringBuffer();
					while(m.find())
					{
						String s =m.group().replaceAll(RegexDate, "$2$3$4 $6");
					//	System.out.println(s);

						d =  parseFormat.parse(s);
						String result =displayFormat.format(d);
					//	System.out.println(result);
					//	System.out.println(date+"3");
					 	m.appendReplacement(buff, "$1"+result);
					}
					m.appendTail(buff);
					date = buff.toString();
					if(!date.equals(stream.previous()))
					{
					stream.set(date);
					if(stream.hasNext())
					stream.next();
					}
					else
						if(stream.hasNext())
						stream.next();

							
				}
				catch(Exception e)
				{
					e.printStackTrace();
					//System.out.println("wow4");
					}
			

			}

			
//////////////////For AD///////////////////////////////////////////////			
			if(date.matches("(.*)"+RegexAD+"(.*)"))
			{
				try{
					SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
					Pattern p= Pattern.compile(RegexAD);
					Matcher m=p.matcher(date);
					buff = new StringBuffer();
					
					while(m.find())
					{
						String s= m.group().replaceAll(RegexBC, "$2");
						s="01 January "+s;
						d =  parser2.parse(s);
						String result =formatter.format(d);
						m.appendReplacement(buff, "$1"+result);
					}
					m.appendTail(buff);
					date = buff.toString();
			//		String s= date.replaceAll(RegexBC, "$3");
			//		s="01 January "+s;
			//		d =  parser2.parse(s);
				//			String result = "-"+formatter.format(d);
							
						//	System.out.println(result);

							//String dateFinal=date.replaceAll(RegexBC, "$1$2"+result+"$6");
							//System.out.println(dateFinal+"3");

							if(!stream.previous().equals(date))
							{//date=dateFinal;
								//stream.previous();
								stream.set(date);
								if(stream.hasNext())
									stream.next();
								}
							else stream.next();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				//	System.out.println("wow3");
					}
		}

			
		
/////////////////////////////////for two year/////////////////////////////////////			
			
			if(date.matches("(.*)"+RegexTwoYear+"(.*)"))
			{//System.out.println("Two years");
				try{
					SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");

					//String s= date.replaceAll(RegexYear, "$3");
					Pattern p= Pattern.compile(RegexTwoYear);
					Matcher m=p.matcher(date);
					while(m.find())
					{
						String cha=m.group().replaceAll(RegexTwoYear, "$3");
						//System.out.println(cha);
						String[] tempString=m.group().split(cha);
						if(tempString.length>1)
						{
							//char[] tempchar=tempString[0].toCharArray();
						//	System.out.println(tempString[0]);
							tempString[1]=tempString[0].substring(0,3)+tempString[1];
						//	System.out.println(tempString[1]);
							
						}
						String result="";
						int i=0;
						for(String d1:tempString)
						{if(i==1)
							result=result+cha;
						
						i++;
						String s ="1 January "+d1;
					//	System.out.println(s);
						d =  parser2.parse(s);
					//	System.out.println(d);
						 result = result+formatter.format(d);
					//	System.out.println(result);
						}
						if(!result.equals(""))
							m.appendReplacement(buff, "$1"+result);
					}
					m.appendTail(buff);
					date = buff.toString();
					
					if(!date.equals(stream.previous()))
					{
					stream.set(date);
					stream.next();
					}
					else
						stream.next();

							
				}
				catch(Exception e)
				{
					e.printStackTrace();
				//	System.out.println("wow3");
					}
			}
		
			
///////////////////////////////////For Year only//////////////////////////			
			if(date.matches("(.*)"+RegexYear+"(.*)"))
			{//System.out.println("Wow4");
				try{
					SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");

					//String s= date.replaceAll(RegexYear, "$3");
					Pattern p= Pattern.compile(RegexYear);
					Matcher m=p.matcher(date);
					buff = new StringBuffer();
					while(m.find())
					{
						String[] tempString=m.group().split("[�]");
						if(tempString.length>1)
						{
							//char[] tempchar=tempString[0].toCharArray();
						//	System.out.println(tempString[0]);
							tempString[1]=tempString[0].substring(0,3)+tempString[1];
							
						}
						String result="";
						for(String d1:tempString)
						{
						String s ="1 January "+d1.trim();
						//System.out.println(s);
						d =  parser2.parse(s);
					//	System.out.println(d);
						 result = result+formatter.format(d);
						}
						if(!result.equals(""))
							m.appendReplacement(buff, "$1"+result+"$3");
					}
					m.appendTail(buff);
					date = buff.toString();
					
					if(!date.equals(stream.previous()))
					{
					stream.set(date);
					stream.next();
					}
					else
						stream.next();

							
				}
				catch(Exception e)
				{
					e.printStackTrace();
				//	System.out.println("wow3");
					}
			}

			



}	
}
		
		
	
	}
		
			

		





		

