// $Id$
package loro.derivacion;

import loro.ILanguageInfo;

import java.util.Map;
import java.util.HashMap;

/**
 * Factory class that gets parsers.
 * @author Carlos Rueda
 */
public final class ParserFactory {
	/** Code of Spanish-0 parser */
	public static final String SP_0 = "sp-0";

	/** Code of English-0 parser */
	public static final String EN_0 = "en-0";

	private static class Pair {
		IDerivador parser;
		ILanguageInfo langInfo;
		Pair(IDerivador parser, ILanguageInfo langInfo) {
			this.parser = parser;
			this.langInfo = langInfo;
		}
	}

	/** available parsers. */
	private static Map /*name->Pair*/ parsers;
	
	/** current language code */
	private static String currCode;

	/** initialization: must be called before other services. */
	static {
		parsers = new HashMap();
		parsers.put(SP_0,
			new Pair(
				new loro.parsers.javacc.SP0_JavaCCParser(),
				new loro.parsers.javacc.SP0_JavaCCParser.MetaInfo()
			)
		);
		parsers.put(EN_0, 
			new Pair(
				new loro.parsers.javacc.EN0_JavaCCParser(),
				new loro.parsers.javacc.EN0_JavaCCParser.MetaInfo()
			)
		);
	}		

	/** Sets the current enabled language code.
	 * This must be called before any other method.
	 * @throws IllegalArgumentException if code is null or unrecognized
	 */
	public static void setCurrentCode(String code) {
		if ( code == null || parsers.get(code) == null )
			throw new IllegalArgumentException(code);
		currCode = code;
	}
	
	/** Gets the current enabled language code. */
	public static String getCurrentCode() {
		return currCode;
	}
	
	/** Gets the parser according to current language code.
	 * @throws NullPointerException if current code is null
	 */
	public static IDerivador getParser() {
		if (currCode == null )
			throw new NullPointerException("currCode is null");
		IDerivador parser = ((Pair) parsers.get(currCode)).parser;
		return parser;
	}

	/** Gets the parser metainfo according to current language code.
	 * @throws NullPointerException if current code is null
	 */
	public static ILanguageInfo getLanguageInfo() {
		if (currCode == null )
			throw new NullPointerException("currCode is null");
		ILanguageInfo langInfo = ((Pair) parsers.get(currCode)).langInfo;
		return langInfo;
	}

	// No instanceable.
	private ParserFactory() {}
}