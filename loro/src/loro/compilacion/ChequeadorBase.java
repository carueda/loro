package loro.compilacion;

import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;
import loro.arbol.*;
import loro.util.ManejadorUnidades;
import loro.util.Util;
import loro.tabsimb.*;
import loro.tipo.*;
import loro.Rango;


import java.util.*;

/////////////////////////////////////////////////////////////////////
/**
 * Servicios de base para el visitante chequeador.
 *
 * @author Carlos Rueda
 * @version 2002-05-18
 */
abstract class ChequeadorBase implements IVisitante
{
	/**
	 * Nombre del archivo fuente en compilacion.
	 */
	String nombreFuente;

	/** La tabla de simbolos. */
	TablaSimbolos tabSimb;
	
	/**
	 * La marca inicial en la tabla de simbolos. 
	 * A este punto se vuelve cuando se invoca reiniciar().
	 */
	int marca_inicial;

	/** La clase actualmente en chequeo. */
	NClase claseActual;

	/** La interface actualmente en chequeo. */
	NInterface interfaceActual;

	/** En invocacion para chequeo de NId? */
	boolean enInvocacion;

	/**
	 * En una expresión para afirmación?
	 */
	boolean enAfirmacion;

	/**
	 * El tipo de la expresion de un segun requerido por
	 * sus casos. PENDIENTE REVISAR ANIDAMIENTO
	 */
	Tipo tipoSegun;

	/** Unidad actual. */
	NUnidad unidadActual;

	/**
	 * Paquete asociado a las unidades en compilacion.
	 */
	String[] paqueteActual;

	/** Si no null, indica el nombr del paquete que debe encontrarse. */
	String expectedPackageName = null;

	/**
	 * Asociaciones nombreSimple/nombreCompuesto de las
	 * unidades de compilacion traidas por "utiliza".
	 */
	Hashtable utiliza;

	/**
	 * Manejador de unidades para almacenamiento.
	 */
	ManejadorUnidades mu;

	/**
	 * Los algorimos que han sido definidos dentro de la
	 * compilacion actual.
	 */
	Hashtable algs;

	/**
	 * Las especificaciones que han sido definidas dentro de la
	 * compilacion actual.
	 */
	Hashtable esps;

	/**
	 * Las clases que han sido definidas dentro de la
	 * compilacion actual.
	 */
	Hashtable clases;

	/**
	 * Las interfaces que han sido definidas dentro de la
	 * compilacion actual.
	 */
	Hashtable interfaces;

	/**
	 * Pila de etiquetas. Para toda accion de iteracion se mete
	 * una etiqueta, sea la explicitamente indicada por el usuario,
	 * o un comodin ,"@", para poder controlar acciones "termine"
	 * o "continue" sin etiqueta, es decir, para chequear que no
	 * aparezcan estas ultimas por fuera de al menos una iteracion.
	 */
	Stack labels;


	/**
	 * Número de excepciones IdIndefinidoException producidas en una pasada.
	 */
	int numIdIndefinidos;

	/**
	 * Indicador al visitar alguna unidad para que permita la
	 * excepción IdIndefinido.
	 */
	boolean permitirIdIndefinido;

	///////////////////////////////////////////////////////////////
	/**
	 * Crea un chequeador.
	 */
	protected ChequeadorBase()
	{
		this(new TablaSimbolos(), null);
	}
	///////////////////////////////////////////////////////////////
	/**
	 * Crea un chequeador.
	 * La tabla de simbolos se marca inmediatamente; esta marca es
	 * utilizada en reiniciar().
	 *
	 * @param tabSimbBase	Tabla de simbolos a tomar como de base.
	 */
	protected ChequeadorBase(TablaSimbolos tabSimbBase, NUnidad unidadActual)
	{
		nombreFuente = "";

		tabSimb = tabSimbBase;
		marca_inicial = tabSimb.marcar();
		this.unidadActual = unidadActual;

		utiliza = new Hashtable();
		mu = ManejadorUnidades.obtManejadorUnidades();
		algs = new Hashtable();
		esps = new Hashtable();
		clases = new Hashtable();
		interfaces = new Hashtable();
		labels = new Stack();

		reiniciar();
	}

	//////////////////////////////////////////////////
	/**
	 * Servicio para chequear un nodo.
	 *
	 * Se le pide al nodo la aceptacion de este chequeador.  
	 * Dicha operación se declara como posible generadora de
	 * VisitanteException; este servicio hace más precisa esta
	 * posibilidad declarando posible ChequeadorException.
	 *
	 * NOTA: Normalmente se invocará reiniciar() antes de este
	 * servicio.
	 */
	public void chequear(Nodo n)
	throws ChequeadorException
	{
		try
		{
			n.aceptar(this);
		}
		catch(ChequeadorException ex)
		{
			throw ex;   // OK, es lo esperado
		}
		catch(VisitanteException ex)
		{
			// no debe suceder
			throw new Error("No debe suceder: excepcion es VisitanteException");
		}
	}

	///////////////////////////////////////////////////////////////
	/**
	 * Agrega un algoritmo a la compilacion.
	 */
	protected void _agregarAlgoritmo(NAlgoritmo n)
	{
		String nombreSimple = n.obtNombreSimpleCadena();
		algs.put(nombreSimple, n);
	}
	///////////////////////////////////////////////////////////////
	/**
	 * Agrega una clase a la compilacion.
	 */
	protected void _agregarClase(NClase n)
	{
		String nombreSimple = n.obtNombreSimpleCadena();
		clases.put(nombreSimple, n);
	}
	//////////////////////////////////////////////////////////////////////
	/**
	 * Agrega una especificacion a la compilacion.
	*/
	protected void _agregarEspecificacion(NEspecificacion n)
	{
		String nombreSimple = n.obtNombreSimpleCadena();
		esps.put(nombreSimple, n);
	}
	///////////////////////////////////////////////////////////////
	/**
	 * Agrega una interface a la compilacion.
	 */
	protected void _agregarInterface(NInterface n)
	{
		String nombreSimple = n.obtNombreSimpleCadena();
		interfaces.put(nombreSimple, n);
	}
	////////////////////////////////////////////////////////////////
	/**
	 * Dice si una clase es igual o es subclase de otra.
	 */
	protected boolean _aKindOf(Nodo n, TipoClase tc1, TipoClase tc2)
	throws ChequeadorException
	{
		// tome el nombre de la super clase:
		String[] n2 = tc2.obtNombreConPaquete();
		String t2 = Util.obtStringRuta(n2);
		NClase c2 = mu.obtClase(t2);

		// tome el nombre de la clase base:
		String[] n1 = tc1.obtNombreConPaquete();
		String t1 = Util.obtStringRuta(n1);
		NClase clase = mu.obtClase(t1);
		
		while ( clase != null )
		{
			if ( t1.equals(t2) )
			{
				return true;
			}

			// intente por super clase:
			clase = _obtSuperClase(n, clase);
			if ( clase != null )
			{
				t1 = clase.obtNombreCompletoCadena();
			}
		}

		return false;
	}
	///////////////////////////////////////////////////////////////
	/**
	 * Busca una etiqueta en la pila de etiquetas.
	 */
	protected String _buscarEtiqueta(String image)
	{
		for ( int i = labels.size() -1; i >= 0; i-- )
		{
			String prev_image = (String) labels.elementAt(i);
			if ( prev_image.equals(image) )
				return prev_image;
		}
		return null;
	}
	///////////////////////////////////////////////////////////////////////////
	/**
	 * Chequea que a una variable de cierto tipo se le puede asignar una cierta 
	 * expresion.
	 *
	 * @param n             Nodo de contexto para posible error.
	 * @param var_tipo      Tipo de la variable.
	 * @param expr_tipo     Tipo de la expresion.
	 */
	protected void _chequearAsignabilidad(Nodo n, Tipo var_tipo, Tipo expr_tipo)
	throws VisitanteException
	{
		boolean asignable;
		try
		{
			asignable = var_tipo.esAsignable(expr_tipo);
		}
		catch (ClaseNoEncontradaException ex)
		{
			throw new ChequeadorException(
				n,
				"No se encuentra '" +ex.obtNombre()+ "'"
			);
		}

		if ( !asignable )
		{
			// Queda la posibilidad que los tipos en cuestión estén
			// relacionados con la unidad actual en compilación.
			// Particularmente, si ésta es una clase:
			if ( claseActual != null
			&&   var_tipo.esClase()  && expr_tipo.esClase() )
			{
				String cn = claseActual.obtNombreCompletoCadena();
				String vn = ((TipoClase) var_tipo).obtNombreCompletoString();
				String en = ((TipoClase) expr_tipo).obtNombreCompletoString();
				asignable = cn.equals(vn) && vn.equals(en);
			}
		}
		
		if ( !asignable )
		{
			throw new ChequeadorException(
				n,
				"No se puede convertir '" +expr_tipo+ "' a '" +var_tipo+ "'"
			);
		}
	}
	///////////////////////////////////////////////////////////////
	/**
	 * Verifica la definición de una etiqueta.
	 * Si etq == null, la verificación es satisfecha inmediatamente
	 * retornando comodin "@". En caso contrario se verifica la
	 * no repetición.
	 * 
	 * @return Identificador que debería meterse posteriormente en la
	 *         pila de etiquetas.
	 */
	protected String _chequearDefinicionEtiqueta(TId etq)
	throws VisitanteException
	{
		// comodin en caso que etq == null:
		String image = "@";
		if ( etq != null )
		{
			image = etq.obtId();
			if ( _buscarEtiqueta(image) != null )
			{
				throw new ChequeadorException(etq,
					"La etiqueta ya esta definida: " +etq
				);
			}
			if ( Util.esVarSemantica(image) )
			{
				throw new ChequeadorException(etq,
					"Una etiqueta no puede ser en estilo de variable semántica: "
					+etq
				);
			}
		}

		return image;
	}
	///////////////////////////////////////////////////////
	/**
	 */
	protected void _chequearEtiqueta(boolean termine, TId etq, IUbicable u)
	throws VisitanteException
	{
		if ( etq != null )
		{
			if ( _buscarEtiqueta(etq.obtId()) == null )
			{
				throw new ChequeadorException(etq,
					"La etiqueta no esta definida: " +etq
				);
			}
		}
		else if ( labels.isEmpty() )
		{
			throw new ChequeadorException(u,
				"Ubicacion invalida de una accion '" +
				(termine ? "termine" : "continue")+ "'"
			);
		}
	}
	////////////////////////////////////////////////////////////////////////
	/**
	 * Auxiliar para chequear con base en los tipos
	 * de las expresiones.
	 */
	protected void _chequearIgualdad(IUbicable u, String op, Tipo t1, Tipo t2)
	throws VisitanteException
	{
		if ( t1.igual(t2) )
			return;		// ok

		if ( t1.esNulo() && t2.esNulo()
		||   t2.esNulo() && (t1.esObjeto() || t1.esCadena() || t1.esAlgoritmo())
		||   t1.esNulo() && (t2.esObjeto() || t2.esCadena() || t2.esAlgoritmo()) )
			return;

		_operadorBinNoDefinido(u, op, t1, t2);
	}
	////////////////////////////////////////////////////////////////
	/**
	 * Dice si una expresión es constante.
	 *
	 * @since 0.7s2
	 */
	protected boolean _esConstanteExpresion(NExpresion expr)
	{
		if ( expr instanceof NSubId )
		{
			NSubId subId = (NSubId) expr;
			NExpresion e = subId.obtExpresion();
			TId id = subId.obtId();
	
			if ( e.esConstante() )
			{
				return true;
			}
	
			Tipo tipo = e.obtTipo();
			
			if ( tipo instanceof TipoClase )
			{
				TipoClase tc = (TipoClase) tipo;
				String[] nombre = tc.obtNombreConPaquete();
				String snombre = Util.obtStringRuta(nombre);
				NClase clase = mu.obtClase(snombre);
				
				return clase.esAtributoConstante(id.obtId());
			}
			else if ( tipo.esArreglo() )
			{
				return true;
			}
			else if ( tipo.esCadena() )
			{
				return true;   // Expresion cadena es inmutable
			}
			else
			{
				throw new RuntimeException(
					"Anomalía: Se espera objeto, arreglo o cadena. "+
					"(Encontrado " +tipo+ ")"
				);
			}
		}
		else if ( expr instanceof NSubindexacion )
		{
			NExpresion e = ((NSubindexacion) expr).obtExpresionIzq();
			if ( e.obtTipo().esCadena() )
			{
				return true;   // Expresion cadena es inmutable
			}
		}

		return expr.esConstante();
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 * Borra, si existe, el archivo que correspondería a una unidad compilada.
	 */
	protected void _borrarCompilado(NUnidad n)
	{
		String nombre = n.obtNombreCompletoCadena(java.io.File.separator);
		if ( n instanceof NEspecificacion )
			mu.borrarEspecificacion((NEspecificacion) n);
		else if ( n instanceof NAlgoritmo )
			mu.borrarAlgoritmo((NAlgoritmo) n);
		else if ( n instanceof NClase )
			mu.borrarClase((NClase) n);
		else // ( n instanceof NInterface )
			mu.borrarInterface((NInterface) n);
	}
	
	/////////////////////////////////////////////////////////////////////
	/**
	 * Guarda una unidad compilada.
	 */
	protected void _guardarCompilado(NUnidad n)
	{
		String nombre = n.obtNombreCompletoCadena(java.io.File.separator);
		try
		{
			if ( n instanceof NEspecificacion )
				mu.ponEspecificacion((NEspecificacion) n);
			else if ( n instanceof NAlgoritmo )
				mu.ponAlgoritmo((NAlgoritmo) n);
			else if ( n instanceof NClase )
				mu.ponClase((NClase) n);
			else // ( n instanceof NInterface )
				mu.ponInterface((NInterface) n);

		}
		catch (java.io.IOException e)
		{
			throw new RuntimeException(
				"Error al guardar compilado.\n"+
				"rutaBaseCompilado=[" +mu.obtDirGuardarCompilado()+ "]:\n" +
				"Verifique directorio destino.\n" +
				"e.getMessage()=" +e.getMessage()
			);
		}
	}
	////////////////////////////////////////////////////////////////////////////////
	/**
	 * Inserta en la tabla de simbolos.
	 * 2002-01-08
	 */
	protected void _insertarEnTablaSimbolos(EntradaTabla et, IUbicable u)
	throws VisitanteException
	{
		try
		{
			tabSimb.insertar(et);
		}
		catch ( TSException ex )
		{
			throw new ChequeadorException(
				u,
				ex.getMessage()
			);
		}
	}
	////////////////////////////////////////////////////////////////
	/**
	 * Obtiene algoritmo para un id.
	 */
	protected NAlgoritmo _obtAlgoritmoParaId(TId id)
	{
		String nombreCompuesto = null;

		if ( unidadActual != null )
		{
			if ( unidadActual instanceof NAlgoritmo
			&&   unidadActual.obtNombreSimpleCadena().equals(id.obtId()) )
			{
				// es el mismo algoritmo actual:
				return (NAlgoritmo) unidadActual;
			}

			// intente pidiendo nombre compuesto correspondiente:
			nombreCompuesto = unidadActual.obtNombreCompuesto("a" +id.obtId());
		}

		if ( nombreCompuesto == null )
		{
			// Intente por los 'utiliza':
			nombreCompuesto = (String) utiliza.get('a' + id.obtId());
			if ( nombreCompuesto == null )
			{
				// Intente por el paquete actual:
				if ( paqueteActual != null )
				{
					nombreCompuesto = Util.obtStringRuta(paqueteActual)+ "::" +id.obtId();
				}
				else
				{
					// Intente por el paquete sin nombre:
					nombreCompuesto = id.obtId();
				}
			}
		}

		NAlgoritmo alg = mu.obtAlgoritmo(nombreCompuesto);

		if ( alg == null )
		{
			// intente por el paquete visible automaticamente:
			nombreCompuesto = mu.obtNombrePaqueteAutomatico()+ "::" +id.obtId();
			alg = mu.obtAlgoritmo(nombreCompuesto);
		}

		return alg;
	}
	////////////////////////////////////////////////////////////////
	/**
	 * Obtiene el algoritmo correspondiente a un nombre.
	 */
	protected NAlgoritmo _obtAlgoritmoParaNombre(TNombre nom)
	{
		TId[] ids = nom.obtIds();
		if ( ids.length == 1 )
		{
			return _obtAlgoritmoParaId(ids[0]);
		}

		String nombreCompuesto = nom.obtCadena();

		return mu.obtAlgoritmo(nombreCompuesto);
	}
	////////////////////////////////////////////////////////////////
	/**
	 * Obtiene clase para un id.
	 */
	protected NClase _obtClaseParaId(TId id)
	{
		String nombreCompuesto = null;

		if ( unidadActual != null )
		{
			if ( unidadActual instanceof NClase
			&&   unidadActual.obtNombreSimpleCadena().equals(id.obtId()) )
			{
				// es el mismo actual:
				return (NClase) unidadActual;
			}

			// intente pidiendo nombre compuesto correspondiente:
			nombreCompuesto = unidadActual.obtNombreCompuesto("c" +id.obtId());
		}

		if ( nombreCompuesto == null )
		{
			// Intente por los 'utiliza':
			nombreCompuesto = (String) utiliza.get('c' + id.obtId());
			if ( nombreCompuesto == null )
			{
				// Intente por el paquete actual:
				if ( paqueteActual != null )
				{
					nombreCompuesto = Util.obtStringRuta(paqueteActual)+ "::" +id.obtId();
				}
				else
				{
					// Intente por el paquete sin nombre:
					nombreCompuesto = id.obtId();
				}
			}
		}

		NClase clase = mu.obtClase(nombreCompuesto);

		if ( clase == null )
		{
			// intente por el paquete visible automaticamente:
			nombreCompuesto = mu.obtNombrePaqueteAutomatico()+ "::" +id.obtId();
			clase = mu.obtClase(nombreCompuesto);
		}

		return clase;
	}
	////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la clase correspondiente a un nombre.
	 */
	protected NClase _obtClaseParaNombre(TNombre nom)
	{
		if ( nom == null )
		{
			// Es el tipo clase generica:
			return null;
		}

		TId[] ids = nom.obtIds();
		if ( ids.length == 1 )
		{
			return _obtClaseParaId(ids[0]);
		}

		String nombreCompuesto = nom.obtCadena();

		return mu.obtClase(nombreCompuesto);
	}
	////////////////////////////////////////////////////////////////
	/**
	 * Obtiene especificacion para un id.
	 */
	protected NEspecificacion _obtEspecificacionParaId(String id)
	{
		String nombreCompuesto = null;

		if ( unidadActual != null )
		{
			if ( unidadActual instanceof NEspecificacion
			&&   unidadActual.obtNombreSimpleCadena().equals(id) )
			{
				// es el mismo actual:
				return (NEspecificacion) unidadActual;
			}

			// intente pidiendo nombre compuesto correspondiente:
			nombreCompuesto = unidadActual.obtNombreCompuesto("e" +id);
		}
		if ( nombreCompuesto == null )
		{
			// Intente por los 'utiliza':
			nombreCompuesto = (String) utiliza.get('e' + id);
			if ( nombreCompuesto == null )
			{
				// Intente por el paquete actual:
				if ( paqueteActual != null )
				{
					nombreCompuesto = Util.obtStringRuta(paqueteActual)+ "::" +id;
				}
				else
				{
					// Intente por el paquete sin nombre:
					nombreCompuesto = id;
				}
			}
		}
		NEspecificacion espec = mu.obtEspecificacion(nombreCompuesto);

		if ( espec == null )
		{
			// intente por el paquete visible automaticamente:
			nombreCompuesto = mu.obtNombrePaqueteAutomatico()+ "::" +id;
			espec = mu.obtEspecificacion(nombreCompuesto);
		}

		return mu.obtEspecificacion(nombreCompuesto);
	}
	////////////////////////////////////////////////////////////////
	/**
	 * Obtiene especificacion para un id.
	 */
	protected NEspecificacion _obtEspecificacionParaId(TId id)
	{
		String nombreCompuesto = null;

		if ( unidadActual != null )
		{
			if ( unidadActual instanceof NEspecificacion
			&&   unidadActual.obtNombreSimpleCadena().equals(id.obtId()) )
			{
				// es el mismo actual:
				return (NEspecificacion) unidadActual;
			}

			// intente pidiendo nombre compuesto correspondiente:
			nombreCompuesto = unidadActual.obtNombreCompuesto("e" +id.obtId());
		}
		if ( nombreCompuesto == null )
		{
			// Intente por los 'utiliza':
			nombreCompuesto = (String) utiliza.get('e' + id.obtId());
			if ( nombreCompuesto == null )
			{
				// Intente por el paquete actual:
				if ( paqueteActual != null )
				{
					nombreCompuesto = Util.obtStringRuta(paqueteActual)+ "::" +id.obtId();
				}
				else
				{
					// Intente por el paquete sin nombre:
					nombreCompuesto = id.obtId();
				}
			}
		}
		NEspecificacion espec = mu.obtEspecificacion(nombreCompuesto);

		if ( espec == null )
		{
			// intente por el paquete visible automaticamente:
			nombreCompuesto = mu.obtNombrePaqueteAutomatico()+ "::" +id.obtId();
			espec = mu.obtEspecificacion(nombreCompuesto);
		}

		return mu.obtEspecificacion(nombreCompuesto);
	}
	////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la especificacion correspondiente a un nombre.
	 */
	protected NEspecificacion _obtEspecificacionParaNombre(String[] nom)
	{
		if ( nom == null )
		{
			// Es el tipo especificacion generica:
			return null;
		}

		if ( nom.length == 1 )
		{
			return _obtEspecificacionParaId(nom[0]);
		}

		String nombreCompuesto = Util.obtStringRuta(nom);

		return mu.obtEspecificacion(nombreCompuesto);
	}
	////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la especificacion correspondiente a un nombre.
	 */
	protected NEspecificacion _obtEspecificacionParaNombre(TNombre nom)
	{
		if ( nom == null )
		{
			// Es el tipo especificacion generica:
			return null;
		}

		TId[] ids = nom.obtIds();
		if ( ids.length == 1 )
		{
			return _obtEspecificacionParaId(ids[0]);
		}

		String nombreCompuesto = nom.obtCadena();

		return mu.obtEspecificacion(nombreCompuesto);
	}
	////////////////////////////////////////////////////////////////
	/**
	 * Obtiene interface para un id.
	 */
	protected NInterface _obtInterfaceParaId(TId id)
	{
		String nombreCompuesto = null;

		if ( unidadActual != null )
		{
			if ( unidadActual instanceof NInterface
			&&   unidadActual.obtNombreSimpleCadena().equals(id.obtId()) )
			{
				// es el mismo actual:
				return (NInterface) unidadActual;
			}

			// intente pidiendo nombre compuesto correspondiente:
			nombreCompuesto = unidadActual.obtNombreCompuesto("i" +id.obtId());
		}

		if ( nombreCompuesto == null )
		{
			// Intente por los 'utiliza':
			nombreCompuesto = (String) utiliza.get('i' + id.obtId());
			if ( nombreCompuesto == null )
			{
				// Intente por el paquete actual:
				if ( paqueteActual != null )
				{
					nombreCompuesto = Util.obtStringRuta(paqueteActual)+ "::" +id.obtId();
				}
				else
				{
					// Intente por el paquete sin nombre:
					nombreCompuesto = id.obtId();
				}
			}
		}

		NInterface interface_ = mu.obtInterface(nombreCompuesto);

		if ( interface_ == null )
		{
			// intente por el paquete visible automaticamente:
			nombreCompuesto = mu.obtNombrePaqueteAutomatico()+ "::" +id.obtId();
			interface_ = mu.obtInterface(nombreCompuesto);
		}

		return interface_;
	}
	////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la interface correspondiente a un nombre.
	 */
	protected NInterface _obtInterfaceParaNombre(TNombre nom)
	{
		TId[] ids = nom.obtIds();
		if ( ids.length == 1 )
		{
			return _obtInterfaceParaId(ids[0]);
		}

		String nombreCompuesto = nom.obtCadena();

		return mu.obtInterface(nombreCompuesto);
	}
	////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la superclase de una clase.
	 *
	 * @param clase La clase a la que se le va a encontrar la superclase.
	 *
	 * @return      La superclase.
	 *              Si la clase corresponde a la clase raiz de Loro,
	 *              se retorna null.
	 */
	protected NClase _obtSuperClase(Nodo n, NClase clase)
	throws ChequeadorException
	{
		try
		{
			return mu.obtSuperClase(clase);
		}
		catch ( ClaseNoEncontradaException ex )
		{
			String nombre_super = ex.obtNombre();
			throw new ChequeadorException(
				n,
				"No se encuentra la super clase '" +nombre_super+ "'"
			);
		}
	}
	////////////////////////////////////////////////////////////////
	protected Tipo _obtTipoAtributo(Nodo n, NClase clase, String nombreAtributo)
	throws ChequeadorException
	{
		// ciclo para mirar superclase si es necesario:
		while ( clase != null )
		{
			Tipo tipo = clase.obtTipoAtributo(nombreAtributo);
			if ( tipo != null )
			{
				return tipo;
			}

			clase = _obtSuperClase(n, clase);
		}

		return null;
	}
	////////////////////////////////////////////////////////////////
	protected void _operadorBinNoDefinido(IUbicable u, String op, Tipo et, Tipo ft)
	throws VisitanteException
	{
		throw new ChequeadorException(
			u,
			"Operador " +op+ " no definido para (" +et+ "," +ft+ ")"
		);
	}
	////////////////////////////////////////////////////////////////
	protected void _operadorUnNoDefinido(IUbicable u, String op, Tipo et)
	throws VisitanteException
	{
		throw new ChequeadorException(
			u,
			"Operador " +op+ " no definido para " +
			(et.esNulo() ? "referencia nula" : et.toString())
		);
	}
	////////////////////////////////////////////////////////////////
	/**
	 * Auxiliar para buscar definicion de id en ambiente.
	 * Si el id es resuelto en el ambiente, se le indica a la
	 * unidad actual una asociacion entre este nombre
	 * simple y el nombre completo de la unidad referenciada
	 * por dicho nombre.
	 *
	 * Retorna true sii encuentra id en ambiente.
	 */
	protected boolean _resolverIdEnAmbiente(NId n)
	throws VisitanteException
	{
		TId id = n.obtId();
		if ( Util.esVarSemantica(id.obtId()) )
			return false;

		NUnidad uni;
		if ( null != (uni = _obtAlgoritmoParaId(id))
	//	||   null != (uni = _obtClaseParaId(id))   <- No se permite el nombre de una clase
	//	||   null != (uni = _obtEspecificacionParaId(id)) 
			// PENDIENTE eliminar el par de lineas comentadas arriba
		)
		{
			// ponga asociacion nombre simple/compuesto
			// a la unidad actual:
			String compuesto = uni.obtNombreCompletoCadena();

			String pref;
			if ( uni instanceof NAlgoritmo )
			{
				pref = "a";
				NAlgoritmo alg = (NAlgoritmo) uni;
				n.ponTipo(Tipo.especificacion(alg.obtNombreEspecificacion()));
			}
			else if ( uni instanceof NClase )
			{
				pref = "c";
				NClase clase = (NClase) uni;
				n.ponTipo(Tipo.clase(clase.obtNombreCompleto()));
			}
			else // uni instanceof NEspecificacion.
			{
				throw new RuntimeException("Esperado NAlgoritmo o NClase");
				//pref = "e";
				//n.ponTipo(new TipoEspecificacion((NEspecificacion) uni));
			}

			if ( unidadActual != null )
			{
				unidadActual.ponNombreCompuesto(pref + id.obtId(), compuesto);
			}

			return true;
		}

		return false;
	}
	////////////////////////////////////////////////////////////////
	/**
	 * Auxiliar para buscar definicion de id en tabla de simbolos.
	 * Retorna true sii encuentra id en tabla de simbolos..
	 */
	protected boolean _resolverIdEnTablaSimbolos(NId n)
	throws VisitanteException
	{
		TId id = n.obtId();
		EntradaTabla et = tabSimb.buscar(id.obtId());
		if ( et != null )
		{
			if ( ! et.obtAsignado() )
			{
				throw new ChequeadorException(id, 
					"Variable '" +id+ "' no tiene asignacion"
				);
			}
			n.ponTipo(et.obtTipo());
			n.ponEsConstante(et.esConstante());
			return true;
		}
		return false;
	}
	//////////////////////////////////////////////////////////////////
	/**
	 * Auxiliar para buscar definicion de un id en invocación
	 * en ambiente.
	 * Si el id es resuelto en el ambiente, se le indica a la
	 * unidad actual una asociacion entre este nombre
	 * simple y el nombre completo de la unidad referenciada
	 * por dicho nombre.
	 *
	 * Retorna true sii encuentra id en ambiente.
	 */
	protected boolean _resolverInvocacionIdEnAmbiente(NId n)
	throws VisitanteException
	{
		TId id = n.obtId();
		if ( Util.esVarSemantica(id) )
		{
			return false;
		}

		NAlgoritmo alg = _obtAlgoritmoParaId(id);
		if ( alg != null )
		{
			// ponga asociacion nombre simple/compuesto
			// a la unidad actual:
			String compuesto = alg.obtNombreCompletoCadena();

			n.ponTipo(Tipo.especificacion(alg.obtNombreEspecificacion()));
			if ( unidadActual != null )
			{
				unidadActual.ponNombreCompuesto("a" + id.obtId(), compuesto);
			}

			return true;
		}

		return false;
	}
	//////////////////////////////////////////////////////////////////////
	/**
	 *	Dice si ya hay un algoritmo del nombre dado en el fuente.
	 */
	protected boolean _yaHayAlgoritmo(String nombreSimple)
	{
		return null != algs.get(nombreSimple);
	}
	//////////////////////////////////////////////////////////////////////
	/**
	 *	Dice si ya hay una clase del nombre dado en el fuente.
	 */
	protected boolean _yaHayClase(String nombreSimple)
	{
		return null != clases.get(nombreSimple);
	}
	//////////////////////////////////////////////////////////////////////
	/**
	 *	Dice si ya hay una especificacion del nombre dado en el fuente.
	 */
	protected boolean _yaHayEspecificacion(String nombreSimple)
	{
		return null != esps.get(nombreSimple);
	}
	//////////////////////////////////////////////////////////////////////
	/**
	 *	Dice si ya hay una clase del nombre dado en el fuente.
	 */
	protected boolean _yaHayInterface(String nombreSimple)
	{
		return null != interfaces.get(nombreSimple);
	}
	///////////////////////////////////////////////////////////////
	/**
	 * Obtiene los algoritmos actualmente compilados.
	 */
	public java.util.Enumeration obtAlgoritmos()
	{
		return algs.elements();
	}
	///////////////////////////////////////////////////////////////
	/**
	 * Obtiene el manejador de unidades.
	 */
	public ManejadorUnidades obtManejadorUnidades()
	{
		return mu;
	}
	//////////////////////////////////////////////////////////////////////
	/**
	 *
	 */
	public void ponNombreFuente(String nombre)
	{
		nombreFuente = nombre;
	}
	
	
	///////////////////////////////////////////////////////////////
	/**
	 * Establece el paquete que debe encontrarse en la compilación.
	 * Este servicio es particularmente necesario cuando se desea
	 * compilar una unidad directamente (<code>visitar(NUnidad)</code>)
	 * sabiéndose de antemano a qué paquete debe pertenecer.
	 *
	 * @param 	Nombre del paquete (en estilo "::") a asociar a esta unidad.
	 *          Un valor de null significa NO establecer ningún paquete así
	 *          que la compilación aceptará cualquier paquete indicado.
	 *          Valor no null indica el nombre del paquete a esperar;
	 *          vacío ("") significa el paquete anónimo.
	 *
	 */
	public void setExpectedPackageName(String pkgname)
	{
		expectedPackageName = pkgname;
	}
	
	///////////////////////////////////////////////////////////////
	/**
	 * Reinicia el estado del chequeador para comenzar una nueva
	 * compilacion completa.
	 */
	public void reiniciar()
	{
		// se "refresca" la referencia al manejador de unidades:
		mu = ManejadorUnidades.obtManejadorUnidades();
		
		algs.clear();
		esps.clear();
		clases.clear();
		interfaces.clear();
		labels.setSize(0);
		utiliza.clear();
		mu.reiniciar();
		
		// reiniciar la tabla de símbolos:
		tabSimb.irAMarca(marca_inicial);
		// Antes de tener marca_inicial se hacía simplemente: tabSimb.reiniciar(). 

		claseActual = null;
		interfaceActual = null;
		enInvocacion = false;
		enAfirmacion = false;
		tipoSegun = null;
		paqueteActual = null;

	}

	////////////////////////////////////////////////////////////////
	/**
	 * Dice si un interface es igual o es extension de otra.
	 */
	protected boolean _aKindOf(Nodo n, TipoInterface ti1, TipoInterface ti2)
	throws ChequeadorException
	{
		if ( ti1.igual(ti2) )
		{
			return true;
		}

		Set super_interfs = _obtSuperInterfaces(n, ti1);

		return super_interfs.contains(ti2.obtNombreCompletoString());
		
	}

	////////////////////////////////////////////////////////////////////////
	/**
	 * Auxiliar para chequear concordancia de parametros.
	 *
	 * @param u           Ubicacion para posible error.
	 * @param pdec        Declaraciones en algoritmo/metodo
	 * @param qdec        Declaraciones en especificacion/operacion
	 * @param operacion   true si es operacion; si no, especificacion
	 * @param entrada     true si es entrada; si no, salida
	 */
	protected void _chequearConcordanciaParametros(
		IUbicable u,
		NDeclaracion[] pdec,
		NDeclaracion[] qdec,
		NInterface interf,
		boolean  entrada
	)
	throws VisitanteException
	{
		String que = (interf != null ? "operación" : "especificación");
		String ent_sal = (entrada ? "entrada" : "salida");
		String card = ent_sal + (qdec.length == 1 ? "" : "s");
		
		if ( pdec.length != qdec.length )
		{
			throw new ChequeadorException(
				u,
				"La " +que+ " indica " +qdec.length+ " " +card);
		}

		//////////////////////////////////////////////////////////////////////
		// Procesar y enlazar parámetros:
		for ( int i = 0; i < pdec.length; i++ )
		{
			NDeclaracion d = pdec[i];
			d.aceptar(this);

			if ( entrada )
			{
				// Indique que hay asignacion: Vea visitar(NId):
				tabSimb.ponAsignado(d.obtId().obtId(), true);
			}
			else // salida
			{
				if ( d.esConstante() )
				{
					throw new ChequeadorException(
						d.obtId(),
						"Una salida no puede ser constante"
					);
				}
			}
			
			// revise que no haya inicializacion (la sintaxis lo admite)
			if ( d.tieneInicializacion() )
			{
				throw new ChequeadorException(
					d.obtId(),
					"Inicialización en parametro " +d.obtId()+ " no es válida"
				);
			}

			// revise que el tipo sea igual al especificado
			if ( !d.obtTipo().igual(qdec[i].obtTipo()) )
			{
				throw new ChequeadorException(d.obtId(),
					ent_sal+ " '" +d.obtId()+ "' no es del tipo especificado " +
					"'" +qdec[i].obtTipo()+ "'" +
					(interf == null ? "" : " en la interface " +interf.obtNombreCompletoCadena())
				);
			}

			// revise que el nombre sea igual al especificado
			if ( !d.obtId().obtId().equals(qdec[i].obtId().obtId()) )
			{
				throw new ChequeadorException(d.obtId(),
					ent_sal+ " '" +d.obtId()+ "' debe tener el mismo nombre especificado " +
					"'" +qdec[i].obtId()+ "'" +
					(interf == null ? "" : " en la interface " +interf.obtNombreCompletoCadena())
				);
			}

			if ( entrada )
			{
				// revise el caracter de constante segun lo especificado
				if ( d.esConstante() ^ qdec[i].esConstante() )
				{
					throw new ChequeadorException(pdec[i].obtId(),
						ent_sal+ " '" +d.obtId()+ "' " +
						(qdec[i].esConstante() ? "" : "no ") +
						"debe ser constante según indica la " +que+
						(interf == null ? "" : " en la interface " +interf.obtNombreCompletoCadena())
					);
				}
			}
		}

	}

	////////////////////////////////////////////////////////////////
	/**
	 * Auxiliar para visitar una expresion binaria booleana.
	 */
	protected void _visitarBinBooleano(NExprBin n)
	throws VisitanteException
	{
		NExpresion e = n.obtExpresionIzq();
		NExpresion f = n.obtExpresionDer();

		e.aceptar(this);
		f.aceptar(this);

		Tipo e_tipo = e.obtTipo();
		Tipo f_tipo = f.obtTipo();

		if ( !e_tipo.esBooleano()
		||   !f_tipo.esBooleano() )
		{
			_operadorBinNoDefinido(
				n,
				n.obtOperador(), e_tipo, f_tipo
			);
		}

		n.ponTipo(e.obtTipo());
	}
	
	////////////////////////////////////////////////////////////////
	/**
	 * Auxiliar para visitar una expresion binaria entera.
	 */
	protected void _visitarBinEntero(NExprBin n)
	throws VisitanteException
	{
		NExpresion e = n.obtExpresionIzq();
		NExpresion f = n.obtExpresionDer();

		e.aceptar(this);
		f.aceptar(this);

		Tipo e_tipo = e.obtTipo();
		Tipo f_tipo = f.obtTipo();

		if ( !e_tipo.esEntero()
		||   !f_tipo.esEntero() )
		{
			_operadorBinNoDefinido(
				n,
				n.obtOperador(), e_tipo, f_tipo
			);
		}

		n.ponTipo(e.obtTipo());
	}
	////////////////////////////////////////////////////////////////
	/**
	 * Auxiliar para visitar una expresion binaria entera/booleana.
	 */
	protected void _visitarBinEnteroBooleano(NExprBin n)
	throws VisitanteException
	{
		NExpresion e = n.obtExpresionIzq();
		NExpresion f = n.obtExpresionDer();

		e.aceptar(this);
		f.aceptar(this);

		Tipo e_tipo = e.obtTipo();
		Tipo f_tipo = f.obtTipo();

		if ( e_tipo.esEntero() && f_tipo.esEntero()
		||   e_tipo.esBooleano() && f_tipo.esBooleano() )
		{
			n.ponTipo(e.obtTipo());
		}
		else
		{
			_operadorBinNoDefinido(
				n,
				n.obtOperador(), e_tipo, f_tipo
			);
		}
	}
	////////////////////////////////////////////////////////////////
	/**
	 * Auxiliar para visitar una expresion binaria numerica.
	 */
	protected void _visitarBinNumerico(NExprBin n)
	throws VisitanteException
	{
		NExpresion e = n.obtExpresionIzq();
		NExpresion f = n.obtExpresionDer();

		e.aceptar(this);
		f.aceptar(this);

		Tipo e_tipo = e.obtTipo();
		Tipo f_tipo = f.obtTipo();

		if ( !e_tipo.esNumerico()
		||   !f_tipo.esNumerico() )
		{
			_operadorBinNoDefinido(
				n,
				n.obtOperador(), e_tipo, f_tipo
			);
		}

		if ( e_tipo.esReal() || f_tipo.esReal() )
			n.ponTipo(Tipo.real);
		else
			n.ponTipo(Tipo.entero);
	}
	////////////////////////////////////////////////////////////////
	/**
	 * Visita una expresion binaria en donde los operandos
	 * se estan comparando (excepto por igualdad).
	 */
	protected void _visitarComparacion(NExprBin n)
	throws VisitanteException
	{
		NExpresion e = n.obtExpresionIzq();
		NExpresion f = n.obtExpresionDer();

		e.aceptar(this);
		f.aceptar(this);

		Tipo e_tipo = e.obtTipo();
		Tipo f_tipo = f.obtTipo();

		if ( !e_tipo.esComparableCon(f_tipo) )
		{
			_operadorBinNoDefinido(
				n,
				n.obtOperador(), e_tipo, f_tipo
			);
		}

		n.ponTipo(Tipo.booleano);
	}
	

	////////////////////////////////////////////////////////////////
	/**
	 * Obtiene los nombres de las superinterfaces de una interface.
	 *
	 * @param ti El tipo interface a la que se le va a encontrar las superinterfaces.
	 *
	 * @return      Los nombres de las superinterfaces.
	 *              Si la interface corresponde a la interface raiz de Loro,
	 *              se retorna null.
	 */
	protected Set _obtSuperInterfaces(Nodo n, TipoInterface ti)
	throws ChequeadorException
	{
		NInterface interf = mu.obtInterface(ti.obtNombreCompletoString());
		
		try
		{
			return mu.obtSuperInterfaces(interf);
		}
		catch ( ClaseNoEncontradaException ex )
		{
			String nombre_super = ex.obtNombre();
			throw new ChequeadorException(
				n,
				"No se encuentra la super interface '" +nombre_super+ "'"
			);
		}
	}

}