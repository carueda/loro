package loro.impl;

import loro.Loro.Str;
import loro.*;
import loro.compilacion.*;
import loro.derivacion.*;
import loro.arbol.*;
import loro.util.Util;
import loro.util.ManejadorUnidades;

import java.io.*;
import java.util.*;
import java.nio.charset.Charset;

/////////////////////////////////////////////////////////////////////
/**
 * Implementacion de ICompilador
 *
 * @author Carlos Rueda
 * @version $Id$
 */
public class CompiladorImpl implements ICompilador
{
	/** Arreglo vacio de unidades. */
	private static final IUnidad[] ARREGLO_VACIO_UNIDAD = new IUnidad[0];
	
	/** El derivador sintáctico. */
	IDerivador derivador;

	/** El chequeador semántico. */
	Chequeador chequeador;

	/////////////////////////////////////////////////////////////////////
	/**
	 * Crea un compilador.
	 */
	public CompiladorImpl()
	{
		derivador = ManejadorDerivacion.obtDerivador();
		chequeador = new Chequeador();
	}

	///////////////////////////////////////////////////////////////////////
	public IUnidad compileSpecificaction(
		String expected_pkgname, 
		String expected_unitname
	)
	throws CompilacionException
	{
		/////////////////////////////////////////////
		// Fase sintáctica (construcción del árbol de derivación):
		NFuente prg = derivador.derivarFuente();
		NUnidad[] unidades = (NUnidad[]) prg.obtUnidades();
		if ( unidades.length > 1 )
		{
			IUnidad u2 = unidades[1];
			Rango r = u2.obtRango();
			throw new CompilacionException(r, 
				Str.get("error.1_only_spec_expected", expected_unitname)
			);
		}
		
		if ( !(unidades[0] instanceof NEspecificacion) )
		{
			IUnidad u2 = unidades[0];
			Rango r = u2.obtRango();
			throw new CompilacionException(r,
				Str.get("error.1_only_spec_expected", expected_unitname)
			);
		}
		
		NUnidad unidad = unidades[0];
		
		String unitname = unidad.obtNombreSimpleCadena();
		if ( ! expected_unitname.equals(unitname) )
		{
			Rango r;
			String msg = "Se espera el nombre '" +expected_unitname+ "'";
			TId id = unidad.obtId();
			r = id.obtRango();
			throw new CompilacionException(r, 
				Str.get("error.1_only_spec_expected", expected_unitname)
			);
		}
		
		/////////////////////////////////////////////
		// Fase semántica:
		try
		{
			chequeador.reiniciar();
			chequeador.setExpectedPackageName(expected_pkgname);
			chequeador.chequear(prg);
			return unidad;
		}
		catch ( ChequeadorException se )
		{
			throw new CompilacionException(se.obtRango(), se.getMessage());
		}
	}

	///////////////////////////////////////////////////////////////////////
	public IUnidad compileAlgorithm(
		String expected_pkgname, 
		String expected_unitname,
		String expected_specname
	)
	throws CompilacionException
	{
		/////////////////////////////////////////////
		// Fase sintáctica (construcción del árbol de derivación):
		NFuente prg = derivador.derivarFuente();
		NUnidad[] unidades = (NUnidad[]) prg.obtUnidades();
		if ( unidades.length > 1 )
		{
			IUnidad u2 = unidades[1];
			Rango r = u2.obtRango();
			throw new CompilacionException(r, 
				Str.get("error.1_only_algorithm_expected", expected_unitname)
			);
		}
		if ( !(unidades[0] instanceof NAlgoritmo) )
		{
			IUnidad u2 = unidades[0];
			Rango r = u2.obtRango();
			throw new CompilacionException(r, 
				Str.get("error.1_only_algorithm_expected", expected_unitname)
			);
		}
		
		NUnidad unidad = unidades[0];
		
		String unitname = unidad.obtNombreSimpleCadena();
		if ( ! expected_unitname.equals(unitname) )
		{
			Rango r;
			String msg;
			TId id = unidad.obtId();
			if ( id != null ) {
				// algorithm name explicitly given
				r = id.obtRango();
				msg = Str.get("error.1_only_algorithm_expected", expected_unitname);
			}
			else {
				// name to be taken from spec
				NAlgoritmo nalg = (NAlgoritmo) unidad;
				TNombre espec = nalg.obtTNombreEspecificacion();
				TId[] ids = espec.obtIds();
				r = ids[ids.length -1].obtRango();
				msg = Str.get("error.2_only_algorithm_expected_would_take", expected_unitname, unitname);
			}
			
			throw new CompilacionException(r, msg);
		}
		
		if ( expected_specname != null )
		{
			NAlgoritmo nalg = (NAlgoritmo) unidad;
			TNombre espec = nalg.obtTNombreEspecificacion();
			String specname = espec.obtCadena();
			boolean ok = expected_specname.equals(specname);
			if ( ! ok )
			{
				// No hay coincidencia exacta.
				// Posibilidad que se trate de una especificación en el mismo
				// paquete o en el paquete automático:
				if ( specname.indexOf(":") < 0 )    // specname debe ser simple
				{
					ok = expected_specname.equals(expected_pkgname+ "::" +specname);
					if ( ! ok )
					{
						ManejadorUnidades mu = chequeador.obtManejadorUnidades();
						String auto_pkg = mu.obtNombrePaqueteAutomatico();
						ok = expected_specname.equals(auto_pkg+ "::" +specname);
					}
				}
			}
			if ( ! ok )
			{
				throw new CompilacionException(
					espec.obtRango(),
					Str.get("error.1_algorithm_must_implement", expected_specname)
				);
			}
		}

		/////////////////////////////////////////////
		// Fase semántica:
		try
		{
			chequeador.reiniciar();
			chequeador.setExpectedPackageName(expected_pkgname);
			chequeador.chequear(prg);
			return unidad;
		}
		catch ( ChequeadorException se )
		{
			throw new CompilacionException(se.obtRango(), se.getMessage());
		}
	}

	///////////////////////////////////////////////////////////////////////
	public IUnidad compileClass(
		String expected_pkgname, 
		String expected_unitname
	)
	throws CompilacionException
	{
		/////////////////////////////////////////////
		// Fase sintáctica (construcción del árbol de derivación):
		NFuente prg = derivador.derivarFuente();
		NUnidad[] unidades = (NUnidad[]) prg.obtUnidades();
		if ( unidades.length > 1 )
		{
			IUnidad u2 = unidades[1];
			Rango r = u2.obtRango();
			throw new CompilacionException(r,
				Str.get("error.1_only_class_expected", expected_unitname)
			);
		}
		
		if ( !(unidades[0] instanceof NClase) 
		&&   !(unidades[0] instanceof NInterface) )   // PROVISIONAL aqui NInterface
		                                              // Luego habria compileInterface
		{
			IUnidad u2 = unidades[0];
			Rango r = u2.obtRango();
			throw new CompilacionException(r, 
				Str.get("error.1_only_class_expected", expected_unitname)
			);
		}
		
		NUnidad unidad = unidades[0];
		
		String unitname = unidad.obtNombreSimpleCadena();
		if ( ! expected_unitname.equals(unitname) )
		{
			Rango r;
			TId id = unidad.obtId();
			r = id.obtRango();
			throw new CompilacionException(r, 
				Str.get("error.1_only_class_expected", expected_unitname)
			);
		}
		
		/////////////////////////////////////////////
		// Fase semántica:
		try
		{
			chequeador.reiniciar();
			chequeador.setExpectedPackageName(expected_pkgname);
			chequeador.chequear(prg);
			return unidad;
		}
		catch ( ChequeadorException se )
		{
			throw new CompilacionException(se.obtRango(), se.getMessage());
		}
	}

	///////////////////////////////////////////////////////////////////////
	public IUnidad[] derivarFuente()
	throws CompilacionException
	{
		NFuente prg = derivador.derivarFuente();
		return prg.obtUnidades();
	}

	///////////////////////////////////////////////////////////////////////
	public void derivarId()
	throws CompilacionException
	{
		TId tid = derivador.derivarId();
		if ( Util.esVarSemantica(tid) ) {
			throw new CompilacionException(tid.obtRango(), 
				Str.get("error.id_is_semantic_var")
			);
		}
	}

	///////////////////////////////////////////////////////////////////////
	public void derivarNombre()
	throws CompilacionException
	{
		TNombre tnombre = derivador.derivarNombre();
		TId[] tids = tnombre.obtIds();
		for ( int i = 0; i < tids.length; i++ )
		{
			if ( Util.esVarSemantica(tids[i]) ) {
				throw new CompilacionException(tnombre.obtRango(), 
					Str.get("error.name_contains_semantic_var")
				);
			}
		}
	}

	///////////////////////////////////////////////////////////////////////
	public IFuente compilarFuente()
	throws CompilacionException
	{
		/////////////////////////////////////////////
		// Fase sintáctica (construcción del árbol de derivación):
		NFuente prg = derivador.derivarFuente();

		/////////////////////////////////////////////
		// Fase semántica:
		try
		{
			chequeador.reiniciar();
			chequeador.chequear(prg);
			return prg;
		}
		catch ( ChequeadorException se )
		{
			throw new CompilacionException(se.obtRango(), se.getMessage());
		}
	}

	///////////////////////////////////////////////////////////////
	public IUnidad[] obtAlgoritmos()
	{
		List list = new ArrayList();
		for ( Enumeration e = chequeador.obtAlgoritmos(); e.hasMoreElements(); )
		{
			list.add(e.nextElement());
		}
		
		return (IUnidad[]) list.toArray(ARREGLO_VACIO_UNIDAD);
	}

	///////////////////////////////////////////////////////////////////////
	public String obtTextoFuente()
	{
		return derivador.obtTextoFuente();
	}

	///////////////////////////////////////////////////////////////
	public void ponGuardarCompilados(boolean g)
	{
		chequeador.obtManejadorUnidades().ponGuardarCompilados(g);
	}

	//////////////////////////////////////////////////////////////
	public void ponDirectorioDestino(String dir)
	{
		chequeador.obtManejadorUnidades().ponDirGuardarCompilado(dir);
	}

	///////////////////////////////////////////////////////////////////////
	public void ponNombreArchivo(String nombreArchivo)
	{
		chequeador.ponNombreFuente(nombreArchivo);
	}

	///////////////////////////////////////////////////////////////////////
	public ICompilador ponTextoFuente(Reader fuente)
	{
		derivador.ponTextoFuente(fuente);
		return this;
	}

	///////////////////////////////////////////////////////////////////////
	public ICompilador ponTextoFuente(String fuente)
	{
		derivador.ponTextoFuente(fuente);
		return this;
	}

	// to sort files according to name:
	private static int _orderCode(String s) {
		return s.endsWith(".e.loro") ? 10
		     : s.endsWith(".i.loro") ? 15
		     : s.endsWith(".a.loro") ? 20
		     : s.endsWith(".c.loro") ? 25
		     : s.endsWith(".o.loro") ? 30
			 : 50
		;
	}
	
	private static Comparator comparator = new Comparator() {
		public int compare(Object o1, Object o2) {
			String s1 = (String) o1;
			String s2 = (String) o2;
			int cmp = _orderCode(s1) - _orderCode(s2);
			if ( cmp == 0 )
				return s1.compareTo(s2);
			else
				return cmp;
		}
	};
	
	///////////////////////////////////////////////////////////////////////
	/**
	 * Ordena una lista de nombres de archivos (Strings) según las extensiones
	 * tal que queda: *.e.loro, *.a.loro, *.c.loro, resto.
	 */
	private static void sort(List archivos)
	{
		Collections.sort(archivos, comparator);
	}
	
	///////////////////////////////////////////////////////////////////////
	public int compilarListaArchivos(List archivos, List unidades, Writer errores)
	{
		// Primero ordenamos los nombres de los archivos fuentes:
		sort(archivos);
	
		// ahora, compilar:
		PrintWriter pwerr = new PrintWriter(errores, true);
		int compilados = 0;
		//pwerr.println("Compiling: {");
		for ( Iterator it = archivos.iterator(); it.hasNext(); )
		{
			String nombre = (String) it.next();
			//pwerr.println("  [" +nombre+ "]");
			try
			{
				Reader reader = _openReader(nombre);
				ponTextoFuente(reader);
				ponNombreArchivo(nombre);
				IFuente fuente = compilarFuente();
				IUnidad[] unids = fuente.obtUnidades();
				compilados++;
				if ( unidades != null )
				{
					unidades.addAll(Arrays.asList(unids));
				}
			}
			catch(FileNotFoundException ex)
			{
				pwerr.println(Str.get("error.1_file_not_found", nombre));
			}
			catch(CompilacionException ex)
			{
				Rango rango = ex.obtRango();
				
				String res = nombre + 
					"[" +rango.obtIniLin()+ "," +rango.obtIniCol()+ "]" +
					" " +ex.getMessage()
				;
				pwerr.println(res);
			}
		}
		//pwerr.println("}");
		
		return compilados;
	}

	///////////////////////////////////////////////////////////////////////
	public int anticompilarListaArchivos(List archivos, Writer errores)
	{
		// Primero ordenamos los nombres de los archivos fuentes:
		sort(archivos);
	
		// ahora, anti-compilar:
		PrintWriter pwerr = new PrintWriter(errores, true);
		int anticompilados = 0;
		for ( Iterator it = archivos.iterator(); it.hasNext(); )
		{
			String nombre = (String) it.next();
			//pwerr.println("[" +nombre+ "]");
			try
			{
				Reader reader = _openReader(nombre);
				ponTextoFuente(reader);
				ponNombreArchivo(nombre);
				compilarFuente();
				pwerr.println(Str.get("error.1_compiled_ok", nombre));
			}
			catch(FileNotFoundException ex)
			{
				pwerr.println(Str.get("error.1_file_not_found", nombre));
			}
			catch(CompilacionException ex)
			{
				anticompilados++;
			}
		}
		
		return anticompilados;
	}

	private static Charset charset = Charset.forName("ISO-8859-1");
	
	private static Reader _openReader(String filename) 
	throws FileNotFoundException, CompilacionException {
		FileInputStream fis = new FileInputStream(filename);
		InputStreamReader is = new InputStreamReader(fis, charset);
		return new BufferedReader(is);
	}
}
