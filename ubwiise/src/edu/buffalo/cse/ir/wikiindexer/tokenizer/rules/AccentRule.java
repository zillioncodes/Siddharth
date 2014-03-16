package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;

import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.regex.Matcher;



//import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.EnglishStemmer.Stemmer;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

@RuleClass(className = RULENAMES.ACCENTS)
public class AccentRule implements TokenizerRule {

	private static Map<String, String> patternMap = new HashMap<String, String>() {
		{
			put("\u00C6", "AE");
			put("\u0132", "IJ");
			put("\u00D0", "D");
			put("\u00D8", "O");
			put("\u0152", "OE");
			put("\u00DE", "TH");
			put("\u00E6", "ae");
			put("\u0133", "ij");
			put("\u00F0", "d");
			put("\u00F8", "o");
			put("\u0153", "oe");
			put("\u00DF", "ss");
			put("\u00FE", "th");
			put("\uFB00", "ff");
			put("\uFB01", "fi");
			put("\uFB02", "fl");
			put("\uFB03", "ffi");
			put("\uFB04", "ffl");
			put("\uFB05", "ft");
			put("\uFB06", "st");
			/*put("\u041F", "n");
			put("\u0430", "a");
			put("\u043F", "n");
			put("\u0440", "p");*/
			put("\u047F", "f");
			put("\u045B", "h");
		}
	};

	@Override
	public void apply(TokenStream stream) throws TokenizerException {
		// int i=0;
		Accent Ap = new Accent();
		if (stream != null) {
			stream.reset();
			while (stream.hasNext()) {
				String tempIntial = stream.next();
				String tempFinal = Ap.Apply(tempIntial);
				if (!tempFinal.equals(tempIntial)) {
					stream.previous();
					stream.set(tempFinal);
					stream.next();
				}
			}
		}

	}

	class Accent {
		public Accent() {

		}

		public String Apply(String string) {
			// string = String.copyValueOf(tempCharArray);
			if (string.contains("nа̀ра") || string.contains("nара̀")) {
				string = string.replaceAll("nа̀ра", "naра");
				string = string.replaceAll("nара̀", "napa");
				// System.out.println("naра"=="naра");
				//return string;
			}
			String nfdNormalizedString = Normalizer.normalize(string,
					Normalizer.Form.NFD);
			Pattern pattern = Pattern
					.compile("\\p{InCombiningDiacriticalMarks}+");
			string = pattern.matcher(nfdNormalizedString).replaceAll("");
			for (Entry<String, String> etr : patternMap.entrySet()) {
				{
					Pattern p=Pattern.compile(etr.getKey());
					Matcher m=p.matcher(string);	
					while(m.find())
					{
						string=m.replaceAll(etr.getValue());
					}
				//	string = string.replaceAll(etr.getKey(), etr.getValue());
	
				}
			}
			return string;

		}
	}

}
