package loro.arbol;

import loro.INode;

import java.io.Serializable;

/**
 * Interface que todo nodo del árbol sintáctico implementa.
 * Esta es la interface especifica para el lenguaje Loro.
 *
 * @author Carlos Rueda
 * @version $Id$
 */
public interface INodo extends INode, IVisitable, Serializable {
}