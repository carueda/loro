package loro.arbol;

import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;

////////////////////////////////////////////////////////////////
/**
 * Interface que todo nodo del árbol sintáctico implementa.
 *
 * @author Carlos Rueda
 */
public interface INodo extends IUbicable, IVisitable, java.io.Serializable
{
}