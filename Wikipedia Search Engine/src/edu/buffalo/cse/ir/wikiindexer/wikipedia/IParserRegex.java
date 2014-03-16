package edu.buffalo.cse.ir.wikiindexer.wikipedia;

import java.util.regex.Pattern;

public interface IParserRegex {

	public static final Pattern[] textPatterns = {
			Pattern.compile("\\'{5}(.*?)\\'{5}", Pattern.DOTALL),
			Pattern.compile("\\'{3}(.*?)\\'{3}", Pattern.DOTALL),
			Pattern.compile("\\'{2}(.*?)\\'{2}", Pattern.DOTALL) };

	public static final Pattern listPattern = Pattern.compile(
			"(^|\\n)([\\*#;:]+)\\s*(([^\\*#;:])([^\\*\\n]*))", Pattern.DOTALL);

	public static final Pattern templatePattern = Pattern.compile(
			"\\{\\{([^}{]+?)\\}\\}", Pattern.DOTALL);
	public static final Pattern templateImagePattern = Pattern.compile(
			"\\{\\|([^}{]+?)\\|\\}", Pattern.DOTALL);

	public static final Pattern[] tagPatterns = {
			Pattern.compile("(\\<\\!--.*?--\\>[ ]*)", Pattern.DOTALL),
			// Pattern.compile("(<([^/>]*)?>(.*?)</([^/>]*)?>|<([^/>]*)?/>)",
			// Pattern.DOTALL),
			Pattern.compile("(\\<ref\\>(http://|https://|irc://|ircs://|ftp://|news://|mailto:|gopher://)[^\\s]*\\<\\/ref\\>)", Pattern.DOTALL),
			Pattern.compile("(\\<ref\\>\\{\\{.*?\\}\\}\\<\\/ref\\>)", Pattern.DOTALL),
			Pattern.compile("(\\<ref\\>()[^\\s]*\\<\\/ref\\>)", Pattern.DOTALL),
			Pattern.compile("(\\<([^\\><]*?)\\>)", Pattern.DOTALL),
			Pattern.compile("(&lt;([^\3]*?)(&gt;))", Pattern.DOTALL) };;

	public static final Pattern linkUrlPattern = Pattern.compile(
			"\\[\\[([^\\]]*)\\]\\]", Pattern.DOTALL);

	public static final Pattern[] linkPatterns = {
			Pattern.compile("(\\[(\\[()\\s*((File|Image):[^\\|\\]]*)\\])\\])",
					Pattern.DOTALL),
			Pattern.compile(
					"(\\[(\\[\\s*:+\\s*(Category\\s*:[^\\|\\]]*)\\])\\])",
					Pattern.DOTALL),
			Pattern.compile(
					"(\\[\\[\\s*:+\\s*(Category\\s*:([^\\|\\]]*))\\s*\\|\\s*\\]\\])",
					Pattern.DOTALL),
			Pattern.compile(
					"(\\[(\\[\\s*Category\\s*:\\s*([^\\|\\]]*)\\])\\])",
					Pattern.DOTALL),
			Pattern.compile(
					"(\\[(\\[\\s*Category\\s*:\\s*([^\\|\\]]*)\\s*\\|\\s*([^\\|\\]]*)\\])\\])",
					Pattern.DOTALL),
			Pattern.compile("(\\[(\\[([^\\|\\]\\[]*)\\])\\])", Pattern.DOTALL),
			Pattern.compile("\\[\\[(([^\\]\\[])*\\|([^\\]\\[]+))\\]\\]",
					Pattern.DOTALL),

			Pattern.compile("(\\[\\[(([^:\\[\\]]*?),[^\\[\\]]*?)\\|\\]\\])",
					Pattern.DOTALL),
			Pattern.compile(
					"(\\[\\[(([^:\\[\\]]*?))\\s*\\(([^\\[\\]]*?)\\)\\|\\]\\])",
					Pattern.DOTALL),
			Pattern.compile("(\\[\\[([^\\[\\]:]*:([^,(#]*?))\\|\\]\\])",
					Pattern.DOTALL),
			Pattern.compile(
					"(\\[\\[([^\\[\\]]*):([^:]*?),([^:,#]*?)\\|\\]\\])",
					Pattern.DOTALL),
			Pattern.compile(
					"(\\[\\[([^\\[\\]]*):([^:\\(\\),]*?)\\s*\\(([^:,#\\[\\]]*?)\\)\\|\\]\\])",
					Pattern.DOTALL),
			Pattern.compile(
					"(\\[\\[(([^\\[\\]:]*:([^,\\(#\\|]*?)#([^\\|]*?)))\\|\\]\\])",
					Pattern.DOTALL),
			Pattern.compile("(\\[(\\[()\\s*((File|Image):[^\\[\\]]*)\\])\\])",
					Pattern.DOTALL),
			Pattern.compile(
					"(\\[?\\s*(http://|https://|irc://|ircs://|ftp://|news://|mailto:|gopher://)[^\\s\\]]*\\s*([^\\]]*)\\]?\\s?)",
					Pattern.DOTALL),
			Pattern.compile(
					"(http://|https://|irc://|ircs://|ftp://|news://|mailto:|gopher://)([^\\s\\]]*\\s?)()",
					Pattern.DOTALL) };

	public static final Pattern categoryPattern = Pattern
			.compile(
					"(\\[(\\[\\s*Category\\s*:\\s*([^\\|\\]]*)\\s*\\|*\\s*([^\\|\\]]*)\\])\\])",
					Pattern.DOTALL);

	public static final Pattern sectionTitlePattern = Pattern.compile(
			"(^|\n)={2,6}\\s*([^=]+?)\\s*={2,6}", Pattern.DOTALL);

	public static final Pattern sectionPattern = Pattern.compile(
			"=({2,6}\\s*(((?!==).)+)\\s*={2,6})\n(((?!==).)+)", Pattern.DOTALL);
}
