package loro.ejecucion;

import loro.util.ManejadorUnidades;
import loro.util.Util;
import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;
import loro.arbol.*;
import loro.ijava.*;
import loro.java.*;
import loro.tabsimb.*;
import loro.tipo.*;
import loro.*;
import loro.compilacion.ChequeadorException;
import loro.compilacion.ClaseNoEncontradaException;
import loro.util.UtilValor;

import java.io.Reader;
import java.io.Writer;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.*;


/////////////////////////////////////////////////////////////////////
/**
 * Código de base para el visitante de ejecución.
 *
 * @author Carlos Rueda
 * @version 2002-05-18
 */
abstract class LoroEjecutorBase implements LAmbiente, IVisitante
{
	/** Valor de retorno en cada visita a nodo expresión. */
	protected Object retorno;

	/** En invocacion para ejecucion de NId? */
	protected boolean enInvocacion;

	/** Objeto al que se le acaba de invocar un método. */
	protected Objeto objInvocado;
		
	/** Argumentos para ejecucion de una algoritmo. */
	protected Object[] argsParaAlgoritmo;

	/** Contexto de una afirmacion. */
	protected int contextoAfirmacion;

	/** Afirmacion en contexto de accion. */
	static final int A_ACCION = 0;

	/** Afirmacion en contexto de precondicion. */
	static final int A_PRECONDICION = 1;

	/** Afirmacion en contexto de poscondicion. */
	static final int A_POSCONDICION = 2;

	/**
	 * En una expresión para afirmación?
	 */
	protected boolean enAfirmacion;

	/**
	 * visitar(NCuantificado) pone este indicador en cierto debido
	 * a que las expresiones de cuantificacion no se procesan,
	 * y asi se le avisa a visitar(NAfirmacion) que tome la afirmacion
	 * por satisfecha.
	 */
	protected boolean afirmacionCiertaPorCuantificacion;

	/**
	 * Tabla de simbolos de base, es decir, la que se utiliza cuando
	 * la pila de ejecucion esta vacia.
	 */
	protected TablaSimbolos tabSimbBase;

	/**
	 * Unidad de base, es decir, la que se utiliza cuando
	 * la pila de ejecucion esta vacia.
	 */
	protected NUnidad unidadBase;

	/**
	 * La pila de ejecucion.
	 */
	protected PilaEjecucion pilaEjec;

	/**
	 * La tabla de simbolos actual.
	 * Siempre que haya pila de ejecucion NO vacia, este atributo es igual
	 * al del tope de la pila. En caso contrario es igual a tabSimbBase.
	 */
	protected TablaSimbolos tabSimb;

	/**
	 * Unidad actual.
	 * Siempre que haya pila de ejecucion NO vacia, este atributo es igual
	 * al del tope de la pila. En caso contrario es igual a unidadBase.
	 */
	protected NUnidad unidadActual;

	/**
	 * Manejador de unidades para almacenamiento.
	 */
	protected ManejadorUnidades mu;



	/**
	 * Manejador para entrada y salida de datos.
	 */
	protected ManejadorEntradaSalida mes;

	/**
	 * Lista de receptores de eventos sobre este ambiente.
	 */
	protected List /* LAmbienteListener*/ receptores;

	/**
	 * Algoritmo principal en ejecucion.
	 */
	protected NAlgoritmo algoritmoPrincipal;


	/**
	 * El cargador de clases de Java.
	 */
	protected LoroClassLoader loroClassLoader;

	////////////////////////////////////////////////////////////////
	/**
	 * Crea un ejecutor.
	 * La tabla de simbolos de base se pone en null.
	 */
	protected LoroEjecutorBase()
	{
		this(null, null);
	}
	////////////////////////////////////////////////////////////////
	/**
	 * Crea un ejecutor basico.
	 */
	protected LoroEjecutorBase(TablaSimbolos tabSimbBase, NUnidad unidadBase)
	{
		retorno = null;
		enInvocacion = false;
		afirmacionCiertaPorCuantificacion = false;
		argsParaAlgoritmo = null;
		pilaEjec = new PilaEjecucion();
		mu = ManejadorUnidades.obtManejadorUnidades();

		mes = new ManejadorEntradaSalida();

		algoritmoPrincipal = null;

		this.loroClassLoader = new LoroClassLoader();
		
		reset(tabSimbBase, unidadBase);
	}

	///////////////////////////////////////////////////////////////////
	/**
	 * Retorna una nueva instancia de una clase.
	 * Los atributos de la nueva instancia toman sus valores por defecto o
	 * bien los de las expresiones de inicialización que se hayan indicado
	 * en la definición de tal clase.
	 *
	 * @param clase La clase a instanciar.
	 * @param Ubicación para posible error.
	 *
	 * 2000-08-09
	 */
	protected Objeto _crearObjetoInicializado(NClase clase, IUbicable u)
	throws VisitanteException
	{
		NDeclDesc[] pent = clase.obtParametrosEntrada();
		_pushClase(clase);

		try
		{
			// Enlace atributos con valores iniciales dados
			// o valores por defecto si no hay iniciales:
			for ( int i = 0; i < pent.length; i++ )
			{
				NDeclDesc d = pent[i];
				Tipo d_tipo = d.obtTipo();
				Object val;
				if ( d.tieneInicializacion() )
				{
					NExpresion d_e = d.obtExpresion();
					d_e.aceptar(this);
					val = retorno;
				}
				else
				{
					val = d_tipo.obtValorDefecto();
				}
				TId d_id = d.obtId();
				EntradaTabla et = new EntradaTabla(d_id.obtId(), d_tipo);
				et.ponValor(val);
				_insertarEnTablaSimbolos(et, d_id);
			}
		}
		catch (loro.compilacion.ChequeadorException ex)
		{
			throw new RuntimeException("Imposible: " +ex);
		}

		// Cree y arme objeto con los valores asociados en la
		// tabla de simbolos:
		Objeto obj = new Objeto(clase);
		for ( int i = 0; i < pent.length; i++ )
		{
			TId id = pent[i].obtId();
			Object val = tabSimb.obtValor(id.obtId());
			_ponValorAObjeto(obj, id.obtId(), val, u);
		}

		_pop();

		return obj;
	}

	///////////////////////////////////////////////////////////////////
	/**
	 * Auxiliar para visita una expresión y retornar el resultado
	 * que haya quedado.
	 */
	protected Object _ejecutarExpresion(NExpresion e)
	throws VisitanteException
	{
		e.aceptar(this);
		return retorno;
	}

	///////////////////////////////////////////////////////////////////
	/**
	 * Visita la pre o la pos-condicion de una especificacion.
	 */
	protected void _visitarCondicionEnEspecificacion(NEspecificacion n, int contexto)
	throws VisitanteException
	{
		NAfirmacion afirm;
		if ( contexto == A_PRECONDICION )
			afirm = n.obtPrecondicion();
		else
			afirm = n.obtPoscondicion();

		if ( afirm != null )
		{
			_pushEspecificacion(n);
			pilaEjec.actualizarTope(afirm);
			contextoAfirmacion = contexto;
			afirm.aceptar(this);
			contextoAfirmacion = A_ACCION;
			_pop();
		}
	}


	///////////////////////////////////////////////////////////////////
	/**
	 * Conversion de valor de retorno de una invocación a método en Java.
	 */
	protected static Object _convertirRetornoDeJava(Object res)
	{
		if ( res instanceof LAlgoritmoImp )
		{
			return ((LAlgoritmoImp) res).obtAlgoritmo();
		}

		return res;
	}
	
	//////////////////////////////////////////////////////////////
	/**
	 * Convierte un valor val que tiene el tipo tipoValor (en compilacion)
	 * en el tipo tipoEsperado y lo retorna.
	 *
	 * @param u - Ubicacion para posible error.
	 */
	protected Object _convertirValor(
			IUbicable u,
			Tipo tipoEsperado,
			Tipo tipoValor,
			Object val
	)
	throws EjecucionVisitanteException
	{
		if ( val == null )
		{
			return null;
		}
		else if ( tipoEsperado.esCadena() )
		{
			return UtilValor.comoCadena(val);
		}
		else if ( tipoValor.esReal() )
		{
			if ( tipoEsperado.esEntero() )
			{
				int v = (int) Math.round(((Number)val).doubleValue());
				return new Integer(v);
			}
		}
		else if ( tipoValor.esEntero() )
		{
			if ( tipoEsperado.esReal() )
			{
				double v = ((Number)val).doubleValue();
				return new Double(v);
			}
			if ( tipoEsperado.esCaracter() )
			{
				int v = ((Number)val).intValue();
				return new Character((char) v);
			}
			if ( tipoEsperado.esBooleano() )
			{
				int v = ((Number)val).intValue();
				return new Boolean(v != 0);
			}
		}
		else if ( tipoValor.esBooleano() )
		{
			if ( tipoEsperado.esEntero() )
			{
				boolean v = ((Boolean)val).booleanValue();
				return new Integer(v ? 1 : 0);
			}
		}
		else if ( tipoValor.esCaracter() )
		{
			if ( tipoEsperado.esEntero() )
			{
				char v = ((Character)val).charValue();
				return new Integer((int)v);
			}
		}
		else if ( tipoValor.esAlgoritmo() )
		{
			if ( tipoEsperado.esAlgoritmo() )
			{
				if ( val instanceof NAlgoritmo )
				{
					NAlgoritmo alg = (NAlgoritmo) val;

					TipoEspecificacion te = (TipoEspecificacion) tipoEsperado;
					String[] nomEspec = te.obtNombreEspecificacion();
					if ( nomEspec == null )
					{
						// tipo esperado es algoritmo generico; Ok:
						//2001-08-28
						return val;
					}

					// si no, confrontar especificacion:

					if ( !alg.esParaEspecificacion(nomEspec) )
					{
						throw _crearEjecucionException(u,
							alg+ " no puede convertirse a " +tipoEsperado
						);
					}
				}

				// Aqui viene la consideracion de LAlgoritmo:
				else if ( val instanceof LAlgoritmo )
				{
					// 2001-09-17
					// OJO: La idea seria hacer la misma revision que se
					// hace con NAlgoritmo: que haya compatibilidad en cuanto
					// a la especificacion correspondiente. PERO actualmente
					// LAlgoritmo no incluye esta funcionalidad.
					// La reestructuracion necesaria en este sentido sigue
					// pendiente.

					// Por ahora, simplemente acepte las cosas:

					return val;
				}
				else if ( val == null )
				{
					// Ok.
				}
				else
				{
					throw new RuntimeException(
						"Uy! val es " +(val==null? "null" : val.getClass().toString())
					);
				}

			}
		}
		else if ( tipoValor.igual(tipoEsperado) )       // OJO : REVISAR ESTO?
		{
			return val;
		}

		return val;
	}
	//////////////////////////////////////////////////////////////////////////
	/**
	 * Crea una EjecucionException con la infomacion dada y la
	 * pila de ejecucion actual.
	 */
	protected EjecucionVisitanteException _crearEjecucionException(IUbicable u, String s)
	{
		return new EjecucionVisitanteException(u, pilaEjec, s);
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
			nombreCompuesto = unidadActual.obtNombreCompuesto("a" +id);
		}

		if ( nombreCompuesto == null )
		{
			// Intente por el paquete sin nombre:
			nombreCompuesto = id.obtId();
		}

		return mu.obtAlgoritmo(nombreCompuesto);
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
			nombreCompuesto = unidadActual.obtNombreCompuesto("c" +id);
		}

		if ( nombreCompuesto == null )
		{
			// Intente por el paquete sin nombre:
			nombreCompuesto = id.obtId();
		}

		return mu.obtClase(nombreCompuesto);
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
			nombreCompuesto = unidadActual.obtNombreCompuesto("e" +id);
		}

		if ( nombreCompuesto == null )
		{
			// Intente por el paquete sin nombre:
			nombreCompuesto = id.obtId();
		}

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
	
	///////////////////////////////////////////////////////////////
	/**
	 * Obtiene el valor de una casilla de un arreglo.
	 */
	protected Object _obtValorDeArreglo(Object obj, int index, IUbicable u)
	throws VisitanteException
	{
		if ( obj == null )
		{
			throw _crearEjecucionException(u,
				"Arreglo o cadena es nulo en subindización"
			);
		}

		if ( obj instanceof String )
		{
			try
			{
				String s = (String) obj;
				return new Character(s.charAt(index));
			}
			catch(StringIndexOutOfBoundsException ex)
			{
				throw _crearEjecucionException(u,
					"Indice en cadena fuera de rango: " +index
				);
			}
		}
		
		Object[] ev;
		int base;
		if ( obj instanceof ArregloBaseNoCero )
		{
			ArregloBaseNoCero abnc = (ArregloBaseNoCero) obj;
			ev = abnc.array;
			base = abnc.base;
		}
		else
		{
			ev = (Object[]) obj;
			base = 0;
		}

		try
		{
			return ev[index - base];
		}
		catch(ArrayIndexOutOfBoundsException ex)
		{
			if ( ev.length == 0) 
			{
				// arreglo vacio:
				throw _crearEjecucionException(u,
					"Indice en arreglo fuera de rango: " +index+ "\n"+
					"Este es un arreglo vacío."
				);
			} 
			else 
			{
				throw _crearEjecucionException(u,
					"Indice en arreglo fuera de rango: " +index+ "\n"+
					"Indices válidos son: " +base+ " .. " +(base + ev.length - 1)
				);
			}
		}
	}

	///////////////////////////////////////////////////////////////
	/**
	 * Pone un valor a una casilla de un arreglo.
	 */
	protected void _ponValorAArreglo(Object obj, int index, Object val, IUbicable u)
	throws VisitanteException
	{
		if ( obj == null )
		{
			throw _crearEjecucionException(u,
				"Arreglo o cadena es nulo en subindización"
			);
		}

		Object[] ev;
		int base;
		if ( obj instanceof ArregloBaseNoCero )
		{
			ArregloBaseNoCero abnc = (ArregloBaseNoCero) obj;
			ev = abnc.array;
			base = abnc.base;
		}
		else
		{
			ev = (Object[]) obj;
			base = 0;
		}

		try
		{
			ev[index - base] = val;
		}
		catch(ArrayIndexOutOfBoundsException ex)
		{
			if ( ev.length == 0) 
			{
				// arreglo vacio:
				throw _crearEjecucionException(u,
					"Indice en arreglo fuera de rango: " +index+ "\n"+
					"Este es un arreglo vacío."
				);
			} 
			else 
			{
				throw _crearEjecucionException(u,
					"Indice en arreglo fuera de rango: " +index+ "\n"+
					"Indices válidos son: " +base+ " .. " +(base + ev.length - 1)
				);
			}
		}
	}

	//////////////////////////////////////////////////////////////////////
	/**
	 * Hace pop a la pila de ejecucion.
	 * Se actualiza la tabla de simbolos y la unidad actual en ejecucion
	 * con la del nuevo marco de activacion del tope, o con null's si la
	 * pila queda vacia.
	 */
	protected void _pop()
	{
		pilaEjec.pop();
		if ( pilaEjec.empty() )
		{
			// pila vacia:
			// tabSimb toma de nuevo tabSimbBase:
			// unidadActual toma de nuevo unidadBase:
			tabSimb = tabSimbBase;
			unidadActual = unidadBase;
		}
		else
		{
			tabSimb = pilaEjec.obtTablaSimbolos();
			unidadActual = pilaEjec.obtUnidad();
		}
	}
	//////////////////////////////////////////////////////////////////////
	/**
	 * Mete en la pila un nuevo marco de activacion con
	 * el algoritmo dado.
	 * Se crea alli una nueva tabla de simbolos.
	 */
	protected void _pushAlgoritmo(NAlgoritmo uni)
	{
		pilaEjec.pushAlgoritmo(uni);
		tabSimb = pilaEjec.obtTablaSimbolos();
		unidadActual = uni;
	}
	//////////////////////////////////////////////////////////////////////
	/**
	 * Mete en la pila un nuevo marco de activacion con
	 * la clase dada.
	 * Se crea alli una nueva tabla de simbolos.
	 */
	protected void _pushClase(NClase uni)
	{
		pilaEjec.pushClase(uni);
		tabSimb = pilaEjec.obtTablaSimbolos();
		unidadActual = uni;
	}
	//////////////////////////////////////////////////////////////////////
	/**
	 * Mete en la pila un nuevo marco de activacion con
	 * la especificacion dada.
	 */
	protected void _pushEspecificacion(NEspecificacion uni)
	{
		pilaEjec.pushEspecificacion(uni);
		tabSimb = pilaEjec.obtTablaSimbolos();
		unidadActual = uni;
	}
	///////////////////////////////////////////////////////////////////
	/**
	 * Trata de resolver id en el ambiente.
	 */
	protected boolean _resolverIdEnAmbiente(NId n)
	throws VisitanteException
	{
		TId id = n.obtId();
		if ( Util.esVarSemantica(id) )
		{
			return false;
		}

		NUnidad uni;
		if ( null != (uni = _obtAlgoritmoParaId(id))
		||   null != (uni = _obtClaseParaId(id))
		||   null != (uni = _obtEspecificacionParaId(id)) )
		{
			retorno = uni;
			return true;
		}

		// intente por posible nombre compuesto asociado
		// al nombre simple dentro de la unidad actual:
		String compuesto;

		if ( unidadActual != null ) // 2001-10-01
		{
			compuesto = unidadActual.obtNombreCompuesto("a" + id);
			if ( compuesto != null )
			{
				uni = mu.obtAlgoritmo(compuesto);
				if ( uni != null )
				{
					retorno = uni;
					return true;
				}
				return false;
			}

			compuesto = unidadActual.obtNombreCompuesto("c" + id);
			if ( compuesto != null )
			{
				uni = mu.obtClase(compuesto);
				if ( uni != null )
				{
					retorno = uni;
					return true;
				}
				return false;
			}
			compuesto = unidadActual.obtNombreCompuesto("e" + id);
			if ( compuesto != null )
			{
				uni = mu.obtEspecificacion(compuesto);
				if ( uni != null )
				{
					retorno = uni;
					return true;
				}
				return false;
			}
		}

		return false;
	}
	///////////////////////////////////////////////////////////////////
	/**
	 * Trata de resolver id en la tabla de simbolos.
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
				throw _crearEjecucionException(n,
					"Variable sin asignacion de valor: '"+id+ "'"
				);
			}
			retorno = et.obtValor();
			return true;
		}

		return false;
	}
	//////////////////////////////////////////////////////////////
	/**
	 * Adiciona un receptor de eventos sobre este ambiente.
	 *
	 * @param al	El receptor de eventos.
	 */
	public void addAmbienteListener(LAmbienteListener al)
	throws LException
	{
		if ( receptores == null )
		{
			receptores = new ArrayList();
		}

		receptores.add(al);
	}
	////////////////////////////////////////////////////////////////////
	/**
	 * Despacha la terminación de una ejecución.
	 */
	public void despacharTerminacion()
	{
		if ( receptores == null )
		{
			return;
		}

		for(Iterator it = receptores.iterator(); it.hasNext(); )
		{
			LAmbienteListener al = (LAmbienteListener) it.next();
			al.terminandoEjecucion();
		}
	}
	//////////////////////////////////////////////////////
	/**
	 * Retorna false: este ejecutor basico no esta preparado
	 * para ser terminado externamente.
	 */
	public boolean esTerminableExternamente()
	{
		return false;
	}
	//////////////////////////////////////////////////////////////
	/**
	 * Obtiene una clase Loro por su nombre completo.
	 *
	 * @param nombre	Nombre completo de la clase.
	 * @return			La clase correspondiente.
	 * @throws			LException Si no fue posible obtener
	 *					la clase.
	 */
	public LClase obtClase(String nombre)
	throws LException
	{
		NClase clase = mu.obtClase(nombre);
		if ( clase != null )
		{
			return new LClaseImp(clase);
		}

		throw new LException("Error al obtener clase '"	+nombre+ "'");
	}
	/////////////////////////////////////////////////////////////////
	/**
	 * Obtiene el Manejador de Entrada-Salida.
	 */
	public LManejadorES obtManejadorEntradaSalida()
	throws LException
	{
		return mes;
	}
	/**
	 * Obtiene el valor de retorno actual.
	 */
	public Object obtRetorno()
	{
		return retorno;
	}
	/**
	 * Pone los argumentos para un subsiguiente visita a un
	 * algoritmo.
	 */
	public void ponArgumentosParaAlgoritmo(Object[] args)
	{
		argsParaAlgoritmo= args;
	}
	////////////////////////////////////////////////////////////////
	/**
	 * Pone el cargador de clases Java.
	 */
	public void ponClassLoader(LoroClassLoader loroClassLoader)
	{
		this.loroClassLoader = loroClassLoader;
	}
	//////////////////////////////////////////////////////////////
	public void ponEntradaEstandar(Reader r)
	{
		mes.ponEntradaEstandar(r);
	}
	//////////////////////////////////////////////////////////////
	public void ponSalidaEstandar(Writer w)
	{
		mes.ponSalidaEstandar(w);
	}
	//////////////////////////////////////////////////////////////
	/**
	 * Quita un receptor de eventos sobre este ambiente.
	 *
	 * @param al	El receptor de eventos.
	 */
	public void removeAmbienteListener(LAmbienteListener al)
	throws LException
	{
		if ( receptores == null )
		{
			return;
		}

		receptores.remove(al);
	}
	///////////////////////////////////////////////////////
	/**
	 * Prepara este ejecutor para nueva visita.
	 *
	 * @param tabSimbBase	Tabla de simbolos a tomar como de base.
	 * @param unidadBase	Unidad de base.
	 */
	public void reset(TablaSimbolos tabSimbBase, NUnidad unidadBase)
	{
		// tabla de simbolos: de base y actual:
		this.tabSimbBase = tabSimbBase;
		tabSimb = tabSimbBase;

		// unidad: de base y actual:
		this.unidadBase = unidadBase;
		unidadActual = unidadBase;

		pilaEjec.reset();
	}
	//////////////////////////////////////////////////////////////
	/**
	 * Termina la ejecución de un programa Loro.
	 * Se considera una terminacion interna, es decir, como
	 * si fuera una instruccion explicita dentro del programa Loro.
	 *
	 * @param codigo	Código de terminación.
	 */
	public void terminarEjecucion(int codigo)
	throws LException
	{
		throw new TerminacionInternaException(codigo);
	}
	//////////////////////////////////////////////////////////////
	/**
	 * @throws UnsupportedOperationException
	 */
	public synchronized void terminarExternamente()
	{
		throw new UnsupportedOperationException();
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
			nombreCompuesto = unidadActual.obtNombreCompuesto("i" +id);
		}

		if ( nombreCompuesto == null )
		{
			// Intente por el paquete sin nombre:
			nombreCompuesto = id.obtId();
		}

		return mu.obtInterface(nombreCompuesto);
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

	///////////////////////////////////////////////////////////////
	/**
	 */
	protected Object _obtMetodoDeObjeto(Objeto obj, String id, IUbicable u)
	throws VisitanteException
	{
		try
		{
			return obj.obtMetodo(id);
		}
		catch(LException ex)
		{
			throw _crearEjecucionException(u, ex.getMessage());
		}
	}

	///////////////////////////////////////////////////////////////
	/**
	 */
	protected Object _obtValorDeObjeto(Objeto obj, String id, IUbicable u)
	throws VisitanteException
	{
		try
		{
			return obj.obtValor(id);
		}
		catch(LException ex)
		{
			throw _crearEjecucionException(u, ex.getMessage());
		}
	}

	///////////////////////////////////////////////////////////////
	/**
	 */
	protected void _ponValorAObjeto(Objeto obj, String id, Object valor, IUbicable u)
	throws VisitanteException
	{
		try
		{
			obj.ponValor(id, valor);
		}
		catch(LException ex)
		{
			throw _crearEjecucionException(u, ex.getMessage());
		}
	}

	////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la lista (NDeclDesc) de declaraciones de los atributos de una clase.
	 */
	protected List _obtDeclaracionesAtributos(NClase clase)
	throws ClaseNoEncontradaException
	{
		List decl_atrs = new ArrayList();

		Stack ascendencia = new Stack();
		while ( clase != null )
		{
			ascendencia.push(clase);
			clase = mu.obtSuperClase(clase);
		}

		while ( !ascendencia.isEmpty() )
		{
			clase = (NClase) ascendencia.pop();
			NDeclDesc[] pent = clase.obtParametrosEntrada();
			for ( int i = 0; i < pent.length; i++ )
			{
				decl_atrs.add(pent[i]);
			}
		}

		return decl_atrs;
	}

	/////////////////////////////////////////////////////////////////////////
	/**
	 * Auxiliar para visitar una lista de acciones. 
	 * Básicamente hace: marca la tabla de símbolos, llama aceptar para cada
	 * nodo de la lista, y desmarca la tabla.
	 *
	 * @param acciones Las acciones a ejecutar.
	 */
	protected void _visitarAcciones(Nodo[] acciones)
	throws VisitanteException
	{
		tabSimb.marcar();
		try
		{
			for ( int i = 0; i < acciones.length; i++ )
			{
				acciones[i].aceptar(this);
			}
		}
		finally
		{
			tabSimb.desmarcar();
		}
	}

	/////////////////////////////////////////////////////////////////////////
	/**
	 * Auxiliar para visitar el cuerpo de acciones de una iteración.
	 * En esta clase simplemente se invoca _visitarAcciones(acciones).
	 *
	 * @param iter El nodo iteracion correspondiente. Ignorado en esta clase.
	 *             La reimplementacion en la subclase EjecutorTerminable sí utiliza
	 *             este parámetro.
	 * @param acciones Las acciones a ejecutar.
	 */
	protected void _visitarAccionesIteracion(Nodo iter, Nodo[] acciones)
	throws VisitanteException
	{
		_visitarAcciones(acciones);
	}
}