// $Id$
package loro.derivacion;

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


	/** available parsers. */
	private static Map /*name->IDerivador*/ parsers;
	
	/** current language code */
	private static String currCode;

	/** initialization: must be called before other services. */
	static {
		parsers = new HashMap();
		parsers.put(SP_0, new loro.parsers.javacc.SP0_JavaCCParser());
		parsers.put(EN_0, new loro.parsers.javacc.EN0_JavaCCParser());
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
		IDerivador parser = (IDerivador) parsers.get(currCode);
		return parser;
	}

	// No instanceable.
	private ParserFactory() {}
}