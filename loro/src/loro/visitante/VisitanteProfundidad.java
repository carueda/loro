package loro.visitante;

import loro.arbol.*;

/**
 * Permite recorrer el árbol sintáctico en orden
 * preferente por profunidad.
 * Lo que se hace en cada nodo visitado es simplemente
 * pedirle a los nodos hijos que me acepten como
 * visitante.
 * Implementa completamente la interface IVisitante
 * pero realmente no hace nada útil por si misma.
 * Más bien sirve de base para extensiones que hagan
 * algo más útil.
 *
 * @author Carlos Rueda
 * @version Jun/09/1999
 * @version Sep/21/2001 - NDeclDesc
 */

public class VisitanteProfundidad
			implements IVisitante
{

	/**
	 * Visita una expresión de conversión a cadena.
	 * Pide a la expresión operada que acepte este visitante.
	 */
	public void visitar(NACadena n)
	throws VisitanteException
	{
		n.obtExpresion().aceptar(this);
	}
	/**
	 * Visita una expresión de afirmación.
	 * Pide a la propia expresión que acepte este visitante.
	 */
	public void visitar(NAfirmacion n)
	throws VisitanteException
	{
		n.obtExpresion().aceptar(this);
	}
	/**
	 * Visita la definición de un algoritmo.
	 * Se recorren los nodos hijos así:
	 * la lista de parámetros de entrada,
	 * la lista de parámetros de salida,
	 *  la pareja especificación/algoritmo,
	 * la lista de acciones.
	 */
	public void visitar(NAlgoritmo n)
	throws VisitanteException
	{
		visitarLista(n.obtParametrosEntrada());
		visitarLista(n.obtParametrosSalida());
		visitarLista(n.obtAcciones());
	}
	/**
	 * Visita una expresión de asignación.
	 * Se recorren los nodos hijos así:
	 * el valor izquierdo (l-value),
	 * el valor derecho (r-value),
	 */
	public void visitar(NAsignacion n)
	throws VisitanteException
	{
		n.obtExpresionIzq().aceptar(this);
		n.obtExpresionDer().aceptar(this);
	}
	/**
	 * Visita una expresión de obtención de cardinalidad.
	 * Pide a la expresión operada que acepte este visitante.
	 */
	public void visitar(NCardinalidad n)
	throws VisitanteException
	{
		n.obtExpresion().aceptar(this);
	}
	/**
	 * Visita un caso de una acción según.
	 *  la expresión del caso (si lo hay; si no hay es de "si_no"),
	 * la lista de acciones.
	 */
	public void visitar(NCaso n)
	throws VisitanteException
	{
		if ( n.obtExpresion() != null )
			n.obtExpresion().aceptar(this);
		visitarLista(n.obtAcciones());
	}
	/**
	 * Visita una accion ciclo.
	 * Se recorren los nodos hijos así:
	 * la lista de acciones.
	 */
	public void visitar(NCiclo n)
	throws VisitanteException
	{
		visitarLista(n.obtAcciones());
	}
	/**
	 * Visita la definición de una clase.
	 * Se recorren los nodos hijos así:
	 * la lista de atributos de la clase,
	 * la lista de constructores.
	 */
	public void visitar(NClase n)
	throws VisitanteException
	{
		System.out.println("::visprof.visitarLista(NClase)");
		visitarLista(n.obtParametrosEntrada());
		visitarLista(n.obtConstructores());
	}
	/**
	 * Visita una expresión condicional.
	 * Se visitan en orden las tres expresiones que conforman
	 * un condicional.
	 */
	public void visitar(NCondicion n)
	throws VisitanteException
	{
		n.obtCondicion().aceptar(this);
		n.obtPrimeraAlternativa().aceptar(this);
		n.obtSegundaAlternativa().aceptar(this);
	}
	/**
	 * Visita la definición de un constructor.
	 * Se recorren los nodos hijos así:
	 * la lista de parámetros de entrada,
	 * la lista de descripciones de entrada,
	 * la expresión de precondición (si hay),
	 * la expresión de poscondición (si hay),
	 * la lista de parámetros de salida,
	 * la lista de acciones.
	 */
	public void visitar(NConstructor n)
	throws VisitanteException
	{
		if ( n.esPorDefecto() )
			return;

		visitarLista(n.obtParametrosEntrada());
		visitarLista(n.obtDescripcionesEntrada());
		if ( n.obtPrecondicion() != null )
			n.obtPrecondicion().aceptar(this);
		if ( n.obtPoscondicion() != null )
			n.obtPoscondicion().aceptar(this);
		visitarLista(n.obtAcciones());
	}
	/**
	 * Visita una accion continue.
	 */
	public void visitar(NContinue n)
	throws VisitanteException
	{}
	/**
	 * Visita una expresión de conversión de tipo.
	 * Pide a la expresión operada que acepte este visitante.
	 */
	public void visitar(NConvertirTipo n)
	throws VisitanteException
	{
		n.obtExpresion().aceptar(this);
	}
	/**
	 * Visita una expresión binaria de corrimiento >>.
	 * Pide en orden a las expresiones operadas que acepten este visitante.
	 */
	public void visitar(NCorrDer n)
	throws VisitanteException
	{
		n.obtExpresionIzq().aceptar(this);
		n.obtExpresionDer().aceptar(this);
	}
	/**
	 * Visita una expresión binaria de corrimiento >>>.
	 * Pide en orden a las expresiones operadas que acepten este visitante.
	 */
	public void visitar(NCorrDerDer n)
	throws VisitanteException
	{
		n.obtExpresionIzq().aceptar(this);
		n.obtExpresionDer().aceptar(this);
	}
	/**
	 * Visita una expresión binaria de corrimiento &lt;&lt;.
	 * Pide en orden a las expresiones operadas que acepten este visitante.
	 */
	public void visitar(NCorrIzq n)
	throws VisitanteException
	{
		n.obtExpresionIzq().aceptar(this);
		n.obtExpresionDer().aceptar(this);
	}
	/**
	 * Visita una expresión binaria de creación de arreglo.
	 * Pide en orden a las expresiones operadas que acepten este visitante.
	 */
	public void visitar(NCrearArreglo n)
	throws VisitanteException
	{
		n.obtExpresionIzq().aceptar(this);
		n.obtExpresionDer().aceptar(this);
	}
	
	/**
	 * Visita una expresión arreglo.
	 * Se visita la lista de expresiones.
	 */
	public void visitar(NExpresionArreglo n)
	throws VisitanteException
	{
		visitarLista(n.obtExpresiones());
	}
	
	/**
	 * Visita el tipo de base en creación de arreglo.
	 * No hace nada.
	 */
	public void visitar(NCrearArregloTipoBase n)
	throws VisitanteException
		{}
	/**
	 * Visita una expresión de creación de objeto.
	 * Solo visita en orden los argumentos del constructor.
	 */
	public void visitar(NCrearObjeto n)
	throws VisitanteException
	{
		visitarLista(n.obtArgumentos());
	}
	/**
	 * Visita una expresión cuantificada.
	 * Se recorren los nodos hijos así:
	 * la lista de declaraciones,
	 * la expresión ''con'' (si hay),
	 * la expresión propia.
	 */
	public void visitar(NCuantificado n)
	throws VisitanteException
	{
		visitarLista(n.obtDeclaraciones());
		if ( n.obtExpresionCon() != null )
			n.obtExpresionCon().aceptar(this);
		n.obtExpresion().aceptar(this);
	}
	/**
	 * Visita una acción de decisión.
	 * Se recorren los nodos hijos así:
	 * la expresión de condición,
	 * la lista de acciones para cierto,
	 * la lista de fragmentos "si_no_si",
	 * la lista de acciones para falso (si la hay).
	 */
	public void visitar(NDecision n)
	throws VisitanteException
	{
		n.obtCondicion().aceptar(this);
		visitarLista(n.obtAccionesCierto());
		visitarLista(n.obtSiNoSis());
		if ( n.obtAccionesFalso() != null )
			visitarLista(n.obtAccionesFalso());
	}
	/**
	 * Visita un fragmento "si_no_si" de una decisión.
	 * Se recorren los nodos hijos así:
	 * la expresión de condición,
	 * la lista de acciones.
	 */
	public void visitar(NDecisionSiNoSi n)
	throws VisitanteException
	{
		n.obtCondicion().aceptar(this);
		visitarLista(n.obtAcciones());
	}
	/**
	 * Visita una acción de decisión múltiple (según).
	 * Se recorren los nodos hijos así:
	 * la expresión de control,
	 * la lista de casos,
	 * el caso "si_no" (si lo hay).
	 */
	public void visitar(NDecisionMultiple n)
	throws VisitanteException
	{
		n.obtExpresion().aceptar(this);
		visitarLista(n.obtCasos());
		if ( n.obtCasoSiNo() != null )
			n.obtCasoSiNo().aceptar(this);
	}
	/**
	 * Visita una declaración de uno o más identificadores.
	 * Sólo se visita la expresión correspondiente cuando
	 * se trata de inicialización.
	 */
	public void visitar(NDeclaracion n)
	throws VisitanteException
	{
		if ( n.obtExpresion() != null )
			n.obtExpresion().aceptar(this);
	}
	/**
	 * Visita una declaracion-descripción.
	 * No hace nada.
	 */
	public void visitar(NDeclDesc n)
	throws VisitanteException
	{
	}
	/**
	 * Visita una descripción.
	 * No hace nada.
	 */
	public void visitar(NDescripcion n)
	throws VisitanteException
	{}
	/**
	 * Visita una expresión binaria de !=.
	 * Pide en orden a las expresiones operadas que acepten este visitante.
	 */
	public void visitar(NDiferente n)
	throws VisitanteException
	{
		n.obtExpresionIzq().aceptar(this);
		n.obtExpresionDer().aceptar(this);
	}
	/**
	 * Visita una expresión binaria de /.
	 * Pide en orden a las expresiones operadas que acepten este visitante.
	 */
	public void visitar(NDivReal n)
	throws VisitanteException
	{
		n.obtExpresionIzq().aceptar(this);
		n.obtExpresionDer().aceptar(this);
	}
	/**
	 * Visita una expresión binaria de <=>.
	 * Pide en orden a las expresiones operadas que acepten este visitante.
	 */
	public void visitar(NEquivalencia n)
	throws VisitanteException
	{
		n.obtExpresionIzq().aceptar(this);
		n.obtExpresionDer().aceptar(this);
	}
	/**
	 * Visita una expresion "es_instancia_de".
	 */
	public void visitar(NEsInstanciaDe n)
	throws VisitanteException
	{
		n.obtExpresion().aceptar(this);
		// PENDIETE   n.obtTipoRevisado() ....
	}
	/**
	 * Visita la definición de una especificación.
	 * Se recorren los nodos hijos así:
	 * la lista de parámetros de entrada,
	 * la lista de parámetros de salida,
	 * la lista de descripciones de entrada,
	 * la lista de descripciones de salida,
	 * la expresión de precondición (si hay),
	 * la expresión de poscondición (si hay).
	 */
	public void visitar(NEspecificacion n)
	throws VisitanteException
	{
		visitarLista(n.obtParametrosEntrada());
		visitarLista(n.obtParametrosSalida());
		visitarLista(n.obtDescripcionesEntrada());
		visitarLista(n.obtDescripcionesSalida());
		if ( n.obtPrecondicion() != null )
			n.obtPrecondicion().aceptar(this);
		if ( n.obtPoscondicion() != null )
			n.obtPoscondicion().aceptar(this);
	}
	/**
	 * Visita un fuente de compilación.
	 */
	public void visitar(NFuente n)
	throws VisitanteException
	{
		visitarLista((Nodo[]) n.obtUnidades());
	}
	/**
	 * Visita una expresión id.
	 * No hace nada.
	 */
	public void visitar(NId n)
	throws VisitanteException
		{}
	/**
	 * Visita una expresión binaria de =.
	 * Pide en orden a las expresiones operadas que acepten este visitante.
	 */
	public void visitar(NIgual n)
	throws VisitanteException
	{
		n.obtExpresionIzq().aceptar(this);
		n.obtExpresionDer().aceptar(this);
	}
	/**
	 * Visita una expresión binaria de =>.
	 * Pide en orden a las expresiones operadas que acepten este visitante.
	 */
	public void visitar(NImplicacion n)
	throws VisitanteException
	{
		n.obtExpresionIzq().aceptar(this);
		n.obtExpresionDer().aceptar(this);
	}
	/**
	 * Visita una expresión de invocación.
	 * Se recorren los nodos hijos así:
	 * la expresión que identifica a quien se invoca,
	 * la lista de expresiones argumentos.
	 */
	public void visitar(NInvocacion n)
	throws VisitanteException
	{
		n.obtExpresion().aceptar(this);
		visitarLista(n.obtArgumentos());
	}
	//////////////////////////////////////////////////////////////////
	/**
	 * Visita una definicion de interface.
	 * Se visitan las operaciones declaradas.
	 */
	public void visitar(NInterface n)
	throws VisitanteException
	{
		System.out.println("::visprof.visitarLista(NInterface)");
		visitarLista(n.obtOperacionesDeclaradas());
	}
	/**
	 * Visita una expresión literal booleano.
	 * No hace nada.
	 */
	public void visitar(NLiteralBooleano n)
	throws VisitanteException
		{}
	/**
	 * Visita una expresión literal cadena.
	 * No hace nada.
	 */
	public void visitar(NLiteralCadena n)
	throws VisitanteException
		{}
	/**
	 * Visita una expresión literal caracter.
	 * No hace nada.
	 */
	public void visitar(NLiteralCaracter n)
	throws VisitanteException
		{}
	/**
	 * Visita una expresión literal entero.
	 * No hace nada.
	 */
	public void visitar(NLiteralEntero n)
	throws VisitanteException
		{}
	/**
	 * Visita una expresión literal nulo.
	 * No hace nada.
	 */
	public void visitar(NLiteralNulo n)
	throws VisitanteException
		{}
	/**
	 * Visita una expresión literal real.
	 * No hace nada.
	 */
	public void visitar(NLiteralReal n)
	throws VisitanteException
		{}
	/**
	 * Visita una expresión binaria de +.
	 * Pide en orden a las expresiones operadas que acepten este visitante.
	 */
	public void visitar(NMas n)
	throws VisitanteException
	{
		n.obtExpresionIzq().aceptar(this);
		n.obtExpresionDer().aceptar(this);
	}
	/**
	 * Visita una expresión binaria de >.
	 * Pide en orden a las expresiones operadas que acepten este visitante.
	 */
	public void visitar(NMayor n)
	throws VisitanteException
	{
		n.obtExpresionIzq().aceptar(this);
		n.obtExpresionDer().aceptar(this);
	}
	/**
	 * Visita una expresión binaria de >=.
	 * Pide en orden a las expresiones operadas que acepten este visitante.
	 */
	public void visitar(NMayorIgual n)
	throws VisitanteException
	{
		n.obtExpresionIzq().aceptar(this);
		n.obtExpresionDer().aceptar(this);
	}
	/**
	 * Visita una expresión binaria de &lt;.
	 * Pide en orden a las expresiones operadas que acepten este visitante.
	 */
	public void visitar(NMenor n)
	throws VisitanteException
	{
		n.obtExpresionIzq().aceptar(this);
		n.obtExpresionDer().aceptar(this);
	}
	/**
	 * Visita una expresión binaria de &lt;=.
	 * Pide en orden a las expresiones operadas que acepten este visitante.
	 */
	public void visitar(NMenorIgual n)
	throws VisitanteException
	{
		n.obtExpresionIzq().aceptar(this);
		n.obtExpresionDer().aceptar(this);
	}
	/**
	 * Visita una expresión binaria de -.
	 * Pide en orden a las expresiones operadas que acepten este visitante.
	 */
	public void visitar(NMenos n)
	throws VisitanteException
	{
		n.obtExpresionIzq().aceptar(this);
		n.obtExpresionDer().aceptar(this);
	}
	/**
	 * Visita una acción mientras.
	 * Se recorren los nodos hijos así:
	 * la expresión de condición,
	 * la lista de acciones.
	 */
	public void visitar(NMientras n)
	throws VisitanteException
	{
		n.obtCondicion().aceptar(this);
		visitarLista(n.obtAcciones());
	}
	/**
	 * Visita una expresión binaria de %.
	 * Pide en orden a las expresiones operadas que acepten este visitante.
	 */
	public void visitar(NMod n)
	throws VisitanteException
	{
		n.obtExpresionIzq().aceptar(this);
		n.obtExpresionDer().aceptar(this);
	}
	/**
	 * Visita una expresión unaria -.
	 * Pide a la expresión operada que acepte este visitante.
	 */
	public void visitar(NNeg n)
	throws VisitanteException
	{
		n.obtExpresion().aceptar(this);
	}
	/**
	 * Visita una expresión unaria !.
	 * Pide a la expresión operada que acepte este visitante.
	 */
	public void visitar(NNo n)
	throws VisitanteException
	{
		n.obtExpresion().aceptar(this);
	}
	/**
	 * Visita una expresión unaria ~.
	 * Pide a la expresión operada que acepte este visitante.
	 */
	public void visitar(NNoBit n)
	throws VisitanteException
	{
		n.obtExpresion().aceptar(this);
	}
	/**
	 */
	public void visitar(NNombre n)
	throws VisitanteException
		{}
	/**
	 * Visita una expresión binaria de ||.
	 * Pide en orden a las expresiones operadas que acepten este visitante.
	 */
	public void visitar(NO n)
	throws VisitanteException
	{
		n.obtExpresionIzq().aceptar(this);
		n.obtExpresionDer().aceptar(this);
	}
	/**
	 * Visita una expresión binaria de |.
	 * Pide en orden a las expresiones operadas que acepten este visitante.
	 */
	public void visitar(NOArit n)
	throws VisitanteException
	{
		n.obtExpresionIzq().aceptar(this);
		n.obtExpresionDer().aceptar(this);
	}
	/**
	 * Visita una expresión binaria de ^.
	 * Pide en orden a las expresiones operadas que acepten este visitante.
	 */
	public void visitar(NOExc n)
	throws VisitanteException
	{
		n.obtExpresionIzq().aceptar(this);
		n.obtExpresionDer().aceptar(this);
	}
	/**
	 * PENDIENTE
	 */
	public void visitar(NPaquete n)
	throws VisitanteException
		{}
	/**
	 * Visita una acción para.
	 * Se recorren los nodos hijos así:
	 * la declaración (si la hay),
	 * la expresión "desde",
	 * la expresión de paso (si la hay),
	 * la expresión "hasta",
	 * la lista de acciones.
	 */
	public void visitar(NPara n)
	throws VisitanteException
	{
		if ( n.obtDeclaracion() != null )
			n.obtDeclaracion().aceptar(this);
		n.obtExpresionDesde().aceptar(this);
		if ( n.obtExpresionPaso() != null )
			n.obtExpresionPaso().aceptar(this);
		n.obtExpresionHasta().aceptar(this);
		visitarLista(n.obtAcciones());
	}
	/**
	 * Visita una expresión unaria +.
	 * Pide a la expresión operada que acepte este visitante.
	 */
	public void visitar(NPlus n)
	throws VisitanteException
	{
		n.obtExpresion().aceptar(this);
	}
	/**
	 * Visita una expresión binaria de *.
	 * Pide en orden a las expresiones operadas que acepten este visitante.
	 */
	public void visitar(NPor n)
	throws VisitanteException
	{
		n.obtExpresionIzq().aceptar(this);
		n.obtExpresionDer().aceptar(this);
	}
	/**
	 * Visita una acción repita.
	 * Se recorren los nodos hijos así:
	 * la lista de acciones,
	 * la expresión de condición.
	 */
	public void visitar(NRepita n)
	throws VisitanteException
	{
		visitarLista(n.obtAcciones());
		n.obtCondicion().aceptar(this);
	}
	/**
	 * Visita una acción retorne.
	 * Se recorren los nodos hijos así:
	 * la lista de expresiones.
	 */
	public void visitar(NRetorne n)
	throws VisitanteException
	{
		visitarLista(n.obtExpresiones());
	}
	/**
	 * Visita una expresión de referencia a atributo.
	 * Se visita la expresión objeto.
	 */
	public void visitar(NSubId n)
	throws VisitanteException
	{
		n.obtExpresion().aceptar(this);
	}
	/**
	 * Visita una expresión binaria de subindexación.
	 * Pide en orden a las expresiones operadas que acepten este visitante.
	 */
	public void visitar(NSubindexacion n)
	throws VisitanteException
	{
		n.obtExpresionIzq().aceptar(this);
		n.obtExpresionDer().aceptar(this);
	}
	/**
	 * Visita una accion termine.
	 */
	public void visitar(NTermine n)
	throws VisitanteException
		{}
	/**
	 */
	public void visitar(NUtiliza n)
	throws VisitanteException
		{}
	/**
	 * Visita una expresión binaria de &&.
	 * Pide en orden a las expresiones operadas que acepten este visitante.
	 */
	public void visitar(NY n)
	throws VisitanteException
	{
		n.obtExpresionIzq().aceptar(this);
		n.obtExpresionDer().aceptar(this);
	}
	/**
	 * Visita una expresión binaria de &.
	 * Pide en orden a las expresiones operadas que acepten este visitante.
	 */
	public void visitar(NYArit n)
	throws VisitanteException
	{
		n.obtExpresionIzq().aceptar(this);
		n.obtExpresionDer().aceptar(this);
	}
	/**
	 * Auxiliar que permite visitar en orden una lista de nodos.
	 */
	public void visitarLista(Nodo[] nodos)
	throws VisitanteException
	{
		if ( nodos == null ) {
			return;

		}
		for ( int i = 0; i < nodos.length; i++ )
			nodos[i].aceptar(this);
	}

	////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NTipoArreglo n)
	throws VisitanteException
	{
		
	}

	////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NTipoBooleano n)
	throws VisitanteException
	{
		
	}

	////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NTipoCadena n)
	throws VisitanteException
	{
		
	}

	////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NTipoCaracter n)
	throws VisitanteException
	{
		
	}

	////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NTipoClase n)
	throws VisitanteException
	{
		
	}

	////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NTipoEntero n)
	throws VisitanteException
	{
		
	}

	////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NTipoEspecificacion n)
	throws VisitanteException
	{
		
	}

	////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NTipoReal n)
	throws VisitanteException
	{
		
	}

	//////////////////////////////////////////////////////
	/**
	 * Visita una expresion "implementa".
	 */
	public void visitar(NImplementa n)
	throws VisitanteException
	{
		n.obtExpresion().aceptar(this);
		// PENDIETE   n.obtTipoRevisado() ....
	}

	/////////////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NTipoInterface n)
	throws VisitanteException
	{
	}
}