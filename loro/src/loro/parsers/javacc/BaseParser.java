package loro.parsers.javacc;

import loro.DerivacionException;
import loro.derivacion.*;
import loro.arbol.*;
import loro.util.Util;
import loro.Rango;

import java.io.*;
import java.util.*;

//////////////////////////////////////////////////////////////////////
/**
 * Base implementation.
 *
 * @author Carlos Rueda
 * @version $Id$
 */
public abstract class BaseParser implements IDerivador {
	/** 
	 * Texto en compilacion. Esta cadena siempre se deja
	 * disponible para despachar adecuadamente la creacion
	 * de errores, en cuanto a rangos comprometidos.
	 */
	protected String texto;

	/** creates the base implementation */
	protected BaseParser() {
		texto = "";
	}
	
	public String obtTextoFuente() {
		return texto;
	}

	///////////////////////////////////////////////////////////////////////
	public IDerivador ponTextoFuente(Reader fuente) {
		// leer todo el reader fuente como cadena
		// para tener disponibilidad del texto siempre.
		
		BufferedReader br = new BufferedReader(fuente, 2*4096);
		StringBuffer sb = new StringBuffer();
		int ch;
		try {
			while ( (ch = br.read()) != -1 )  {
				sb.append((char) ch);
			}
		}
		catch ( IOException ex ) {
			throw new RuntimeException("Oops: " +ex.getMessage());
		}

		return ponTextoFuente(sb.toString());
	}
}

