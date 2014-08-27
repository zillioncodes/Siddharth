


	/**
	 * 
	 */
	package edu.buffalo.cse.ir.wikiindexer.tokenizer.test;

	import java.util.Properties;

	import static org.junit.Assert.*;

	import org.junit.Test;
	import org.junit.runner.RunWith;
	import org.junit.runners.Parameterized;

	import edu.buffalo.cse.ir.wikiindexer.IndexerConstants;
	import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;

	/**
	 * @author nikhillo
	 *
	 */
	@RunWith(Parameterized.class)
	
	public class DelimitorRuleTest  extends TokenizerRuleTest {

		public DelimitorRuleTest(Properties props) {
			super(props, IndexerConstants.DELIMITORRULE);
			// TODO Auto-generated constructor stub
		}
		
		@Test
		public void testRule() {
			if (rule == null) {
				 fail("Rule not implemented");
			} else {
				try {
					if (isPreTokenization) {
					//whitespace padded hyphens
					//alphanumeric
					assertArrayEquals(new Object[]{"B 52"}, runtest("B_52"));
					assertArrayEquals(new Object[]{"Allen Cooper"}, runtest("Allen_Cooper"));
					assertArrayEquals(new Object[]{"Allen Cooper shk"}, runtest("Allen_Cooper_shk"));

					}
				} catch (TokenizerException e) {
					
				}
			}
		}
		
	}

