package loro.arbol;


import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;



//////////////////////////////////////////////////////////////////
/**
 * Interface de todo objeto visitable por un IVisitante.
 *
 * @author Carlos Rueda
 * @version 2002-01-08 refactoring
 */
public interface IVisitable
{

	/////////////////////////////////////////////////////////
	/**
	 * Acepta al visitante.
	 */
	public void aceptar(IVisitante v)
	throws VisitanteException;
	

	


}