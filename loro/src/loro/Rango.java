package loro;

//////////////////////////////////////////////////////////////////
/**
 * Representa un zona de texto fuente.
 * En particular esta información está disponible para cada nodo 
 * del árbol de derivación.
 *
 * <ul>   
 *	<li> Permite señalar el texto comprometido ante un error de compilación 
 *		o ejecución.
 *
 *	<li> En un modo de ejecución incremental, permite señalar la zona de
 *		 texto de cada paso.
 *
 * </ul>
 *
 * @author Carlos Rueda
 */
public class Rango implements java.io.Serializable
{
	/** Posición inicial del rango. */
	protected int posIni;
	
	/** Posición final del rango. */
	protected int posFin;
	
	/** Columna final. */
	protected int finCol;
	/** Linea final. */
	protected int finLin;
	/** Columna inicial. */
	protected int iniCol;
	/** Linea inicial. */
	protected int iniLin;



	//////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la posicion final del rango.
	 */
	public int obtPosFin()
	{
		return posFin;
	}
	//////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la posicion final del rango.
	 */
	public int obtPosIni()
	{
		return posIni;
	}



	//////////////////////////////////////////////////////////////////
	/**
	 * Crea un rango que cubre otros rangos.
	 *
	 * @param ini Rango inicial.
	 * @param fin Rango final.
	 */
	public Rango(Rango ini, Rango fin)
	{
		iniLin = ini.obtIniLin();
		iniCol = ini.obtIniCol();

		finLin = fin.obtFinLin();
		finCol = fin.obtFinCol();

		posIni = ini.obtPosIni();
		posFin = fin.obtPosFin();

	}

	//////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la columna final.
	 */
	public int obtFinCol()
	{
		return finCol;
	}

	//////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la columna final.
	 */
	public int obtFinLin()
	{
		return finLin;
	}

	//////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la columna inicial.
	 */
	public int obtIniCol()
	{
		return iniCol;
	}

	//////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la linea inicial.
	 */
	public int obtIniLin()
	{
		return iniLin;
	}



	//////////////////////////////////////////////////////////////////
	/**
	 * Crea un rango. 
	 * Este constructor recibe todos los atributos asociados.
	 *
	 * @param posLin Posicion inicial
	 * @param posFin Posicion final
	 * @param iniLin Línea inicial
	 * @param iniCol Columna inicial
	 * @param finLin Línea final
	 * @param finCol Columna final
	 */
	public Rango(
		int posIni,
		int posFin,
		int iniLin, int iniCol,
		int finLin, int finCol
	)
	{
		this.posIni = posIni;
		this.posFin = posFin;

		this.iniLin = iniLin;
		this.iniCol = iniCol;
		this.finLin = finLin;
		this.finCol = finCol;
	}
}