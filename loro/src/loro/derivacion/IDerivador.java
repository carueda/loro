package loro.derivacion;

import loro.DerivacionException;
import loro.arbol.*;

import java.io.Reader;

//////////////////////////////////////////////////////////////////////
/**
 * Interface para interactuar con un analizador sintáctico.
 *
 * Un derivador permite construir el árbol sintáctico correspondiente
 * a un programa fuente, a una accion para interpretacion interactiva, o
 * simplemente derivar un nombre para facilitar su revision desde
 * interfaces interactivas con el usuario.
 *
 * @author Carlos Rueda
 */
public interface IDerivador
{
	///////////////////////////////////////////////////////////////////////
	/**
	 * Deriva un arbol sintactico para accion de interprete.
	 *
	 * @return 			El arbol resultante.
	 *
	 * @exception DerivacionException
	 *		Si se presenta algún error en la derivación.
	 */
	public Nodo derivarAccionInterprete()
	throws DerivacionException;

	///////////////////////////////////////////////////////////////////////
	/**
	 * Deriva un arbol sintactico para el programa fuente asociado.
	 *
	 * @return 			El arbol resultante.
	 *
	 * @exception DerivacionException
	 *		TraduccionException Si se presenta 
	 *		algún error en la derivación.
	 */
	public NFuente derivarFuente()
	throws DerivacionException;

	///////////////////////////////////////////////////////////////////////
	/**
	 * Deriva un identificador:  id &lt;EOF>
	 *
	 * @return 			El arbol resultante.
	 *
	 * @exception DerivacionException
	 *		TraduccionException Si se presenta 
	 *		algún error en la derivación.
	 */
	public TId derivarId()
	throws DerivacionException;

	///////////////////////////////////////////////////////////////////////
	/**
	 * Deriva un nombre:  id1::id2::id3 &lt;EOF>
	 *
	 * @return 			El arbol resultante.
	 *
	 * @exception DerivacionException
	 *		TraduccionException Si se presenta 
	 *		algún error en la derivación.
	 */
	public TNombre derivarNombre()
	throws DerivacionException;

	///////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene el texto fuente asociado actualmente al derivador.
	 *
	 * @return El texto fuente asociado actualmente al derivador.
	 */
	public String obtTextoFuente();

	///////////////////////////////////////////////////////////////////////
	/**
	 * Establece fuente a examinar.
	 *
	 * @param fuente 	El programa fuente como objeto reader.
	 *
	 * @return Este derivador.
	 */
	public IDerivador ponTextoFuente(Reader fuente);

	///////////////////////////////////////////////////////////////////////
	/**
	 * Establece fuente a examinar.
	 *
	 * @param fuente 	El programa fuente como cadena.
	 *
	 * @return Este derivador.
	 */
	public IDerivador ponTextoFuente(String fuente);
}