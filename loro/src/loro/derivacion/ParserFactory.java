package loro.derivacion;

import java.util.Map;
import java.util.HashMap;

/**
 * Factory class that gets parsers.
 * @author Carlos Rueda
 */
public final class ManejadorDerivacion {
	/** Code of Spanish-0 parser */
	public static final String SP_0 = "sp-0";

	/** Code of English-0 parser */
	public static final String EN_0 = "en-0";


	/** available parsers. */
	private static Map /*name->IDerivador*/ parsers;

	/** Se encarga de obtener el derivador de trabajo.
	 * Múltiples llamadas arrojan el mismo objeto derivador.
	 * @throws IllegalArgumentException
	 */
	public static IDerivador obtDerivador(String code) {
		if ( parsers == null ) {
			parsers = new HashMap();
			parsers.put(SP_0, new loro.parsers.javacc.SP0_JavaCCParser());
			parsers.put(EN_0, new loro.parsers.javacc.EN0_JavaCCParser());
		}
		IDerivador parser = parsers.get(code);
		if ( parser == null )
			throw new IllegalArgumentException(code)

		return parser;
	}
	
	// No instanceable.
	private ManejadorDerivacion() {}
}